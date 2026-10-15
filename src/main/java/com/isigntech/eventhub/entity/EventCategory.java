package com.isigntech.eventhub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "event_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventCategory {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
    
    private String status; // e.g., ACTIVE, INACTIVE

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Event> events;

}
