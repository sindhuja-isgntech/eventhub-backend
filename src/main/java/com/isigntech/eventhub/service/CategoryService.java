package com.isigntech.eventhub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isigntech.eventhub.entity.EventCategory;
import com.isigntech.eventhub.repository.EventCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
	
	@Autowired
	private EventCategoryRepository categoryRepository;
	
	public List<EventCategory> getAllCategories(){
		
		return categoryRepository.findAll();
	}
	
	public EventCategory createCategory(EventCategory eventCategory)
	{
		return categoryRepository.save(eventCategory);
	}
	

}
