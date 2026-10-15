package com.isigntech.eventhub.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDto(
@NotBlank(message = "Category name is required")
String name,

String description,

String status) {

}
