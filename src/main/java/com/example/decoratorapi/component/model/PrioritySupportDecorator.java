package com.example.decoratorapi.component.model;

import java.math.BigDecimal;

public class PrioritySupportDecorator extends AbstractDecorator {
    public PrioritySupportDecorator(Component component) { super(component, "PRIORITY_SUPPORT"); }

    @Override
    protected BigDecimal getAdditionalCost() { return new BigDecimal("25.00"); }

    @Override
    protected String getAdditionalDescription() { return "Priority Support"; }
}
