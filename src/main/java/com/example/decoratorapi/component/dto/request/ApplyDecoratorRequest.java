package com.example.decoratorapi.component.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ApplyDecoratorRequest {
    @NotBlank(message = "decoratorType is required")
    private String decoratorType;
}
