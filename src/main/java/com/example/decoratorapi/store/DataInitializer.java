package com.example.decoratorapi.store;

import com.example.decoratorapi.decorator.model.DecoratorDefinition;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;

@Component
public class DataInitializer {
    private final InMemoryDecoratorStore decoratorStore;

    public DataInitializer(InMemoryDecoratorStore decoratorStore) {
        this.decoratorStore = decoratorStore;
    }

    @PostConstruct
    public void init() {
        decoratorStore.add(DecoratorDefinition.builder()
                .type("INSURANCE")
                .displayName("Insurance Coverage")
                .description("Adds insurance protection to your product")
                .additionalCost(new BigDecimal("15.00"))
                .canBeAppliedMultipleTimes(false)
                .build());

        decoratorStore.add(DecoratorDefinition.builder()
                .type("GIFT_WRAP")
                .displayName("Gift Wrapping")
                .description("Wraps the product in premium gift packaging")
                .additionalCost(new BigDecimal("8.50"))
                .canBeAppliedMultipleTimes(false)
                .build());

        decoratorStore.add(DecoratorDefinition.builder()
                .type("PRIORITY_SUPPORT")
                .displayName("Priority Support")
                .description("Adds 24/7 priority customer support")
                .additionalCost(new BigDecimal("25.00"))
                .canBeAppliedMultipleTimes(false)
                .build());

        decoratorStore.add(DecoratorDefinition.builder()
                .type("EXPRESS_DELIVERY")
                .displayName("Express Delivery")
                .description("Delivers the product within 24 hours")
                .additionalCost(new BigDecimal("12.00"))
                .canBeAppliedMultipleTimes(false)
                .build());
    }
}
