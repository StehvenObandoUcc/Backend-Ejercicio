package com.example.decoratorapi.decorator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class DecoratorDefinition {
    private String type;
    private String displayName;
    private String description;
    private BigDecimal additionalCost;
    private boolean canBeAppliedMultipleTimes;
}
