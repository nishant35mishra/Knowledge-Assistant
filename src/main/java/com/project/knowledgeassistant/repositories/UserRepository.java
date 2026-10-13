package com.project.knowledgeassistant.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.knowledgeassistant.entities.User;
import com.project.knowledgeassistant.enums.AccountStatus;


@Repository
public interface UserRepository extends JpaRepository<User,Integer> {


    Optional<User> findByEmail(String email);


    List<User> findByAccountStatus(AccountStatus accountStatus);
}
