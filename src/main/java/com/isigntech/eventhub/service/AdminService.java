package com.isigntech.eventhub.service;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isigntech.eventhub.dto.CategoryRequestDto;
import com.isigntech.eventhub.dto.EventRequestDto;
import com.isigntech.eventhub.dto.VenueRequestDto;
import com.isigntech.eventhub.entity.Event;
import com.isigntech.eventhub.entity.EventCategory;
import com.isigntech.eventhub.entity.User;
import com.isigntech.eventhub.entity.Venue;
import com.isigntech.eventhub.exception.AuthException;
import com.isigntech.eventhub.repository.BookingItemRepository;
import com.isigntech.eventhub.repository.BookingRepository;
import com.isigntech.eventhub.repository.EventCategoryRepository;
import com.isigntech.eventhub.repository.EventRepository;
import com.isigntech.eventhub.repository.UserRepository;
import com.isigntech.eventhub.repository.VenueRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final EventRepository eventRepository;
    private final EventCategoryRepository eventCategoryRepository;
    private final VenueRepository venueRepository;
    private final BookingRepository bookingRepository;
    private final BookingItemRepository bookingItemRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<EventCategory> getAllCategories() {
        return eventCategoryRepository.findAll();
    }

    public EventCategory createCategory(CategoryRequestDto request) {
        return eventCategoryRepository.save(EventCategory.builder()
                .name(request.name())
                .description(request.description())
                .status(request.status() == null ? "ACTIVE" : request.status())
                .build());
    }

    public EventCategory updateCategory(Long id, CategoryRequestDto request) {
        EventCategory category = eventCategoryRepository.findById(id)
                .orElseThrow(() -> notFound("Category", id));
        category.setName(request.name());
        category.setDescription(request.description());
        if (request.status() != null) {
            category.setStatus(request.status());
        }
        return eventCategoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        EventCategory category = eventCategoryRepository.findById(id)
                .orElseThrow(() -> notFound("Category", id));
        if (eventRepository.existsByCategory_Id(id)) {
            throw new AuthException(HttpStatus.CONFLICT, "Cannot delete a category while events use it.");
        }
        eventCategoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Venue createVenue(VenueRequestDto request) {
        return venueRepository.save(Venue.builder()
                .name(request.name())
                .address(request.address())
                .city(request.city())
                .capacity(request.capacity())
                .build());
    }

    public Venue updateVenue(Long id, VenueRequestDto request) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> notFound("Venue", id));
        venue.setName(request.name());
        venue.setAddress(request.address());
        venue.setCity(request.city());
        venue.setCapacity(request.capacity());
        return venueRepository.save(venue);
    }

    public void deleteVenue(Long id) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> notFound("Venue", id));
        if (eventRepository.existsByVenue_Id(id)) {
            throw new AuthException(HttpStatus.CONFLICT, "Cannot delete a venue while events use it.");
        }
        venueRepository.delete(venue);
    }

    @Transactional
    public Event createEvent(EventRequestDto request) {
        if (!request.dateTime().isAfter(LocalDateTime.now())) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "Event date and time must be in the future.");
        }
        EventCategory category = eventCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> notFound("Category", request.categoryId()));
        Venue venue = venueRepository.findById(request.venueId())
                .orElseThrow(() -> notFound("Venue", request.venueId()));
        return eventRepository.save(Event.builder()
                .title(request.title())
                .description(request.description())
                .category(category)
                .venue(venue)
                .dateTime(request.dateTime())
                .price(request.price())
                .totalSeats(request.totalSeats())
                .availableSeats(request.totalSeats())
                .imageUrl(request.imageUrl())
                .organizer(request.organizer() == null ? "EventHub Host" : request.organizer())
                .featured(request.featured() == null ? false : request.featured())
                .status("UPCOMING")
                .build());
    }

    @Transactional
    public Event updateEvent(Long id, EventRequestDto request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> notFound("Event", id));
        EventCategory category = eventCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> notFound("Category", request.categoryId()));
        Venue venue = venueRepository.findById(request.venueId())
                .orElseThrow(() -> notFound("Venue", request.venueId()));
        int ticketsSold = event.getTotalSeats() - event.getAvailableSeats();
        if (request.totalSeats() < ticketsSold) {
            throw new AuthException(HttpStatus.CONFLICT, "Total seats cannot be less than tickets already sold.");
        }
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setCategory(category);
        event.setVenue(venue);
        event.setDateTime(request.dateTime());
        event.setPrice(request.price());
        event.setTotalSeats(request.totalSeats());
        event.setAvailableSeats(request.totalSeats() - ticketsSold);
        event.setImageUrl(request.imageUrl());
        event.setOrganizer(request.organizer());
        event.setFeatured(request.featured());
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> notFound("Event", id));
        if (bookingItemRepository.existsByEvent_Id(id)) {
            throw new AuthException(HttpStatus.CONFLICT, "Cannot delete an event with existing bookings.");
        }
        eventRepository.delete(event);
    }

    @Transactional
    public void deleteBooking(Long bookingId) {
        var booking = bookingRepository.findWithDetailsById(bookingId)
                .orElseThrow(() -> notFound("Booking", bookingId));
        if ("CONFIRMED".equals(booking.getStatus())) {
            booking.getBookingItems().forEach(item -> {
                Event event = item.getEvent();
                event.setAvailableSeats(event.getAvailableSeats() + item.getQuantity());
                eventRepository.save(event);
            });
        }
        bookingRepository.delete(booking);
    }

    // Profile Management
    public User getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    public User updateUserProfile(Long userId, User updatedUser) {
        User user = getUserProfile(userId);
        user.setName(updatedUser.getName());
        user.setPhone(updatedUser.getPhone());
        return userRepository.save(user);
    }

    private AuthException notFound(String resource, Long id) {
        return new AuthException(HttpStatus.NOT_FOUND, resource + " not found with id: " + id);
    }

}
