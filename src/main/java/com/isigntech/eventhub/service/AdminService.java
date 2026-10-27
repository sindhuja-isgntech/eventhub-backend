package com.isigntech.eventhub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isigntech.eventhub.entity.Booking;
import com.isigntech.eventhub.entity.Event;
import com.isigntech.eventhub.entity.EventCategory;
import com.isigntech.eventhub.entity.User;
import com.isigntech.eventhub.entity.Venue;
import com.isigntech.eventhub.repository.BookingRepository;
import com.isigntech.eventhub.repository.EventCategoryRepository;
import com.isigntech.eventhub.repository.EventRepository;
import com.isigntech.eventhub.repository.UserRepository;
import com.isigntech.eventhub.repository.VenueRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
	
	@Autowired
	private EventRepository eventRepository;
	
	@Autowired
	private EventCategoryRepository eventCategoryRepository;
	
	@Autowired
	private VenueRepository venueRepository;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	public EventCategory createCategory(EventCategory eventCategory)
	{
		return eventCategoryRepository.save(eventCategory);
	}
	
	
	public List<EventCategory> getAllCategories() {
        return eventCategoryRepository.findAll();
    }

    public void deleteCategory(Long id) {
        eventCategoryRepository.deleteById(id);
    }

    // Venue CRUD
    public Venue createVenue(Venue venue) {
        return venueRepository.save(venue);
    }

    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }

    public void deleteVenue(Long id) {
        venueRepository.deleteById(id);
    }
    
 // Event Management
    public Event updateEvent(Long id, Event updatedEvent) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        event.setTitle(updatedEvent.getTitle());
        event.setDescription(updatedEvent.getDescription());
        event.setDateTime(updatedEvent.getDateTime());
        event.setPrice(updatedEvent.getPrice());
        event.setTotalSeats(updatedEvent.getTotalSeats());
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    // Booking Management
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking updateBookingStatus(Long bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));
        booking.setStatus(status);
        return bookingRepository.save(booking);
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

}
