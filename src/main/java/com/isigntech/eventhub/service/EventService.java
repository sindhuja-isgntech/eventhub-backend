package com.isigntech.eventhub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isigntech.eventhub.entity.Event;
import com.isigntech.eventhub.repository.EventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {
	
	@Autowired
	private EventRepository eventRepository;
	
	public List<Event> getAllEvents(){
		return eventRepository.findAll();
	}
	
	public Event getEventById(Long id)
	{
		return eventRepository.findById(id).orElseThrow(()-> new RuntimeException());
	}
	
	public Event createEvent(Event event) {
        return eventRepository.save(event);
    }
	
	public List<Event> searchEvents(String title)
	{
		return eventRepository.findByTitleContainingIgnoreCase(title);
	}
	

}
