package com.example.decoratorapi.component.model;

import java.math.BigDecimal;

public class ExpressDeliveryDecorator extends AbstractDecorator {
    public ExpressDeliveryDecorator(Component component) { super(component, "EXPRESS_DELIVERY"); }

    @Override
    protected BigDecimal getAdditionalCost() { return new BigDecimal("12.00"); }

    @Override
    protected String getAdditionalDescription() { return "Express Delivery"; }
}
