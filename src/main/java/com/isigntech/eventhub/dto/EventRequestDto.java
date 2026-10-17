package com.isigntech.eventhub.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;


public record EventRequestDto (
	
	@NotBlank(message = "Title is required")
    String title,
    
    String description,

    @NotNull(message = "Category ID is required")
    Long categoryId,

    @NotNull(message = "Venue ID is required")
    Long venueId,

    @Future(message = "Event date and time must be in the future")
    @NotNull(message = "Event date and time is required")
    LocalDateTime dateTime,

    @Positive(message = "Price must be greater than zero")
    Double price,

    @Positive(message = "Total seats must be positive")
    Integer totalSeats,

    String imageUrl,
    String organizer,
    Boolean featured
) {}

