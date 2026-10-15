package com.isigntech.eventhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isigntech.eventhub.entity.Attendee;

public interface AttendeeRepository extends JpaRepository<Attendee, Long>{
	

}
