package io.github.fbsarracini.javadesign.project;

import io.github.fbsarracini.javadesign.account.Account;
import io.github.fbsarracini.javadesign.account.AccountRepository;
import io.github.fbsarracini.javadesign.account.Membership;
import io.github.fbsarracini.javadesign.account.MembershipRepository;
import io.github.fbsarracini.javadesign.account.Role;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListAccountProjectsTest {

    @Mock private AccountRepository accountRepository;
    @Mock private MembershipRepository membershipRepository;
    @Mock private ProjectRepository projectRepository;

    @InjectMocks private ListAccountProjects listAccountProjects;

    private User user;
    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account("Conta Teste");
        user = new User("User", "user@test.com", "hash");
    }

    @Test
    void shouldReturnProjectsForMember() {
        Project p1 = new Project("Projeto 1", account);
        Project p2 = new Project("Projeto 2", account);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, user))
                .thenReturn(Optional.of(new Membership(account, user, Role.MEMBER)));
        when(projectRepository.findByAccount(account)).thenReturn(List.of(p1, p2));

        List<ProjectSummaryResponse> result = listAccountProjects.execute(1L, user);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ProjectSummaryResponse::name)
                .containsExactly("Projeto 1", "Projeto 2");
    }

    @Test
    void shouldReturnEmptyListWhenNoProjects() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, user))
                .thenReturn(Optional.of(new Membership(account, user, Role.MEMBER)));
        when(projectRepository.findByAccount(account)).thenReturn(List.of());

        List<ProjectSummaryResponse> result = listAccountProjects.execute(1L, user);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listAccountProjects.execute(99L, user))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowForbiddenWhenNotMember() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(membershipRepository.findByAccountAndUser(account, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listAccountProjects.execute(1L, user))
                .isInstanceOf(ForbiddenException.class);
    }
}
