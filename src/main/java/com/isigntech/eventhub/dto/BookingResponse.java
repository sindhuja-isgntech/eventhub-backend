package com.isigntech.eventhub.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.isigntech.eventhub.entity.Attendee;
import com.isigntech.eventhub.entity.Booking;
import com.isigntech.eventhub.entity.BookingItem;
import com.isigntech.eventhub.entity.Event;
import com.isigntech.eventhub.entity.User;

public record BookingResponse(
        Long id,
        Long userId,
        String customerName,
        String customerEmail,
        Long eventId,
        String eventTitle,
        String eventCategory,
        LocalDateTime eventDateTime,
        String venueName,
        String venueCity,
        Integer ticketCount,
        String tierName,
        Double totalAmount,
        LocalDateTime bookingDate,
        String status,
        String attendeeName,
        String attendeeEmail,
        String attendeePhone,
        String qrCodeUrl
) {
    public static BookingResponse from(Booking booking) {
        List<BookingItem> items = booking.getBookingItems();
        BookingItem item = items == null ? null : items.stream().findFirst().orElse(null);
        Event event = item != null ? item.getEvent() : null;
        User user = booking.getUser();
        List<Attendee> attendees = booking.getAttendees();
        Attendee attendee = attendees == null ? null : attendees.stream().findFirst().orElse(null);
        String reference = "EH-" + booking.getId();

        return new BookingResponse(
                booking.getId(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                event != null ? event.getId() : null,
                event != null ? event.getTitle() : "Event",
                event != null && event.getCategory() != null ? event.getCategory().getName() : "General",
                event != null ? event.getDateTime() : null,
                event != null && event.getVenue() != null ? event.getVenue().getName() : "",
                event != null && event.getVenue() != null ? event.getVenue().getCity() : "",
                booking.getTicketCount(),
                "Standard Pass",
                booking.getTotalAmount(),
                booking.getBookingDate(),
                booking.getStatus(),
                attendee != null ? attendee.getName() : user.getName(),
                attendee != null ? attendee.getEmail() : user.getEmail(),
                attendee != null ? attendee.getPhone() : user.getPhone(),
                "https://api.qrserver.com/v1/create-qr-code/?size=160x160&data=" + reference
        );
    }
}
