package com.project.knowledgeassistant.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.knowledgeassistant.enums.AccountStatus;
import com.project.knowledgeassistant.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class MyUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    	com.project.knowledgeassistant.entities.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    	

        List<GrantedAuthority> authorities = new ArrayList<>();
        if(user.getRoles() != null) {
        	user.getRoles().forEach(role->{
        		String roleName = role.getRole();
        		if(roleName != null && !roleName.startsWith("ROLE_")) {
        			roleName = "ROLE_" + roleName;
        		}
        		authorities.add(new SimpleGrantedAuthority(roleName));
        	});
        }
        

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .disabled(user.getAccountStatus() != AccountStatus.ACTIVE)
                .build() ;
    }
}
