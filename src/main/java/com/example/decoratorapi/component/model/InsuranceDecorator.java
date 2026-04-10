package com.example.decoratorapi.component.model;

import java.math.BigDecimal;

public class InsuranceDecorator extends AbstractDecorator {
    public InsuranceDecorator(Component component) { super(component, "INSURANCE"); }

    @Override
    protected BigDecimal getAdditionalCost() { return new BigDecimal("15.00"); }

    @Override
    protected String getAdditionalDescription() { return "Insurance Coverage"; }
}
