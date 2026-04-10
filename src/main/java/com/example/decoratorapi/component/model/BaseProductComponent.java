package com.example.decoratorapi.component.model;

import lombok.AccessLevel;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
public class BaseProductComponent implements Component {
    private final String id;
    private final String name;
    @Getter(AccessLevel.NONE)
    private final String description;
    private final BigDecimal basePrice;

    public BaseProductComponent(String id, String name, String description, BigDecimal basePrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public BigDecimal getFinalPrice() {
        return this.basePrice;
    }

    @Override
    public List<String> getAppliedDecoratorTypes() {
        return new ArrayList<>();
    }
}
