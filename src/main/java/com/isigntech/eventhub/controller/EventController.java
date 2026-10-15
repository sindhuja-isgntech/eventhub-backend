package com.isigntech.eventhub.controller;


import com.isigntech.eventhub.dto.EventRequestDto;
import com.isigntech.eventhub.entity.Event;
import com.isigntech.eventhub.entity.EventCategory;
import com.isigntech.eventhub.entity.Venue;
import com.isigntech.eventhub.service.EventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
	
	private final EventService eventService;

	@GetMapping
    public ResponseEntity<List<Event>> getAllEvents(@RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(eventService.searchEvents(search));
        }
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody EventRequestDto requestDto) {
        Event event = Event.builder()
                .title(requestDto.title())
                .description(requestDto.description())
                .category(EventCategory.builder().id(requestDto.categoryId()).build())
                .venue(Venue.builder().id(requestDto.venueId()).build())
                .dateTime(requestDto.dateTime())
                .price(requestDto.price())
                .totalSeats(requestDto.totalSeats())
                .availableSeats(requestDto.totalSeats()) // Initialize available seats to total seats
                .status("UPCOMING")
                .build();

        return new ResponseEntity<>(eventService.createEvent(event), HttpStatus.CREATED);
    }

}
