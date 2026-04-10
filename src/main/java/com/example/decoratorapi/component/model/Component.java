package com.example.decoratorapi.component.model;

import java.math.BigDecimal;
import java.util.List;

public interface Component {
    String getId();
    String getName();
    String getDescription();
    BigDecimal getBasePrice();
    BigDecimal getFinalPrice();
    List<String> getAppliedDecoratorTypes();
}
