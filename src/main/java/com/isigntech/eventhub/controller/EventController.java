package com.isigntech.eventhub.controller;


import com.isigntech.eventhub.dto.EventRequestDto;
import com.isigntech.eventhub.entity.Event;
import com.isigntech.eventhub.service.EventService;
import com.isigntech.eventhub.service.RequestUserService;
import com.isigntech.eventhub.service.AdminService;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
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
    private final RequestUserService requestUserService;
    private final AdminService adminService;

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
    public ResponseEntity<Event> createEvent(
            @Valid @RequestBody EventRequestDto requestDto,
            HttpServletRequest request) {
        requestUserService.requireAdmin(request);
        return new ResponseEntity<>(adminService.createEvent(requestDto), HttpStatus.CREATED);
    }

}
