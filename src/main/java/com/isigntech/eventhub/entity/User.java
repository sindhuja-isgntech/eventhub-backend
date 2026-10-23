package com.isigntech.eventhub.entity;
 
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false,unique = true)
	private String email;
	
	@JsonIgnore
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
