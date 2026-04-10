package com.example.decoratorapi.component.factory;

import com.example.decoratorapi.component.model.*;
import com.example.decoratorapi.shared.exception.InvalidDecoratorException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DecoratorFactory {

    public Component apply(Component base, String type) {
        return switch (type.toUpperCase()) {
            case "INSURANCE" -> new InsuranceDecorator(base);
            case "GIFT_WRAP" -> new GiftWrapDecorator(base);
            case "PRIORITY_SUPPORT" -> new PrioritySupportDecorator(base);
            case "EXPRESS_DELIVERY" -> new ExpressDeliveryDecorator(base);
            default -> throw new InvalidDecoratorException("Unknown decorator type: " + type);
        };
    }

    public Component applyAll(Component base, List<String> types) {
        Component current = base;
        for (String t : types) {
            current = apply(current, t);
        }
        return current;
    }
}
