package com.aecode.webcoursesback.servicesimplement.Paid.Voucher;

import com.aecode.webcoursesback.dtos.Paid.Voucher.ManualPaymentVoucherDTO;
import com.aecode.webcoursesback.dtos.Paid.Voucher.ManualPaymentVoucherPayload;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.Paid.Voucher.ManualPaymentVoucher;
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
import java.util.stream.Stream;

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
    public ManualPaymentVoucherDTO uploadVoucher(MultipartFile voucherFileOrNull,
                                                 ManualPaymentVoucherPayload payload) {

        // 0) defensivo
        if (payload == null) {
            payload = new ManualPaymentVoucherPayload();
        }

        // 1) Subir archivo (si llega)
        String url = null;
        if (voucherFileOrNull != null && !voucherFileOrNull.isEmpty()) {
            validateMime(voucherFileOrNull.getContentType());

            String clerkId = trimToNull(payload.getClerkId());
            String path;
            String safeClerk = (clerkId != null) ? clerkId : "unknown";

            if (clerkId != null) {
                var optUser = userProfileRepo.findByClerkId(clerkId);
                if (optUser.isPresent()) {
                    var user = optUser.get();
                    String fullNameSafe = safeName(nvl(user.getFullname(), "usuario"));
                    path = "users/" + user.getUserId() + "_" + fullNameSafe + "/vouchers/";
                } else {
                    path = "vouchers/" + safeClerk + "/";
                }
            } else {
                path = "vouchers/" + safeClerk + "/";
            }

            try {
                url = firebaseStorageService.uploadImage(voucherFileOrNull, path);
            } catch (IOException e) {
                throw new RuntimeException("No se pudo subir el voucher: " + e.getMessage(), e);
            }
        }

        // 2) Derivar email desde clerkId (si existe el usuario)
        String clerkId = trimToNull(payload.getClerkId());
        String resolvedEmail = null;
        if (clerkId != null) {
            userProfileRepo.findByClerkId(clerkId).ifPresent(u -> {
                // usamos siempre el email del usuario si existe
            });
            Optional<UserProfile> opt = userProfileRepo.findByClerkId(clerkId);
            if (opt.isPresent()) {
                resolvedEmail = opt.get().getEmail();
            }
        }
        if (resolvedEmail == null) {
            // si no hay user por clerkId, usamos el email que vino en el payload (si vino)
            resolvedEmail = trimToNull(payload.getEmail());
        }

        // 3) Normalizar CSV de módulos (permite nulo/vacío)
        String csv = toCsvDistinct(payload.getModuleIds());

        // 4) Mapear status (default PAID en @PrePersist)
        ManualPaymentVoucher.PaymentStatus status =
                mapStatusOrDefault(payload.getStatus(), ManualPaymentVoucher.PaymentStatus.PAID);

        // 5) Parsear domain
        ManualPaymentVoucher.PaymentDomain domain = parseDomainOrNull(payload.getDomain());

        // 6) Construir y guardar la entidad (nada obligatorio)
        ManualPaymentVoucher entity = ManualPaymentVoucher.builder()
                .clerkId(clerkId)
                .voucherUrl(url)
                .moduleIdsCsv(csv)
                .paymentMethod(trimToNull(payload.getPaymentMethod()))
                .paidAt(payload.getPaidAt())   // puede ser null
                .status(status)

                // nuevos
                .email(resolvedEmail)
                .orderId(trimToNull(payload.getOrderId()))
                .amountCents(payload.getAmountCents())
                .currency(upperOrNull(payload.getCurrency()))
                .domain(domain)
                .landingSlug(trimToNull(payload.getLandingSlug()))
                .landingPlanKey(trimToNull(payload.getLandingPlanKey()))
                .landingQuantity(payload.getLandingQuantity())
                .landingModality(payload.getLandingModality())

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

        // Solo si cambió a TRUE recién ahora
        if (!wasValidated && validated) {
            // EVENT => no exporta módulos
            if (entity.getDomain() == ManualPaymentVoucher.PaymentDomain.EVENT) {
                info = "Validado (EVENT). No se exportan módulos. Datos: slug="
                        + nvl(entity.getLandingSlug(), "-")
                        + ", plan=" + nvl(entity.getLandingPlanKey(), "-")
                        + ", qty=" + String.valueOf(entity.getLandingQuantity());
            } else {
                // MODULES (o null)
                if (isBlank(entity.getClerkId())) {
                    info = "Validado, pero sin exportar: falta clerkId en el voucher.";
                } else {
                    UserProfile user = userProfileRepo.findByClerkId(entity.getClerkId())
                            .orElseThrow(() -> new EntityNotFoundException("No se puede validar: clerkId no existe en UserProfile."));

                    List<Long> requested = parseCsv(entity.getModuleIdsCsv());
                    if (requested.isEmpty()) {
                        info = "Validado, pero sin exportar: el voucher no contiene módulos.";
                    } else {
                        List<Module> foundModules = moduleRepo.findAllById(requested);
                        Set<Long> foundIds = foundModules.stream().map(Module::getModuleId).collect(Collectors.toSet());
                        skippedMissing = requested.stream().filter(mid -> !foundIds.contains(mid)).toList();

                        List<UserModuleAccess> already = userModuleRepo.findByUserProfile_ClerkId(entity.getClerkId());
                        Set<Long> alreadyOwned = already.stream()
                                .filter(a -> a.getModule() != null)
                                .map(a -> a.getModule().getModuleId())
                                .collect(Collectors.toSet());
                        skippedOwned = requested.stream().filter(alreadyOwned::contains).toList();

                        Set<Long> skipUnion = new HashSet<>(skippedMissing);
                        skipUnion.addAll(skippedOwned);
                        accepted = requested.stream().filter(mid -> !skipUnion.contains(mid)).toList();

                        if (accepted.isEmpty()) {
                            info = "Validado, pero sin exportar: todos los módulos eran inexistentes o ya poseídos.";
                        } else {
                            OffsetDateTime paidAt = Optional.ofNullable(entity.getPaidAt()).orElse(OffsetDateTime.now());
                            String emailToLog = nvl(entity.getEmail(), user.getEmail());
                            unifiedPaidOrderService.logPaidOrder(
                                    emailToLog,
                                    nvl(user.getFullname(), ""),
                                    paidAt,
                                    accepted
                            );
                            info = buildInfoMessage(accepted, skippedOwned, skippedMissing);
                        }
                    }
                }
            }
        }

        return ManualPaymentVoucherDTO.builder()
                .id(entity.getId())
                .clerkId(entity.getClerkId())
                .voucherUrl(entity.getVoucherUrl())
                .moduleIds(parseCsv(entity.getModuleIdsCsv()))
                .paymentMethod(entity.getPaymentMethod())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .paidAt(entity.getPaidAt())

                .email(entity.getEmail())
                .orderId(entity.getOrderId())
                .amountCents(entity.getAmountCents())
                .currency(entity.getCurrency())
                .domain(entity.getDomain() != null ? entity.getDomain().name() : null)

                .landingSlug(entity.getLandingSlug())
                .landingPlanKey(entity.getLandingPlanKey())
                .landingQuantity(entity.getLandingQuantity())
                .landingModality(entity.getLandingModality())

                .validated(entity.isValidated())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())

                .acceptedModuleIds(accepted.isEmpty() ? null : accepted)
                .skippedAlreadyOwned(skippedOwned.isEmpty() ? null : skippedOwned)
                .skippedNotFound(skippedMissing.isEmpty() ? null : skippedMissing)
                .infoMessage(info)
                .build();
    }



    @Transactional(readOnly = true)
    @Override
    public List<ManualPaymentVoucherDTO> listAll() {
        return repo.findAll().stream()
                .sorted(Comparator.comparing(ManualPaymentVoucher::getCreatedAt).reversed())
                .map(this::map)
                .toList();
    }


    @Transactional(readOnly = true)
    @Override
    public List<ManualPaymentVoucherDTO> listEvents() {
        return repo.findByDomainOrderByCreatedAtDesc(ManualPaymentVoucher.PaymentDomain.EVENT)
                .stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ManualPaymentVoucherDTO> listModules(boolean includeLegacyNull) {
        Stream<ManualPaymentVoucher> base = repo
                .findByDomainOrderByCreatedAtDesc(ManualPaymentVoucher.PaymentDomain.MODULES)
                .stream();
        if (includeLegacyNull) {
            var legacy = repo.findByDomainIsNullOrderByCreatedAtDesc();
            base = Stream.concat(base, legacy.stream())
                    .sorted(Comparator.comparing(ManualPaymentVoucher::getCreatedAt).reversed());
        }
        return base.map(this::map).toList();
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
        if (isBlank(s)) return def;
        try { return ManualPaymentVoucher.PaymentStatus.valueOf(s.trim().toUpperCase()); }
        catch (IllegalArgumentException ex) { return def; }
    }

    private ManualPaymentVoucher.PaymentDomain parseDomainOrNull(String s) {
        if (isBlank(s)) return null;
        try { return ManualPaymentVoucher.PaymentDomain.valueOf(s.trim().toUpperCase()); }
        catch (IllegalArgumentException ex) { return null; }
    }

    private String trimToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
    private boolean isBlank(String s) { return s == null || s.isBlank(); }
    private String upperOrNull(String s) { return isBlank(s) ? null : s.trim().toUpperCase(); }
    private String nvl(String s, String def) { return isBlank(s) ? def : s; }
    private String safeName(String s) { return s.replaceAll("[^a-zA-Z0-9]", "_"); }

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

    private ManualPaymentVoucherDTO map(ManualPaymentVoucher e) {
        return ManualPaymentVoucherDTO.builder()
                .id(e.getId())
                .clerkId(e.getClerkId())
                .voucherUrl(e.getVoucherUrl())
                .moduleIds(parseCsv(e.getModuleIdsCsv()))
                .paymentMethod(e.getPaymentMethod())
                .status(e.getStatus() != null ? e.getStatus().name() : null)
                .paidAt(e.getPaidAt())

                .email(e.getEmail())
                .orderId(e.getOrderId())
                .amountCents(e.getAmountCents())
                .currency(e.getCurrency())
                .domain(e.getDomain() != null ? e.getDomain().name() : null)

                .landingSlug(e.getLandingSlug())
                .landingPlanKey(e.getLandingPlanKey())
                .landingQuantity(e.getLandingQuantity())
                .landingModality(e.getLandingModality())

                .validated(e.isValidated())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
