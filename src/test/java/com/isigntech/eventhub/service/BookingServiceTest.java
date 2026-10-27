package com.isigntech.eventhub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.isigntech.eventhub.entity.Booking;
import com.isigntech.eventhub.entity.User;
import com.isigntech.eventhub.exception.AuthException;
import com.isigntech.eventhub.repository.BookingRepository;
import com.isigntech.eventhub.repository.EventRepository;

class BookingServiceTest {
    private BookingRepository bookingRepository;
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingService(bookingRepository, mock(EventRepository.class));
    }

    @Test
    void customerCannotReadAnotherUsersBooking() {
        User owner = User.builder().id(21L).name("Owner").email("owner@example.com").build();
        User otherUser = User.builder().id(22L).name("Other").email("other@example.com").build();
        Booking booking = Booking.builder().id(8L).user(owner).build();
        when(bookingRepository.findWithDetailsById(8L)).thenReturn(Optional.of(booking));

        AuthException exception = assertThrows(
                AuthException.class,
                () -> bookingService.getBookingById(8L, otherUser, false));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void adminsCanReadBookingsTheyDoNotOwn() {
        User owner = User.builder().id(21L).name("Owner").email("owner@example.com").build();
        User admin = User.builder().id(23L).name("Admin").role("ADMIN").build();
        Booking booking = Booking.builder()
                .id(8L)
                .user(owner)
                .bookingItems(List.of())
                .attendees(List.of())
                .build();
        when(bookingRepository.findWithDetailsById(8L)).thenReturn(Optional.of(booking));

        assertEquals(8L, bookingService.getBookingById(8L, admin, true).id());
    }
}
