package com.isigntech.eventhub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.isigntech.eventhub.dto.AdminBookingSummary;
import com.isigntech.eventhub.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	List<Booking> findByUserId(Long userId);

	@EntityGraph(attributePaths = {"user", "bookingItems", "bookingItems.event", "bookingItems.event.category", "bookingItems.event.venue"})
	List<Booking> findByUser_IdOrderByBookingDateDesc(Long userId);

	@EntityGraph(attributePaths = {"user", "bookingItems", "bookingItems.event", "bookingItems.event.category", "bookingItems.event.venue"})
	List<Booking> findAllByOrderByBookingDateDesc();

	@Query("""
			select new com.isigntech.eventhub.dto.AdminBookingSummary(
				count(b),
				coalesce(sum(b.ticketCount), 0L),
				coalesce(sum(b.totalAmount), 0.0)
			)
			from Booking b
			where b.status = 'CONFIRMED'
			""")
	AdminBookingSummary getConfirmedBookingSummary();

	@EntityGraph(attributePaths = {"user", "bookingItems", "bookingItems.event", "bookingItems.event.category", "bookingItems.event.venue"})
	Optional<Booking> findWithDetailsById(Long id);

}
