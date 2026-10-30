package com.isigntech.eventhub.controller;

import java.util.List;

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

import com.isigntech.eventhub.dto.BookingResponse;
import com.isigntech.eventhub.dto.CategoryRequestDto;
import com.isigntech.eventhub.dto.EventRequestDto;
import com.isigntech.eventhub.dto.VenueRequestDto;
import com.isigntech.eventhub.entity.Event;
import com.isigntech.eventhub.entity.EventCategory;
import com.isigntech.eventhub.entity.User;
import com.isigntech.eventhub.entity.Venue;
import com.isigntech.eventhub.service.AdminService;
import com.isigntech.eventhub.service.BookingService;
import com.isigntech.eventhub.service.RequestUserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final RequestUserService requestUserService;
    private final BookingService bookingService;
    private final AdminService adminService;

    @GetMapping("/categories")
    public ResponseEntity<List<EventCategory>> getCategories(HttpServletRequest request) {
        requestUserService.requireAdmin(request);
        return ResponseEntity.ok(adminService.getAllCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<EventCategory> createCategory(
            @Valid @RequestBody CategoryRequestDto category,
            HttpServletRequest request) {
        requestUserService.requireAdmin(request);
        return ResponseEntity.ok(adminService.createCategory(category));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<EventCategory> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDto category,
            HttpServletRequest request) {
        requestUserService.requireAdmin(request);
        return ResponseEntity.ok(adminService.updateCategory(id, category));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id, HttpServletRequest request) {
        requestUserService.requireAdmin(request);
        adminService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/venues")
    public ResponseEntity<List<Venue>> getVenues(HttpServletRequest request) {
        requestUserService.requireAdmin(request);
        return ResponseEntity.ok(adminService.getAllVenues());
    }

    @PostMapping("/venues")
    public ResponseEntity<Venue> createVenue(
            @Valid @RequestBody VenueRequestDto venue,
            HttpServletRequest request) {
        requestUserService.requireAdmin(request);
        return ResponseEntity.ok(adminService.createVenue(venue));
    }

    @PutMapping("/venues/{id}")
    public ResponseEntity<Venue> updateVenue(
            @PathVariable Long id,
            @Valid @RequestBody VenueRequestDto venue,
            HttpServletRequest request) {
        requestUserService.requireAdmin(request);
        return ResponseEntity.ok(adminService.updateVenue(id, venue));
    }

    @DeleteMapping("/venues/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id, HttpServletRequest request) {
        requestUserService.requireAdmin(request);
        adminService.deleteVenue(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/events")
    public ResponseEntity<List<Event>> getEvents(HttpServletRequest request) {
        requestUserService.requireAdmin(request);
        return ResponseEntity.ok(adminService.getAllEvents());
    }

    @PostMapping("/events")
    public ResponseEntity<Event> createEvent(
            @Valid @RequestBody EventRequestDto event,
            HttpServletRequest request) {
        requestUserService.requireAdmin(request);
        return ResponseEntity.ok(adminService.createEvent(event));
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequestDto event,
            HttpServletRequest request) {
        requestUserService.requireAdmin(request);
        return ResponseEntity.ok(adminService.updateEvent(id, event));
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, HttpServletRequest request) {
        requestUserService.requireAdmin(request);
        adminService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings(HttpServletRequest request) {
        requestUserService.requireAdmin(request);
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PatchMapping("/bookings/{id}/status")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam String status,
            HttpServletRequest request) {
        requestUserService.requireAdmin(request);
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }

    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id, HttpServletRequest request) {
        requestUserService.requireAdmin(request);
        adminService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserProfile(@PathVariable Long id, HttpServletRequest request) {
        requestUserService.requireAdmin(request);
        return ResponseEntity.ok(adminService.getUserProfile(id));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUserProfile(
            @PathVariable Long id,
            @RequestBody User user,
            HttpServletRequest request) {
        requestUserService.requireAdmin(request);
        return ResponseEntity.ok(adminService.updateUserProfile(id, user));
    }
}
