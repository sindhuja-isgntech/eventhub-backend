package com.isigntech.eventhub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isigntech.eventhub.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	
	Optional<Role> findByName(String name);

}
