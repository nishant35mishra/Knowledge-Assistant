package com.project.knowledgeassistant.services;

import com.project.knowledgeassistant.entities.MyUser;
import com.project.knowledgeassistant.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class MyUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<MyUser> myuser  = userRepository.findByEmail(email);
        MyUser user1 = myuser.get() ;

        ArrayList<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority(user1.getRole().toString())) ;

        return User.builder()
                .username(user1.getEmail())
                .password(user1.getPassword())
                .authorities(list)
                .build() ;
    }
}
