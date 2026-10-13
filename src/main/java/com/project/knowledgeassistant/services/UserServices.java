package com.project.knowledgeassistant.services;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.knowledgeassistant.CustomException.NotFound;
import com.project.knowledgeassistant.CustomException.UserAlreadyExist;
import com.project.knowledgeassistant.DTOs.AuthResponseDto;
import com.project.knowledgeassistant.DTOs.LoginDto;
import com.project.knowledgeassistant.DTOs.RefreshTokenRequestDto;
import com.project.knowledgeassistant.DTOs.RegisterRequestDto;
import com.project.knowledgeassistant.DTOs.RegisterResponseDto;
import com.project.knowledgeassistant.ObjectMapper.RegisterRequestMapper;
import com.project.knowledgeassistant.entities.RefreshToken;
import com.project.knowledgeassistant.entities.Role;
import com.project.knowledgeassistant.entities.User;
import com.project.knowledgeassistant.enums.AccountStatus;
import com.project.knowledgeassistant.repositories.RefreshTokenRepository;
import com.project.knowledgeassistant.repositories.RoleRepository;
import com.project.knowledgeassistant.repositories.UserRepository;
import com.project.knowledgeassistant.utilities.JWTUtility;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class UserServices {

    
    private final UserRepository userRepository;
    
    private final RoleRepository roleRepository;
    	
    private final RegisterRequestMapper requestMapper ;


    private final BCryptPasswordEncoder getEncoder ;

   
    private final JWTUtility jwtUtility;

    
    private final RefreshTokenService refreshService;
    
    private final RefreshTokenRepository refreshRepo;


    public RegisterResponseDto  addUser(RegisterRequestDto dto){

    	
        Optional<User> optUser  = userRepository.findByEmail(dto.getEmail());
        if(optUser.isPresent()){

            throw new UserAlreadyExist("User already exist with email"+dto.getEmail());
        }

        String requestedRole = dto.getRole().trim().toUpperCase();
        if(!requestedRole.startsWith("ROLE_")) {
        	requestedRole = "ROLE_"+requestedRole;
        }
        
        if(requestedRole.equals("ROLE_ADMIN")) {
        	throw new RuntimeException("USER CAN NOT SELECT ITSELF AS ADMIN");
        }
        
        Role role = roleRepository.findByRole(requestedRole).orElseThrow(()->new NotFound("Role not found in database"));
        
        User myUser = requestMapper.RegisterRequestMapper(dto) ;
        myUser.setPassword(getEncoder.encode(dto.getPassword()));
        
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        myUser.setRoles(roles);
        
        if("ROLE_PUBLIC".equals(requestedRole)) {
        	myUser.setAccountStatus(AccountStatus.ACTIVE);
        }
        else {
        	myUser.setAccountStatus(AccountStatus.PENDING_APPROVAL);
        }

      User user =   userRepository.save(myUser) ;

        RegisterResponseDto response = requestMapper.RegisterResponseMapper(user) ;

        return response  ;

    }

    public AuthResponseDto getUser(LoginDto dto) {

        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(()-> new NotFound("User Not Found"));
        
        if(!getEncoder.matches(dto.getPassword(),user.getPassword())){
        	throw new RuntimeException("Ivalid Password");
        }

        if(user.getAccountStatus() != AccountStatus.ACTIVE){
        	throw new RuntimeException("Account is not active..\n Current Status : "+user.getAccountStatus());
        }
         

         List<String> roleNames = user.getRoles().stream().map(m->m.getRole()).collect(Collectors.toList());

         // to generate short access token like for 15 minutes
         String accessToken = jwtUtility.generateAccessToken(user.getUsername(), user.getEmail(), roleNames);
         
         RefreshToken refreshToken = refreshService.createToken(user.getEmail());
         
          //  RegisterResponseDto response = requestMapper.RegisterResponseMapper(user.get());
            return AuthResponseDto.builder().refreshToken(refreshToken.getToken()).accessToken(accessToken).email(user.getEmail())
            		.userName(user.getUsername()).roles(roleNames).build();
        }
    
    public String approveUser(int userId,String approverEmail) {
    	
    	User user = userRepository.findById(userId).orElseThrow(()-> new NotFound("User with id does not exist"));
    	User approver = userRepository.findByEmail(approverEmail).orElseThrow(()-> new NotFound("Approver Not Found"));
    	
    	boolean isApproverAdmin = approver.getRoles().stream().anyMatch(r->"ROLE_ADMIN".equals(r.getRole()));
    	boolean isApproverManager = approver.getRoles().stream()
    	            .anyMatch(r -> "ROLE_MANAGER".equals(r.getRole()));
    	
        boolean targetIsManager = user.getRoles().stream()
                .anyMatch(r -> "ROLE_MANAGER".equals(r.getRole()));
        boolean targetIsEmployee = user.getRoles().stream()
                .anyMatch(r -> "ROLE_EMPLOYEE".equals(r.getRole()));
        
        if(targetIsManager && !isApproverAdmin) {
        	throw new RuntimeException("Only an ADMIN can approve a MANAGER account!");
        }
        
        if (targetIsEmployee && !isApproverManager && !isApproverAdmin) {
            throw new RuntimeException("Only a MANAGER or ADMIN can approve an EMPLOYEE account!");
        }
        
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setApprovedAt(LocalDate.now());
        user.setApprovedBy(approver.getEmail());
        userRepository.save(user);
        
    	return "User "+user.getEmail()+" has been approved successfully..";
    }
    
    public AuthResponseDto refreshAccessToken(RefreshTokenRequestDto req) {
    	
    	RefreshToken token = refreshRepo.findByToken(req.getRefreshToken()).orElseThrow(()-> new RuntimeException("Refresh token does not exist in db"));
    	
    	refreshService.verifyExpiration(token);
    	
    	User user = token.getUser();
    	List<String> roles = user.getRoles().stream().map(m->m.getRole()).collect(Collectors.toList());
    	
    	//for genereting new refresh toeken
    	String newAccessToken = jwtUtility.generateAccessToken(user.getUsername(), user.getEmail(), roles);
    	
    	return AuthResponseDto.builder().refreshToken(token.getToken()).accessToken(newAccessToken).email(user.getEmail())
        		.userName(user.getUsername()).roles(roles).build();
    }
    
    
    public List<RegisterResponseDto> getPendingUser(String userEmail) {
    	User user = userRepository.findByEmail(userEmail).orElseThrow(()-> new NotFound("User with the email do not exist"));
    	
    	boolean isAdmin = user.getRoles().stream().anyMatch(r->"ROLE_ADMIN".equals(r.getRole()));
    	boolean isManager = user.getRoles().stream()
    	            .anyMatch(r -> "ROLE_MANAGER".equals(r.getRole()));
    	
    	List<User> pendingUsers = userRepository.findByAccountStatus(AccountStatus.PENDING_APPROVAL);
    	
    	if(isAdmin) {
    		return pendingUsers.stream().map(requestMapper::RegisterResponseMapper).collect(Collectors.toList());
    	}
    	else if(isManager) {
    		return pendingUsers.stream().filter(u->u.getRoles().stream().anyMatch(r->"ROLE_EMPLOYEE".equals(r.getRole()))).map(requestMapper::RegisterResponseMapper)
    				.collect(Collectors.toList());
    	}
    	return List.of();
    }
   
    public String rejectUser(int userId,String approverEmail){
    	
    	User user = userRepository.findById(userId).orElseThrow(()-> new NotFound("User with the Id do not exist"));
    	
    	User approverUser = userRepository.findByEmail(approverEmail).orElseThrow(()-> new NotFound("Approver with the email do not exist"));
    	
    	boolean isApproverAdmin = approverUser.getRoles().stream()
                .anyMatch(r -> "ROLE_ADMIN".equals(r.getRole()));
        boolean isApproverManager = approverUser.getRoles().stream()
                .anyMatch(r -> "ROLE_MANAGER".equals(r.getRole()));
        boolean targetIsManager = user.getRoles().stream()
                .anyMatch(r -> "ROLE_MANAGER".equals(r.getRole()));
        boolean targetIsEmployee = user.getRoles().stream()
                .anyMatch(r -> "ROLE_EMPLOYEE".equals(r.getRole()));
        
        if(targetIsManager && !isApproverAdmin) {
        	throw new RuntimeException("only an ADMIN can reject a MANAGER account..");
        }
        
        if(targetIsEmployee && !isApproverAdmin && !isApproverManager) {
        	throw new RuntimeException("only a ADMIN or MANAGER can reject an EMPLOYEE account..");
        }
        
        user.setAccountStatus(AccountStatus.REJECTED);
        userRepository.save(user);
        
    	return "User "+ user.getEmail()+" has been rejected..";
    }
    
    public void logOut(RefreshTokenRequestDto dto) {
    	refreshService.revokeToken(dto.getRefreshToken());
    }
    
    public List<RegisterResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(requestMapper::RegisterResponseMapper)
                .collect(Collectors.toList());
    }

    }


