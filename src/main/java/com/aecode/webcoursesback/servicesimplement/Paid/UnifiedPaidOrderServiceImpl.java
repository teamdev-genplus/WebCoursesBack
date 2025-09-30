package com.aecode.webcoursesback.servicesimplement.Paid;
import com.aecode.webcoursesback.dtos.Paid.UnifiedPaidOrderDTO;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.Paid.UnifiedPaidOrder;
import com.aecode.webcoursesback.repositories.IModuleRepo;
import com.aecode.webcoursesback.repositories.Paid.UnifiedPaidOrderRepository;
import com.aecode.webcoursesback.services.Paid.UnifiedPaidOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnifiedPaidOrderServiceImpl implements UnifiedPaidOrderService{

    private final UnifiedPaidOrderRepository repo;
    private final IModuleRepo moduleRepo;

    @Override
    public UnifiedPaidOrderDTO logPaidOrder(String email, String fullName, OffsetDateTime paidAt, List<Long> moduleIds) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email requerido");
        if (fullName == null) fullName = "";
        if (paidAt == null) paidAt = OffsetDateTime.now();
        if (moduleIds == null || moduleIds.isEmpty()) throw new IllegalArgumentException("moduleIds requerido");

        // Dedupe + orden estable (para comparar duplicados)
        String csv = moduleIds.stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .distinct()
                .sorted(Comparator.comparingLong(Long::parseLong))
                .collect(Collectors.joining(","));

        // Si ya existe un registro idéntico (email + paidAt + csv), no reinsertar
        Optional<UnifiedPaidOrder> existing = repo.findByEmailAndPaidAtAndModuleIdsCsv(email, paidAt, csv);
        if (existing.isPresent()) {
            return map(existing.get());
        }

        UnifiedPaidOrder entity = UnifiedPaidOrder.builder()
                .email(email)
                .fullName(fullName)
                .status(UnifiedPaidOrder.PaymentStatus.PAID)
                .paidAt(paidAt)
                .moduleIdsCsv(csv)
                .build();

        entity = repo.save(entity);
        return map(entity);
    }

    @Override
    public List<UnifiedPaidOrderDTO> getAll() {
        return repo.findAllByOrderByPaidAtDesc().stream()
                .map(this::map)
                .toList();
    }

    // ===== helpers =====

    private UnifiedPaidOrderDTO map(UnifiedPaidOrder e) {
        List<Long> ids = parseCsv(e.getModuleIdsCsv());
        List<Module> modules = ids.isEmpty() ? List.of() : moduleRepo.findAllById(ids);
        Map<Long, String> titleById = modules.stream()
                .collect(Collectors.toMap(Module::getModuleId,
                        m -> Optional.ofNullable(m.getProgramTitle()).orElse("Módulo " + m.getModuleId())));

        List<String> titles = ids.stream().map(id -> titleById.getOrDefault(id, "Módulo " + id)).toList();

        return UnifiedPaidOrderDTO.builder()
                .id(e.getId())
                .email(e.getEmail())
                .fullName(e.getFullName())
                .status(e.getStatus().name())
                .paidAt(e.getPaidAt())
                .moduleIds(ids)
                .moduleTitles(titles)
                .build();
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
}
