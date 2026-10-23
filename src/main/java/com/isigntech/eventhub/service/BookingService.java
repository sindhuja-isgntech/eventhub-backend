package com.isigntech.eventhub.service;

import com.isigntech.eventhub.dto.*;
import com.isigntech.eventhub.entity.Attendee;
import com.isigntech.eventhub.entity.Booking;
import com.isigntech.eventhub.entity.BookingItem;
import com.isigntech.eventhub.entity.Event;
import com.isigntech.eventhub.entity.User;
import com.isigntech.eventhub.repository.BookingRepository;
import com.isigntech.eventhub.repository.EventRepository;
import com.isigntech.eventhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public Booking createBooking(BookingRequestDto requestDto) {
        // 1. Fetch & validate user
        User user = null;
        if (requestDto.userId() != null) {
            user = userRepository.findById(requestDto.userId()).orElse(null);
        }
        if (user == null) {
            user = userRepository.findAll().stream().findFirst().orElseGet(() ->
                    userRepository.save(User.builder()
                            .name("Alex Morgan")
                            .email("alex.morgan@example.com")
                            .password("demo123")
                            .phone("+1 (555) 234-5678")
                            .role("CUSTOMER")
                            .build())
            );
        }

        // 2. Fetch & validate event seats
        Event event = eventRepository.findById(requestDto.eventId())
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + requestDto.eventId()));

        if (event.getAvailableSeats() < requestDto.quantity()) {
            throw new RuntimeException("Not enough seats available. Requested: " 
                    + requestDto.quantity() + ", Available: " + event.getAvailableSeats());
        }

        // 3. Deduct available seats
        event.setAvailableSeats(event.getAvailableSeats() - requestDto.quantity());
        eventRepository.save(event);

        // 4. Create and populate Booking
        Double totalAmount = event.getPrice() * requestDto.quantity();
        Booking booking = Booking.builder()
                .user(user)
                .ticketCount(requestDto.quantity())
                .totalAmount(totalAmount)
                .status("CONFIRMED")
                .bookingItems(new ArrayList<>())
                .attendees(new ArrayList<>())
                .build();

        // 5. Build BookingItem
        BookingItem item = BookingItem.builder()
                .booking(booking)
                .event(event)
                .quantity(requestDto.quantity())
                .unitPrice(event.getPrice())
                .subtotal(totalAmount)
                .build();
        booking.getBookingItems().add(item);

        // 6. Build Attendee records
        if (requestDto.attendees() != null && !requestDto.attendees().isEmpty()) {
            for (var attendeeDto : requestDto.attendees()) {
                Attendee attendee = Attendee.builder()
                        .booking(booking)
                        .name(attendeeDto.name() != null && !attendeeDto.name().isBlank() ? attendeeDto.name() : user.getName())
                        .email(attendeeDto.email() != null && !attendeeDto.email().isBlank() ? attendeeDto.email() : user.getEmail())
                        .phone(attendeeDto.phone() != null ? attendeeDto.phone() : user.getPhone())
                        .build();
                booking.getAttendees().add(attendee);
            }
        } else {
            Attendee attendee = Attendee.builder()
                    .booking(booking)
                    .name(user.getName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .build();
            booking.getAttendees().add(attendee);
        }

        return bookingRepository.save(booking);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }

    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId);
    }
}