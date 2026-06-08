package io.github.fbsarracini.javadesign.account;

import io.github.fbsarracini.javadesign.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Optional<Membership> findByAccountAndUser(Account account, User user);
    List<Membership> findByUser(User user);
    List<Membership> findByAccount(Account account);
}
