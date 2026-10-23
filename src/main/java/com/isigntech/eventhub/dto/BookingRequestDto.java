package com.isigntech.eventhub.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BookingRequestDto (
	
    Long userId,

    @NotNull(message = "Event ID is required")
    Long eventId,

    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity,

    List<AttendeeDto> attendees
) {
    public record AttendeeDto(
        String name,
        String email,
        String phone
    ) {}

}
