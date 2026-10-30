package com.isigntech.eventhub.controller;


import com.isigntech.eventhub.dto.BookingRequestDto;
import com.isigntech.eventhub.dto.BookingResponse;
import com.isigntech.eventhub.entity.User;
import com.isigntech.eventhub.service.BookingService;
import com.isigntech.eventhub.service.RequestUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final RequestUserService requestUserService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequestDto requestDto,
            HttpServletRequest request) {
        User user = requestUserService.requireUser(request);
        return new ResponseEntity<>(bookingService.createBooking(requestDto, user), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id, HttpServletRequest request) {
        User user = requestUserService.requireUser(request);
        return ResponseEntity.ok(bookingService.getBookingById(id, user, "ADMIN".equals(user.getRole())));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<BookingResponse>> getMyBookings(HttpServletRequest request) {
        return ResponseEntity.ok(bookingService.getBookingsForUser(requestUserService.requireUser(request)));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id, HttpServletRequest request) {
        User user = requestUserService.requireUser(request);
        return ResponseEntity.ok(bookingService.cancelBooking(id, user, "ADMIN".equals(user.getRole())));
    }
}
