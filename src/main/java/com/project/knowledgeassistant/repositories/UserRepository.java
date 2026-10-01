package com.project.knowledgeassistant.repositories;

import com.project.knowledgeassistant.entities.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<MyUser,Integer> {


    public Optional<MyUser> findByEmail(String email);


}
