package project.self_development.routine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.self_development.routine.domain.EmailVerification;

import java.util.Optional;

@Repository

public interface EmailRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmail(String email);
}
