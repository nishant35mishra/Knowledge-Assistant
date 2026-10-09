package com.project.knowledgeassistant.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.project.knowledgeassistant.entities.RefreshToken;
import com.project.knowledgeassistant.entities.User;



@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByToken(String token); //here we are finding refresh token to get new access token
	
	//doubt on this whether to use it or not 
	@Modifying
	int deleteByUser(User user);
	
	Optional<RefreshToken> findByUser(User user);
}
