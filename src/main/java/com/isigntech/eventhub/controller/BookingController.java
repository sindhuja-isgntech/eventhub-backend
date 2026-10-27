package com.isigntech.eventhub.controller;


import com.isigntech.eventhub.dto.BookingRequestDto;
import com.isigntech.eventhub.dto.BookingResponse;
import com.isigntech.eventhub.entity.User;
import com.isigntech.eventhub.service.AuthSessionService;
import com.isigntech.eventhub.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final AuthSessionService authSessionService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequestDto requestDto,
            HttpSession session) {
        User user = authSessionService.requireUser(session);
        return new ResponseEntity<>(bookingService.createBooking(requestDto, user), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id, HttpSession session) {
        User user = authSessionService.requireUser(session);
        return ResponseEntity.ok(bookingService.getBookingById(id, user, "ADMIN".equals(user.getRole())));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<BookingResponse>> getMyBookings(HttpSession session) {
        return ResponseEntity.ok(bookingService.getBookingsForUser(authSessionService.requireUser(session)));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id, HttpSession session) {
        User user = authSessionService.requireUser(session);
        return ResponseEntity.ok(bookingService.cancelBooking(id, user, "ADMIN".equals(user.getRole())));
    }
}
