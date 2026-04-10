package com.example.decoratorapi.store;

import com.example.decoratorapi.decorator.model.DecoratorDefinition;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InMemoryDecoratorStore {
    private final Map<String, DecoratorDefinition> catalog = new LinkedHashMap<>();

    public void add(DecoratorDefinition def) {
        catalog.put(def.getType().toUpperCase(), def);
    }

    public Optional<DecoratorDefinition> findByType(String type) {
        if (type == null) return Optional.empty();
        return Optional.ofNullable(catalog.get(type.toUpperCase()));
    }

    public List<DecoratorDefinition> findAll() {
        return catalog.values().stream().collect(Collectors.toList());
    }

    public boolean exists(String type) {
        if (type == null) return false;
        return catalog.containsKey(type.toUpperCase());
    }
}
