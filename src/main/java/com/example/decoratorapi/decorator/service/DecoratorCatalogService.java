package com.example.decoratorapi.decorator.service;

import com.example.decoratorapi.decorator.model.DecoratorDefinition;
import com.example.decoratorapi.shared.exception.ResourceNotFoundException;
import com.example.decoratorapi.store.InMemoryDecoratorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DecoratorCatalogService {
    private final InMemoryDecoratorStore decoratorStore;

    public DecoratorCatalogService(InMemoryDecoratorStore decoratorStore) {
        this.decoratorStore = decoratorStore;
    }

    public List<DecoratorDefinition> getAll() {
        return decoratorStore.findAll();
    }

    public DecoratorDefinition getByType(String type) {
        return decoratorStore.findByType(type)
                .orElseThrow(() -> new ResourceNotFoundException("Decorator not found with type: " + type));
    }
}
