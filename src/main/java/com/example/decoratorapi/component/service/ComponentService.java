package com.example.decoratorapi.component.service;

import com.example.decoratorapi.component.dto.request.ApplyDecoratorRequest;
import com.example.decoratorapi.component.dto.request.CreateComponentRequest;
import com.example.decoratorapi.component.record.ComponentRecord;
import com.example.decoratorapi.component.mapper.ComponentMapper;
import com.example.decoratorapi.component.model.BaseProductComponent;
import com.example.decoratorapi.component.model.Component;
import com.example.decoratorapi.component.factory.DecoratorFactory;
import com.example.decoratorapi.shared.exception.InvalidDecoratorException;
import com.example.decoratorapi.shared.exception.ResourceNotFoundException;
import com.example.decoratorapi.store.InMemoryComponentStore;
import com.example.decoratorapi.store.InMemoryDecoratorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ComponentService {
    private final InMemoryComponentStore componentStore;
    private final InMemoryDecoratorStore decoratorStore;
    private final DecoratorFactory decoratorFactory;
    private final ComponentMapper componentMapper;

    public ComponentService(InMemoryComponentStore componentStore, InMemoryDecoratorStore decoratorStore, DecoratorFactory decoratorFactory, ComponentMapper componentMapper) {
        this.componentStore = componentStore;
        this.decoratorStore = decoratorStore;
        this.decoratorFactory = decoratorFactory;
        this.componentMapper = componentMapper;
    }

    public com.example.decoratorapi.component.dto.response.ComponentResponse createComponent(CreateComponentRequest request) {
        Component base = new BaseProductComponent(UUID.randomUUID().toString(), request.getName(), request.getDescription(), request.getBasePrice());
        ComponentRecord record = ComponentRecord.fromDomain(base);
        componentStore.save(record);
        return componentMapper.toResponse(record);
    }

    public com.example.decoratorapi.component.dto.response.ComponentResponse getComponent(String id) {
        ComponentRecord r = componentStore.findById(id).orElseThrow(() -> new ResourceNotFoundException("Component not found with id: " + id));
        return componentMapper.toResponse(r);
    }

    public List<com.example.decoratorapi.component.dto.response.ComponentResponse> getAllComponents() {
        return componentMapper.toResponseList(componentStore.findAll());
    }

    public com.example.decoratorapi.component.dto.response.ComponentResponse applyDecorator(String componentId, ApplyDecoratorRequest request) {
        ComponentRecord record = componentStore.findById(componentId).orElseThrow(() -> new ResourceNotFoundException("Component not found with id: " + componentId));
        if (!decoratorStore.exists(request.getDecoratorType())) throw new InvalidDecoratorException("Unknown decorator type: " + request.getDecoratorType());

        Component rebuilt = rebuildChain(record);
        Component decorated = decoratorFactory.apply(rebuilt, request.getDecoratorType());
        ComponentRecord updated = ComponentRecord.fromDomain(decorated);
        updated.setCreatedAt(record.getCreatedAt());
        componentStore.save(updated);
        return componentMapper.toResponse(updated);
    }

    public com.example.decoratorapi.component.dto.response.ComponentResponse removeDecorator(String componentId, String decoratorType) {
        ComponentRecord record = componentStore.findById(componentId).orElseThrow(() -> new ResourceNotFoundException("Component not found with id: " + componentId));
        String typeUpper = decoratorType.toUpperCase();
        if (!record.getAppliedDecoratorTypes().contains(typeUpper)) throw new InvalidDecoratorException("Decorator " + typeUpper + " not found on component");
        List<String> remaining = new ArrayList<>(record.getAppliedDecoratorTypes());
        remaining.remove(typeUpper);
        // use the stored description when rebuilding after removal (was incorrectly using name)
        Component rebuilt = decoratorFactory.applyAll(new BaseProductComponent(record.getId(), record.getName(), record.getDescription(), record.getBasePrice()), remaining);
        ComponentRecord updated = ComponentRecord.fromDomain(rebuilt);
        updated.setCreatedAt(record.getCreatedAt());
        componentStore.save(updated);
        return componentMapper.toResponse(updated);
    }

    public void deleteComponent(String id) {
        componentStore.deleteById(id);
    }

    public com.example.decoratorapi.component.dto.response.ComponentResponse simulateComposition(CreateComponentRequest request, List<String> decoratorTypes) {
        Component base = new BaseProductComponent(UUID.randomUUID().toString(), request.getName(), request.getDescription(), request.getBasePrice());
        Component composed = decoratorFactory.applyAll(base, decoratorTypes == null ? List.of() : decoratorTypes);
        return componentMapper.toResponse(ComponentRecord.fromDomain(composed));
    }

    private Component rebuildChain(ComponentRecord record) {
        Component base = new BaseProductComponent(record.getId(), record.getName(), record.getName(), record.getBasePrice());
        return decoratorFactory.applyAll(base, record.getAppliedDecoratorTypes());
    }
}
