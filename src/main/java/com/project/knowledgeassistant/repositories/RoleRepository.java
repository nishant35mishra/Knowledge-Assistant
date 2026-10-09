package com.project.knowledgeassistant.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.knowledgeassistant.entities.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByRole(String role);
	
	boolean existsByRole(String role);
}
