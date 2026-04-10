package com.example.decoratorapi.component.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ComponentResponse {
    private String id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private BigDecimal finalPrice;
    private List<String> appliedDecoratorTypes;
    private PriceBreakdownResponse priceBreakdown;
    private String createdAt;
    private String updatedAt;
}
