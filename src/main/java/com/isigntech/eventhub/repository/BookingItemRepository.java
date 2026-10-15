package com.isigntech.eventhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isigntech.eventhub.entity.BookingItem;

public interface BookingItemRepository extends JpaRepository<BookingItem, Long> {

}
