package com.example.decoratorapi.component.dto.request;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
