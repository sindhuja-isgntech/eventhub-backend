package com.isigntech.eventhub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isigntech.eventhub.entity.Event;
import com.isigntech.eventhub.entity.EventCategory;
import com.isigntech.eventhub.entity.User;
import com.isigntech.eventhub.entity.Venue;
import com.isigntech.eventhub.dto.BookingResponse;
import com.isigntech.eventhub.service.AuthSessionService;
import com.isigntech.eventhub.service.AdminService;
import com.isigntech.eventhub.service.BookingService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthSessionService authSessionService;
    private final BookingService bookingService;

	@Autowired
	private AdminService adminService;
	
	// Categories
	@PostMapping("/categories")
	public ResponseEntity<EventCategory> createCategory(@RequestBody EventCategory eventCategory, HttpSession session)
	{
		authSessionService.requireAdmin(session);
		return ResponseEntity.ok(adminService.createCategory(eventCategory));
	}
	
	@DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id, HttpSession session) {
        authSessionService.requireAdmin(session);
        adminService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // Venues
    @PostMapping("/venues")
    public ResponseEntity<Venue> createVenue(@RequestBody Venue venue, HttpSession session) {
        authSessionService.requireAdmin(session);
        return ResponseEntity.ok(adminService.createVenue(venue));
    }

    @DeleteMapping("/venues/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id, HttpSession session) {
        authSessionService.requireAdmin(session);
        adminService.deleteVenue(id);
        return ResponseEntity.noContent().build();
    }

    // Events
    @PutMapping("/events/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event event, HttpSession session) {
        authSessionService.requireAdmin(session);
        return ResponseEntity.ok(adminService.updateEvent(id, event));
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, HttpSession session) {
        authSessionService.requireAdmin(session);
        adminService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    // Bookings
    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings(HttpSession session) {
        authSessionService.requireAdmin(session);
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PatchMapping("/bookings/{id}/status")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam String status,
            HttpSession session) {
        authSessionService.requireAdmin(session);
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }

    // User Profile
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserProfile(@PathVariable Long id, HttpSession session) {
        authSessionService.requireAdmin(session);
        return ResponseEntity.ok(adminService.getUserProfile(id));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUserProfile(@PathVariable Long id, @RequestBody User user, HttpSession session) {
        authSessionService.requireAdmin(session);
        return ResponseEntity.ok(adminService.updateUserProfile(id, user));
    }
	
	

}
