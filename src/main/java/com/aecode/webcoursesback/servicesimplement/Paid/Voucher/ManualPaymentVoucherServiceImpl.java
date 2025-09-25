package com.aecode.webcoursesback.servicesimplement.Paid.Voucher;

import com.aecode.webcoursesback.dtos.Paid.Voucher.ManualPaymentVoucherDTO;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.Paid.Voucher.ManualPaymentVoucher;
import com.aecode.webcoursesback.entities.Paid.UnifiedPaidOrder;
import com.aecode.webcoursesback.entities.UserModuleAccess;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.repositories.IModuleRepo;
import com.aecode.webcoursesback.repositories.IUserModuleRepo;
import com.aecode.webcoursesback.repositories.IUserProfileRepository;
import com.aecode.webcoursesback.repositories.Paid.Voucher.ManualPaymentVoucherRepository;
import com.aecode.webcoursesback.services.Paid.Voucher.ManualPaymentVoucherService;
import com.aecode.webcoursesback.services.Paid.UnifiedPaidOrderService;
import com.aecode.webcoursesback.servicesimplement.FirebaseStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ManualPaymentVoucherServiceImpl implements ManualPaymentVoucherService {

    private final ManualPaymentVoucherRepository repo;
    private final FirebaseStorageService firebaseStorageService;
    private final IUserProfileRepository userProfileRepo;
    private final IUserModuleRepo userModuleRepo;
    private final IModuleRepo moduleRepo;
    private final UnifiedPaidOrderService unifiedPaidOrderService;

    @Override
    public ManualPaymentVoucherDTO uploadVoucher(
            MultipartFile voucherFileOrNull,
            String clerkIdOrNull,
            List<Long> moduleIdsOrNull,
            String paymentMethodOrNull,
            OffsetDateTime paidAtOrNull,
            String statusOrNull
    ) {
        // 1) Subir archivo (si llega)
        String url = null;
        if (voucherFileOrNull != null && !voucherFileOrNull.isEmpty()) {
            validateMime(voucherFileOrNull.getContentType());

            // ===== NUEVO: ruta ordenada por usuario, misma idea que en UserDetailController =====
            String path;
            String safeClerk = (clerkIdOrNull != null && !clerkIdOrNull.isBlank()) ? clerkIdOrNull : "unknown";

            if (clerkIdOrNull != null && !clerkIdOrNull.isBlank()) {
                var optUser = userProfileRepo.findByClerkId(clerkIdOrNull);
                if (optUser.isPresent()) {
                    var user = optUser.get();
                    String fullName = user.getFullname();
                    String fullNameSafe = safeName(fullName != null ? fullName : "usuario");
                    path = "users/" + user.getUserId() + "_" + fullNameSafe + "/vouchers/";
                } else {
                    // clerkId no encontrado -> fallback por clerk
                    path = "vouchers/" + safeClerk + "/";
                }
            } else {
                // sin clerkId -> carpeta genérica
                path = "vouchers/" + safeClerk + "/";
            }
            // ================================================================================

            try {
                url = firebaseStorageService.uploadImage(voucherFileOrNull, path);
            } catch (IOException e) {
                throw new RuntimeException("No se pudo subir el voucher: " + e.getMessage(), e);
            }
        }

        // 2) Normalizar CSV de módulos (permite nulo/vacío)
        String csv = toCsvDistinct(moduleIdsOrNull);

        // 3) Mapear estado (por defecto PAID)
        ManualPaymentVoucher.PaymentStatus status =
                mapStatusOrDefault(statusOrNull, ManualPaymentVoucher.PaymentStatus.PAID);

        // 4) Crear y guardar entidad
        ManualPaymentVoucher entity = ManualPaymentVoucher.builder()
                .clerkId(nullIfBlank(clerkIdOrNull))
                .voucherUrl(url)
                .moduleIdsCsv(csv)
                .paymentMethod(nullIfBlank(paymentMethodOrNull))
                .paidAt(paidAtOrNull)  // puede ser null
                .status(status)
                // validated = false en @PrePersist
                .build();

        entity = repo.save(entity);
        return map(entity);
    }

    @Override
    public ManualPaymentVoucherDTO setValidated(Long id, boolean validated) {
        ManualPaymentVoucher entity = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Voucher no encontrado: " + id));

        boolean wasValidated = entity.isValidated();
        entity.setValidated(validated);
        repo.save(entity);

        List<Long> accepted = List.of();
        List<Long> skippedOwned = List.of();
        List<Long> skippedMissing = List.of();
        String info = null;

        // Solo si cambió a TRUE recién ahora, intentamos exportar a la unificada
        if (!wasValidated && validated) {
            if (entity.getClerkId() == null || entity.getClerkId().isBlank()) {
                // No bloqueamos el cambio de estado, pero informamos que no se pudo exportar
                info = "Validado, pero sin exportar: falta clerkId en el voucher.";
            } else {
                UserProfile user = userProfileRepo.findByClerkId(entity.getClerkId())
                        .orElseThrow(() -> new EntityNotFoundException("No se puede validar: clerkId no existe en UserProfile."));

                // IDs del voucher (distintos, en el mismo orden de CSV si quieres mantenerlo)
                List<Long> requested = parseCsv(entity.getModuleIdsCsv());
                if (requested.isEmpty()) {
                    info = "Validado, pero sin exportar: el voucher no contiene módulos.";
                } else {
                    // Módulos existentes
                    List<Module> foundModules = moduleRepo.findAllById(requested);
                    Set<Long> foundIds = foundModules.stream().map(Module::getModuleId).collect(Collectors.toSet());
                    skippedMissing = requested.stream().filter(mid -> !foundIds.contains(mid)).toList();

                    // Ya comprados por el usuario
                    List<UserModuleAccess> already = userModuleRepo.findByUserProfile_ClerkId(entity.getClerkId());
                    Set<Long> alreadyOwned = already.stream()
                            .filter(a -> a.getModule() != null)
                            .map(a -> a.getModule().getModuleId())
                            .collect(Collectors.toSet());
                    skippedOwned = requested.stream().filter(alreadyOwned::contains).toList();

                    // Aceptados = solicitados - (missing ∪ yaOwned)
                    Set<Long> skipUnion = new HashSet<>(skippedMissing);
                    skipUnion.addAll(skippedOwned);
                    accepted = requested.stream().filter(mid -> !skipUnion.contains(mid)).toList();

                    if (accepted.isEmpty()) {
                        info = "Validado, pero sin exportar: todos los módulos del voucher eran inexistentes o ya poseídos.";
                    } else {
                        // Exportar SOLO aceptados a la tabla unificada
                        OffsetDateTime paidAt = Optional.ofNullable(entity.getPaidAt()).orElse(OffsetDateTime.now());
                        unifiedPaidOrderService.logPaidOrder(
                                user.getEmail(),
                                Optional.ofNullable(user.getFullname()).orElse(""),
                                paidAt,
                                accepted
                        );
                        info = buildInfoMessage(accepted, skippedOwned, skippedMissing);
                    }
                }
            }
        }

        // Respuesta con reporte (los nuevos campos van nulos para casos no aplicables)
        return ManualPaymentVoucherDTO.builder()
                .id(entity.getId())
                .clerkId(entity.getClerkId())
                .voucherUrl(entity.getVoucherUrl())
                .moduleIds(parseCsv(entity.getModuleIdsCsv()))
                .paymentMethod(entity.getPaymentMethod())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .paidAt(entity.getPaidAt())
                .validated(entity.isValidated())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .acceptedModuleIds(accepted.isEmpty() ? null : accepted)
                .skippedAlreadyOwned(skippedOwned.isEmpty() ? null : skippedOwned)
                .skippedNotFound(skippedMissing.isEmpty() ? null : skippedMissing)
                .infoMessage(info)
                .build();
    }

    private String buildInfoMessage(List<Long> accepted, List<Long> skippedOwned, List<Long> skippedMissing) {
        StringBuilder sb = new StringBuilder("Exportado a unificada: ");
        sb.append(accepted);
        if (!skippedOwned.isEmpty()) {
            sb.append(" | Omitidos (ya tenía acceso): ").append(skippedOwned);
        }
        if (!skippedMissing.isEmpty()) {
            sb.append(" | Omitidos (inexistentes): ").append(skippedMissing);
        }
        return sb.toString();
    }


    @Override
    @Transactional(readOnly = true)
    public List<ManualPaymentVoucherDTO> listAll() {
        return repo.findAll().stream().sorted(Comparator.comparing(ManualPaymentVoucher::getCreatedAt).reversed())
                .map(this::map)
                .toList();
    }

    // ===== Helpers =====

    private void validateMime(String contentType) {
        if (contentType == null) return;
        if (contentType.equalsIgnoreCase("application/pdf")) return;
        if (contentType.equalsIgnoreCase("image/jpeg")) return;
        if (contentType.equalsIgnoreCase("image/png")) return;
        if (contentType.equalsIgnoreCase("image/jpg")) return;
        throw new IllegalArgumentException("Formato no permitido. Solo PDF/JPG/PNG.");
    }

    private String toCsvDistinct(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return null;
        return ids.stream().filter(Objects::nonNull).map(String::valueOf).distinct()
                .collect(Collectors.joining(","));
    }

    private List<Long> parseCsv(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        String[] parts = csv.split(",");
        List<Long> out = new ArrayList<>();
        for (String p : parts) {
            try { out.add(Long.parseLong(p.trim())); } catch (NumberFormatException ignored) {}
        }
        return out;
    }

    private ManualPaymentVoucher.PaymentStatus mapStatusOrDefault(String s, ManualPaymentVoucher.PaymentStatus def) {
        if (s == null || s.isBlank()) return def;
        try { return ManualPaymentVoucher.PaymentStatus.valueOf(s.trim().toUpperCase()); }
        catch (IllegalArgumentException ex) { return def; }
    }

    private String nullIfBlank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private String safeName(String s) {
        return s.replaceAll("[^a-zA-Z0-9]", "_");
    }

    private ManualPaymentVoucherDTO map(ManualPaymentVoucher e) {
        return ManualPaymentVoucherDTO.builder()
                .id(e.getId())
                .clerkId(e.getClerkId())
                .voucherUrl(e.getVoucherUrl())
                .moduleIds(parseCsv(e.getModuleIdsCsv()))
                .paymentMethod(e.getPaymentMethod())
                .status(e.getStatus() != null ? e.getStatus().name() : null)
                .paidAt(e.getPaidAt())
                .validated(e.isValidated())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
