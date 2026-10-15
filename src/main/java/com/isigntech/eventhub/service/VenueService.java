package com.isigntech.eventhub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isigntech.eventhub.entity.Venue;
import com.isigntech.eventhub.repository.VenueRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VenueService {
	
	@Autowired
	private VenueRepository venueRepository;
	
	public List<Venue> getAllVenues(){
		return venueRepository.findAll();
	}
	
	public Venue createVenue(Venue venue) {
		return venueRepository.save(venue);
		
	}
	

}
