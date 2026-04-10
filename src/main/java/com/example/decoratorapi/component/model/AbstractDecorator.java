package com.example.decoratorapi.component.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDecorator implements Component {
    protected final Component wrappedComponent;
    protected final String decoratorType;

    protected AbstractDecorator(Component wrappedComponent, String decoratorType) {
        this.wrappedComponent = wrappedComponent;
        this.decoratorType = decoratorType;
    }

    @Override
    public String getId() { return wrappedComponent.getId(); }

    @Override
    public String getName() { return wrappedComponent.getName(); }

    @Override
    public BigDecimal getBasePrice() { return wrappedComponent.getBasePrice(); }

    @Override
    public BigDecimal getFinalPrice() { return wrappedComponent.getFinalPrice().add(getAdditionalCost()); }

    @Override
    public List<String> getAppliedDecoratorTypes() {
        List<String> list = new ArrayList<>(wrappedComponent.getAppliedDecoratorTypes());
        list.add(this.decoratorType);
        return list;
    }

    @Override
    public String getDescription() {
        return wrappedComponent.getDescription() + " + " + getAdditionalDescription();
    }

    protected abstract BigDecimal getAdditionalCost();
    protected abstract String getAdditionalDescription();
}
