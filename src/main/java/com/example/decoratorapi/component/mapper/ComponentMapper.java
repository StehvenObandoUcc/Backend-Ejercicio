package com.example.decoratorapi.component.mapper;

import com.example.decoratorapi.component.dto.response.ComponentResponse;
import com.example.decoratorapi.component.dto.response.PriceBreakdownResponse;
import com.example.decoratorapi.component.record.ComponentRecord;
import com.example.decoratorapi.store.InMemoryDecoratorStore;
import com.example.decoratorapi.decorator.model.DecoratorDefinition;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class ComponentMapper {
    private final InMemoryDecoratorStore decoratorStore;

    public ComponentMapper(InMemoryDecoratorStore decoratorStore) {
        this.decoratorStore = decoratorStore;
    }

    public ComponentResponse toResponse(ComponentRecord record) {
        List<PriceBreakdownResponse.DecoratorCostItem> costs = new ArrayList<>();
        for (String type : record.getAppliedDecoratorTypes()) {
            BigDecimal cost = decoratorStore.findByType(type)
                    .map(DecoratorDefinition::getAdditionalCost)
                    .orElse(BigDecimal.ZERO);
            costs.add(new PriceBreakdownResponse.DecoratorCostItem(type, cost));
        }

        BigDecimal totalAdded = costs.stream().map(PriceBreakdownResponse.DecoratorCostItem::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PriceBreakdownResponse breakdown = new PriceBreakdownResponse(record.getBasePrice(), costs, totalAdded, record.getFinalPrice());

        return ComponentResponse.builder()
                .id(record.getId())
                .name(record.getName())
                .description(record.getDescription())
                .basePrice(record.getBasePrice())
                .finalPrice(record.getFinalPrice())
                .appliedDecoratorTypes(record.getAppliedDecoratorTypes())
                .priceBreakdown(breakdown)
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }

    public List<ComponentResponse> toResponseList(List<ComponentRecord> records) {
        List<ComponentResponse> list = new ArrayList<>();
        for (ComponentRecord r : records) list.add(toResponse(r));
        return list;
    }
}
