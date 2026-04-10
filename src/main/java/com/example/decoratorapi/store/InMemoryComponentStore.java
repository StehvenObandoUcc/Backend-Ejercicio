package com.example.decoratorapi.store;

import com.example.decoratorapi.component.record.ComponentRecord;
import com.example.decoratorapi.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryComponentStore {
    private final Map<String, ComponentRecord> storage = new ConcurrentHashMap<>();

    public ComponentRecord save(ComponentRecord record) {
        storage.put(record.getId(), record);
        return record;
    }

    public Optional<ComponentRecord> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<ComponentRecord> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void deleteById(String id) {
        if (!storage.containsKey(id)) throw new ResourceNotFoundException("Component not found with id: " + id);
        storage.remove(id);
    }

    public boolean existsById(String id) { return storage.containsKey(id); }

    public int count() { return storage.size(); }
}
