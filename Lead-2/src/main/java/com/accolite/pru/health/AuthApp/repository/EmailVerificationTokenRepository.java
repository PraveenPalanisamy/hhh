package com.accolite.pru.health.AuthApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accolite.pru.health.AuthApp.model.token.EmailVerificationToken;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);
}
