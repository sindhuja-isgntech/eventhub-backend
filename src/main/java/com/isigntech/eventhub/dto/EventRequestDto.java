package com.isigntech.eventhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;


public record EventRequestDto (
	
	@NotBlank(message = "Title is required")
    String title,
    
    String description,

    @NotNull(message = "Category ID is required")
    Long categoryId,

    @NotNull(message = "Venue ID is required")
    Long venueId,

    @NotNull(message = "Event date and time is required")
    LocalDateTime dateTime,

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    Double price,

    @NotNull(message = "Total seats are required")
    @Positive(message = "Total seats must be positive")
    Integer totalSeats,

    String imageUrl,
    String organizer,
    Boolean featured
) {}
