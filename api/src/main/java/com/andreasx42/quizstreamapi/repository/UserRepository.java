package com.andreasx42.quizstreamapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.andreasx42.quizstreamapi.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

}
