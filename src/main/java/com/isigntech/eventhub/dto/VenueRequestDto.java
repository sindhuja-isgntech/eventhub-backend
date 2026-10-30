package com.isigntech.eventhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VenueRequestDto(
	
		@NotBlank(message = "Venue name is required")
	    String name,
	    String address,
	    @NotBlank(message = "City is required")
	    String city,
	    @NotNull(message = "Capacity is required")
	    @Positive(message = "Capacity must be positive")
	    Integer capacity
) {}
