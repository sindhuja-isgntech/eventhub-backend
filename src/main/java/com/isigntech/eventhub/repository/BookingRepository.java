package com.isigntech.eventhub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isigntech.eventhub.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	List<Booking> findByUserId(Long userId);

}
