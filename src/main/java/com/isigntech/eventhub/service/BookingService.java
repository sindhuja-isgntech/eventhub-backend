package com.isigntech.eventhub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isigntech.eventhub.entity.Booking;
import com.isigntech.eventhub.entity.Event;
import com.isigntech.eventhub.repository.BookingRepository;
import com.isigntech.eventhub.repository.EventRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {
	
	@Autowired
	private BookingRepository bookingRepository;
	
	@Autowired
	private EventRepository eventRepository;
	
	@Transactional
	public Booking createBooking(Booking booking)
	{
		for(var item : booking.getBookingItems())
		{
			item.setBooking(booking);

			Event event = eventRepository.findById(item.getEvent().getId())
                    .orElseThrow(() -> new RuntimeException("Event not found"));
			
			if(event.getAvailableSeats()<item.getQuantity())
			{
				throw new RuntimeException("Not enough available seats for the event");
			}
			event.setAvailableSeats(event.getAvailableSeats() - item.getQuantity());
            eventRepository.save(event);
		}
		
		return bookingRepository.save(booking);
		
	}
	
	public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

}
