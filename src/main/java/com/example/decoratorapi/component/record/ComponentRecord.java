package com.example.decoratorapi.component.record;

import com.example.decoratorapi.component.model.Component;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ComponentRecord {
    private String id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private BigDecimal finalPrice;
    private List<String> appliedDecoratorTypes;
    private String createdAt;
    private String updatedAt;

    public static ComponentRecord fromDomain(Component component) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return ComponentRecord.builder()
                .id(component.getId())
                .name(component.getName())
                .description(component.getDescription())
                .basePrice(component.getBasePrice())
                .finalPrice(component.getFinalPrice())
                .appliedDecoratorTypes(new ArrayList<>(component.getAppliedDecoratorTypes()))
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
