package com.isigntech.eventhub.controller;

import com.isigntech.eventhub.dto.VenueRequestDto;
import com.isigntech.eventhub.entity.Venue;
import com.isigntech.eventhub.service.VenueService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {
	
	@Autowired
	private VenueService venueService;

	@GetMapping
    public ResponseEntity<List<Venue>> getAllVenues() {
        return ResponseEntity.ok(venueService.getAllVenues());
    }

    @PostMapping
    public ResponseEntity<Venue> createVenue(@Valid @RequestBody VenueRequestDto requestDto) {
        Venue venue = Venue.builder()
                .name(requestDto.name())
                .address(requestDto.address())
                .city(requestDto.city())
                .capacity(requestDto.capacity())
                .build();
        return new ResponseEntity<>(venueService.createVenue(venue), HttpStatus.CREATED);
    }

}
