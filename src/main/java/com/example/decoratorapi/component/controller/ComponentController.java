package com.example.decoratorapi.component.controller;

import com.example.decoratorapi.component.dto.request.ApplyDecoratorRequest;
import com.example.decoratorapi.component.dto.request.CreateComponentRequest;
import com.example.decoratorapi.component.service.ComponentService;
import com.example.decoratorapi.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/components")
@Validated
public class ComponentController {
    private final ComponentService componentService;

    public ComponentController(ComponentService componentService) {
        this.componentService = componentService;
    }

    @Operation(summary = "Create a new base component")
    @PostMapping
    public ResponseEntity<ApiResponse<com.example.decoratorapi.component.dto.response.ComponentResponse>> createComponent(@Valid @RequestBody CreateComponentRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.ok("Component created", componentService.createComponent(request)));
    }

    @Operation(summary = "List all components")
    @GetMapping
    public ResponseEntity<ApiResponse<List<com.example.decoratorapi.component.dto.response.ComponentResponse>>> getAllComponents() {
        return ResponseEntity.ok(ApiResponse.ok("Components retrieved", componentService.getAllComponents()));
    }

    @Operation(summary = "Get component by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<com.example.decoratorapi.component.dto.response.ComponentResponse>> getComponent(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Component retrieved", componentService.getComponent(id)));
    }

    @Operation(summary = "Apply decorator to component")
    @PostMapping("/{id}/decorators")
    public ResponseEntity<ApiResponse<com.example.decoratorapi.component.dto.response.ComponentResponse>> applyDecorator(@PathVariable String id, @Valid @RequestBody ApplyDecoratorRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Decorator applied", componentService.applyDecorator(id, request)));
    }

    @Operation(summary = "Remove decorator from component")
    @DeleteMapping("/{id}/decorators/{decoratorType}")
    public ResponseEntity<ApiResponse<com.example.decoratorapi.component.dto.response.ComponentResponse>> removeDecorator(@PathVariable String id, @PathVariable String decoratorType) {
        return ResponseEntity.ok(ApiResponse.ok("Decorator removed", componentService.removeDecorator(id, decoratorType)));
    }

    @Operation(summary = "Delete component")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComponent(@PathVariable String id) {
        componentService.deleteComponent(id);
        return ResponseEntity.ok(ApiResponse.ok("Component deleted", null));
    }

    public static class SimulateRequest {
        @javax.validation.constraints.NotBlank
        public String name;
        @javax.validation.constraints.NotBlank
        public String description;
        @javax.validation.constraints.NotNull
        public java.math.BigDecimal basePrice;
        public List<String> decoratorTypes;
    }

    @Operation(summary = "Simulate composition without saving")
    @PostMapping("/simulate")
    public ResponseEntity<ApiResponse<com.example.decoratorapi.component.dto.response.ComponentResponse>> simulateComposition(@RequestBody SimulateRequest body) {
        CreateComponentRequest req = new CreateComponentRequest();
        req.setName(body.name);
        req.setDescription(body.description);
        req.setBasePrice(body.basePrice);
        return ResponseEntity.ok(ApiResponse.ok("Simulation result", componentService.simulateComposition(req, body.decoratorTypes)));
    }
}
