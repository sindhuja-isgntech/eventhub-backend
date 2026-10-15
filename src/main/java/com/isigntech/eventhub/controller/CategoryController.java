package com.isigntech.eventhub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isigntech.eventhub.dto.CategoryRequestDto;
import com.isigntech.eventhub.entity.EventCategory;
import com.isigntech.eventhub.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping
    public ResponseEntity<List<EventCategory>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<EventCategory> createCategory(@Valid @RequestBody CategoryRequestDto requestDto) {
        EventCategory category = EventCategory.builder()
                .name(requestDto.name())
                .description(requestDto.description())
                .status(requestDto.status())
                .build();
        return new ResponseEntity<>(categoryService.createCategory(category), HttpStatus.CREATED);
    }
}
