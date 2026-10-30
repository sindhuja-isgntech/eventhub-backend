package com.isigntech.eventhub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.isigntech.eventhub.dto.EventRequestDto;
import com.isigntech.eventhub.entity.Event;
import com.isigntech.eventhub.entity.EventCategory;
import com.isigntech.eventhub.entity.Venue;
import com.isigntech.eventhub.exception.AuthException;
import com.isigntech.eventhub.repository.BookingItemRepository;
import com.isigntech.eventhub.repository.BookingRepository;
import com.isigntech.eventhub.repository.EventCategoryRepository;
import com.isigntech.eventhub.repository.EventRepository;
import com.isigntech.eventhub.repository.UserRepository;
import com.isigntech.eventhub.repository.VenueRepository;

class AdminServiceTest {
    private EventRepository eventRepository;
    private EventCategoryRepository categoryRepository;
    private VenueRepository venueRepository;
    private BookingItemRepository bookingItemRepository;
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        categoryRepository = mock(EventCategoryRepository.class);
        venueRepository = mock(VenueRepository.class);
        bookingItemRepository = mock(BookingItemRepository.class);
        adminService = new AdminService(
                eventRepository,
                categoryRepository,
                venueRepository,
                mock(BookingRepository.class),
                bookingItemRepository,
                mock(UserRepository.class));
    }

    @Test
    void eventUpdateCannotReduceCapacityBelowTicketsAlreadySold() {
        Event existing = Event.builder().id(7L).totalSeats(100).availableSeats(60).build();
        when(eventRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(EventCategory.builder().id(1L).build()));
        when(venueRepository.findById(2L)).thenReturn(Optional.of(Venue.builder().id(2L).build()));

        AuthException exception = assertThrows(AuthException.class,
                () -> adminService.updateEvent(7L, eventRequest(39)));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(eventRepository, never()).save(existing);
    }

    @Test
    void deletingAnEventWithBookingsIsRejected() {
        Event event = Event.builder().id(7L).build();
        when(eventRepository.findById(7L)).thenReturn(Optional.of(event));
        when(bookingItemRepository.existsByEvent_Id(7L)).thenReturn(true);

        AuthException exception = assertThrows(AuthException.class, () -> adminService.deleteEvent(7L));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(eventRepository, never()).delete(event);
    }

    private EventRequestDto eventRequest(int totalSeats) {
        return new EventRequestDto(
                "Updated event",
                "Description",
                1L,
                2L,
                LocalDateTime.now().plusDays(1),
                50.0,
                totalSeats,
                "https://example.com/image.jpg",
                "Organizer",
                false);
    }
}
