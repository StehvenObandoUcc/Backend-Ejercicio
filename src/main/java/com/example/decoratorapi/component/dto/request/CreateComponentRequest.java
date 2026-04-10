package com.example.decoratorapi.component.dto.request;

import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class CreateComponentRequest {
    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name must be at most 100 characters")
    private String name;

    @NotBlank(message = "description is required")
    private String description;

    @NotNull(message = "basePrice is required")
    @DecimalMin(value = "0.01", message = "basePrice must be greater than 0")
    private BigDecimal basePrice;
}
