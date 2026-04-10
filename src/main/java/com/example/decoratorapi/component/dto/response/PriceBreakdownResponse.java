package com.example.decoratorapi.component.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class PriceBreakdownResponse {
    private BigDecimal basePrice;
    private List<DecoratorCostItem> decoratorCosts;
    private BigDecimal totalAdded;
    private BigDecimal finalPrice;

    @Data
    @AllArgsConstructor
    public static class DecoratorCostItem {
        private String decoratorType;
        private BigDecimal cost;
    }
}
