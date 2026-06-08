package io.github.fbsarracini.javadesign.invite;

import io.github.fbsarracini.javadesign.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InviteRepository extends JpaRepository<Invite, Long> {
    Optional<Invite> findByToken(String token);
    List<Invite> findByAccountAndStatusAndExpirationDateGreaterThanEqual(Account account, InviteStatus status, LocalDate today);
    boolean existsByAccountAndEmailAndStatusAndExpirationDateGreaterThanEqual(Account account, String email, InviteStatus status, LocalDate today);
}
