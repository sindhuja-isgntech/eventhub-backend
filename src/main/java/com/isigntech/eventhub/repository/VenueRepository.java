package com.isigntech.eventhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isigntech.eventhub.entity.Venue;

public interface VenueRepository extends JpaRepository<Venue, Long> {

}
