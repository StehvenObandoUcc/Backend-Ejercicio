package com.example.decoratorapi.component.model;

import java.math.BigDecimal;

public class GiftWrapDecorator extends AbstractDecorator {
    public GiftWrapDecorator(Component component) { super(component, "GIFT_WRAP"); }

    @Override
    protected BigDecimal getAdditionalCost() { return new BigDecimal("8.50"); }

    @Override
    protected String getAdditionalDescription() { return "Gift Wrapping"; }
}
