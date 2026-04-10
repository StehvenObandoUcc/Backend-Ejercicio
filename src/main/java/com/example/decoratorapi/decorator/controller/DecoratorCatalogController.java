package com.example.decoratorapi.decorator.controller;

import com.example.decoratorapi.decorator.model.DecoratorDefinition;
import com.example.decoratorapi.decorator.service.DecoratorCatalogService;
import com.example.decoratorapi.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/decorators")
public class DecoratorCatalogController {
    private final DecoratorCatalogService decoratorCatalogService;

    public DecoratorCatalogController(DecoratorCatalogService decoratorCatalogService) {
        this.decoratorCatalogService = decoratorCatalogService;
    }

    @Operation(summary = "List all available decorators")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DecoratorDefinition>>> listDecorators() {
        return ResponseEntity.ok(ApiResponse.ok("Decorators retrieved", decoratorCatalogService.getAll()));
    }

    @Operation(summary = "Get decorator by type")
    @GetMapping("/{type}")
    public ResponseEntity<ApiResponse<DecoratorDefinition>> getDecorator(@PathVariable String type) {
        return ResponseEntity.ok(ApiResponse.ok("Decorator retrieved", decoratorCatalogService.getByType(type)));
    }
}
