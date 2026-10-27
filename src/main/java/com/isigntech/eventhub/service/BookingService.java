package com.isigntech.eventhub.service;

import com.isigntech.eventhub.dto.*;
import com.isigntech.eventhub.entity.Attendee;
import com.isigntech.eventhub.entity.Booking;
import com.isigntech.eventhub.entity.BookingItem;
import com.isigntech.eventhub.entity.Event;
import com.isigntech.eventhub.entity.User;
import com.isigntech.eventhub.exception.AuthException;
import com.isigntech.eventhub.repository.BookingRepository;
import com.isigntech.eventhub.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;

    @Transactional
    public BookingResponse createBooking(BookingRequestDto requestDto, User user) {
        Event event = eventRepository.findById(requestDto.eventId())
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + requestDto.eventId()));

        if (event.getAvailableSeats() < requestDto.quantity()) {
            throw new RuntimeException("Not enough seats available. Requested: " 
                    + requestDto.quantity() + ", Available: " + event.getAvailableSeats());
        }

        event.setAvailableSeats(event.getAvailableSeats() - requestDto.quantity());
        eventRepository.save(event);

        Double totalAmount = event.getPrice() * requestDto.quantity();
        Booking booking = Booking.builder()
                .user(user)
                .ticketCount(requestDto.quantity())
                .totalAmount(totalAmount)
                .status("CONFIRMED")
                .bookingItems(new ArrayList<>())
                .attendees(new ArrayList<>())
                .build();

        BookingItem item = BookingItem.builder()
                .booking(booking)
                .event(event)
                .quantity(requestDto.quantity())
                .unitPrice(event.getPrice())
                .subtotal(totalAmount)
                .build();
        booking.getBookingItems().add(item);

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

        return BookingResponse.from(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsForUser(User user) {
        return bookingRepository.findByUser_IdOrderByBookingDateDesc(user.getId())
                .stream()
                .map(BookingResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAllByOrderByBookingDateDesc()
                .stream()
                .map(BookingResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id, User currentUser, boolean isAdmin) {
        Booking booking = findBooking(id);
        ensureOwnerOrAdmin(booking, currentUser, isAdmin);
        return BookingResponse.from(booking);
    }

    @Transactional
    public BookingResponse cancelBooking(Long id, User currentUser, boolean isAdmin) {
        Booking booking = findBooking(id);
        ensureOwnerOrAdmin(booking, currentUser, isAdmin);
        if ("CONFIRMED".equals(booking.getStatus())) {
            updateStatus(booking, "CANCELLED");
        }
        return BookingResponse.from(booking);
    }

    @Transactional
    public BookingResponse updateBookingStatus(Long id, String requestedStatus) {
        String status = requestedStatus.toUpperCase(Locale.ROOT);
        if (!List.of("CONFIRMED", "CANCELLED", "ATTENDED").contains(status)) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "Booking status must be CONFIRMED, CANCELLED, or ATTENDED.");
        }
        Booking booking = findBooking(id);
        updateStatus(booking, status);
        return BookingResponse.from(bookingRepository.save(booking));
    }

    private Booking findBooking(Long id) {
        return bookingRepository.findWithDetailsById(id)
                .orElseThrow(() -> new AuthException(HttpStatus.NOT_FOUND, "Booking not found."));
    }

    private void ensureOwnerOrAdmin(Booking booking, User currentUser, boolean isAdmin) {
        if (!isAdmin && !booking.getUser().getId().equals(currentUser.getId())) {
            throw new AuthException(HttpStatus.NOT_FOUND, "Booking not found.");
        }
    }

    private void updateStatus(Booking booking, String status) {
        String previousStatus = booking.getStatus();
        if (previousStatus.equals(status)) {
            return;
        }

        BookingItem item = booking.getBookingItems().stream().findFirst()
                .orElseThrow(() -> new AuthException(HttpStatus.CONFLICT, "Booking has no event item."));
        Event event = item.getEvent();
        if ("CONFIRMED".equals(previousStatus) && "CANCELLED".equals(status)) {
            event.setAvailableSeats(event.getAvailableSeats() + booking.getTicketCount());
            eventRepository.save(event);
        } else if ("CANCELLED".equals(previousStatus) && "CONFIRMED".equals(status)) {
            if (event.getAvailableSeats() < booking.getTicketCount()) {
                throw new AuthException(HttpStatus.CONFLICT, "There are not enough available seats to confirm this booking.");
            }
            event.setAvailableSeats(event.getAvailableSeats() - booking.getTicketCount());
            eventRepository.save(event);
        }
        booking.setStatus(status);
    }
}