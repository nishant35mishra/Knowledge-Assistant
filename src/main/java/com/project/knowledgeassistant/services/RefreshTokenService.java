package com.project.knowledgeassistant.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.project.knowledgeassistant.CustomException.NotFound;
import com.project.knowledgeassistant.entities.RefreshToken;
import com.project.knowledgeassistant.entities.User;
import com.project.knowledgeassistant.repositories.RefreshTokenRepository;
import com.project.knowledgeassistant.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class RefreshTokenService {

	public static final long REFRESH_TOKEN_VALIDITY = 7L*24*60*60*1000;
	
	private final RefreshTokenRepository refreshRepo;
	
	private final UserRepository userRepo;
	
	public RefreshToken createToken(String email) {
		User user = userRepo.findByEmail(email).orElseThrow(()-> new NotFound("User not found with this email"));
		
		RefreshToken token = refreshRepo.findByUser(user).orElse(new RefreshToken());
		//refreshRepo.deleteByUser(user);
		
	//	 RefreshToken token = new RefreshToken();
		 token.setUser(user);
		 token.setToken(UUID.randomUUID().toString());
		 token.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_VALIDITY));
		 token.setRevoked(false);
		
		
		return refreshRepo.save(token);
		
	}
	
	public RefreshToken verifyExpiration(RefreshToken token) {
		if(token.isRevoked() || token.getExpiryDate().isBefore(Instant.now())) {
				refreshRepo.delete(token);
				throw new RuntimeException("Refresh token was expired or revoked pls signin again....");
		}
		return token;
	}
	
	public void revokeToken(String token) {
		RefreshToken rToken = refreshRepo.findByToken(token).orElseThrow(()-> new NotFound("Token does not found"));
		rToken.setRevoked(true);
		refreshRepo.save(rToken);
	}
}
