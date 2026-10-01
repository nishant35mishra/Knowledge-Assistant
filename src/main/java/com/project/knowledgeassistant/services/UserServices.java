package com.project.knowledgeassistant.services;

import com.project.knowledgeassistant.CustomException.NotFound;
import com.project.knowledgeassistant.CustomException.UserAlreadyExist;
import com.project.knowledgeassistant.CustomException.somethingWentWrong;
import com.project.knowledgeassistant.DTOs.LoginDto;
import com.project.knowledgeassistant.DTOs.RegisterRequestDto;
import com.project.knowledgeassistant.DTOs.RegisterResponseDto;
import com.project.knowledgeassistant.ObjectMapper.RegisterRequestMapper;
import com.project.knowledgeassistant.entities.MyUser;
import com.project.knowledgeassistant.repositories.UserRepository;
import com.project.knowledgeassistant.utilities.JWTUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserServices {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RegisterRequestMapper requestMapper ;

    @Autowired
    private BCryptPasswordEncoder getEncoder ;

    @Autowired
    JWTUtility jwtUtility;



    public RegisterResponseDto  addUser (RegisterRequestDto dto){

        Optional<MyUser> optUser  = userRepository.findByEmail(dto.getEmail());
        if(optUser.isPresent()){

            throw new UserAlreadyExist("User already exist");
        }

        MyUser myUser = requestMapper.RegisterRequestMapper(dto) ;
        myUser.setPassword(getEncoder.encode(dto.getPassword()));

      MyUser user =   userRepository.save(myUser) ;

        RegisterResponseDto response = requestMapper.RegisterResponseMapper(user) ;

        return response  ;

    }

    public String getUser (LoginDto dto) {

        Optional<MyUser> optUser = userRepository.findByEmail(dto.getEmail());
        if(!optUser.isPresent()){
            throw new NotFound("User Not Found ");
        }

        if(getEncoder.matches(dto.getPassword(),optUser.get().getPassword())){

            MyUser myUser = optUser.get();

            String role = myUser.getRole().toString().replace("ROLE_","");

            RegisterResponseDto response = requestMapper.RegisterResponseMapper(optUser.get());
            return jwtUtility.generateToken(myUser.getUsername(), myUser.getEmail(), role);
        }
        else {
            throw new somethingWentWrong("something went wrong");
        }

    }




}
