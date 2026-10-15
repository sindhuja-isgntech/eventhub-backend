package com.isigntech.eventhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isigntech.eventhub.entity.EventCategory;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long>{

}
