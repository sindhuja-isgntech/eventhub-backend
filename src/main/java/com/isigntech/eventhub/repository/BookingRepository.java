package com.isigntech.eventhub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.isigntech.eventhub.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	List<Booking> findByUserId(Long userId);

	@EntityGraph(attributePaths = {"user", "bookingItems", "bookingItems.event", "bookingItems.event.category", "bookingItems.event.venue"})
	List<Booking> findByUser_IdOrderByBookingDateDesc(Long userId);

	@EntityGraph(attributePaths = {"user", "bookingItems", "bookingItems.event", "bookingItems.event.category", "bookingItems.event.venue"})
	List<Booking> findAllByOrderByBookingDateDesc();

	@EntityGraph(attributePaths = {"user", "bookingItems", "bookingItems.event", "bookingItems.event.category", "bookingItems.event.venue"})
	Optional<Booking> findWithDetailsById(Long id);

}
