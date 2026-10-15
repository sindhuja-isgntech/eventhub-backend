package com.isigntech.eventhub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false,unique = true)
	private String email;
	
	@Column(nullable = false)
	private String password;
	
	private String phone;
	
	@Column(nullable = false)
	private String role;
	
	@Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
	
	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	//private List<Booking> bookings;
	
	@PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
