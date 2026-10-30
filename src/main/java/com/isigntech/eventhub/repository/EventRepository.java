package com.isigntech.eventhub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isigntech.eventhub.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long>{
	
	List<Event> findByTitleContainingIgnoreCase(String title);
    List<Event> findByCategoryId(Long categoryId);
    boolean existsByCategory_Id(Long categoryId);
    boolean existsByVenue_Id(Long venueId);

}
