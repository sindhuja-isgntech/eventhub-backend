package com.isigntech.eventhub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isigntech.eventhub.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByEmail(String email);

}
