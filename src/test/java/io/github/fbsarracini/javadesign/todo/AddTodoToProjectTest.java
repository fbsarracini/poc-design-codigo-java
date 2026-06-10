package io.github.fbsarracini.javadesign.todo;

import io.github.fbsarracini.javadesign.account.Account;
import io.github.fbsarracini.javadesign.exception.ForbiddenException;
import io.github.fbsarracini.javadesign.exception.NotFoundException;
import io.github.fbsarracini.javadesign.project.Project;
import io.github.fbsarracini.javadesign.project.ProjectMembershipRepository;
import io.github.fbsarracini.javadesign.project.ProjectRepository;
import io.github.fbsarracini.javadesign.user.User;
import io.github.fbsarracini.javadesign.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import static io.github.fbsarracini.javadesign.TestFixtures.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddTodoToProjectTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMembershipRepository projectMembershipRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private AddTodoToProject addTodoToProject;

    private User actor;
    private User assignee;
    private Account account;
    private Project project;

    @BeforeEach
    void setUp() {
        account = account("Conta Teste");
        actor = user("Actor", "actor@test.com");
        assignee = user("Assignee", "assignee@test.com");
        project = project("Projeto", account);
    }

    @Test
    void shouldAddTodoWithAssignee() {
        NewTodoData request = new NewTodoRequest("Tarefa", 2L, null, false);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMembershipRepository.existsByProjectAndUser(project, actor)).thenReturn(true);
        when(userRepository.findById(2L)).thenReturn(Optional.of(assignee));
        ArgumentCaptor<Todo> todoCaptor = ArgumentCaptor.forClass(Todo.class);
        when(todoRepository.save(todoCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        Todo result = addTodoToProject.execute(1L, actor, request);

        assertThat(result.getTitle()).isEqualTo("Tarefa");
        assertThat(result.getAssignee()).isEqualTo(assignee);
    }

    @Test
    void shouldAddTodoWithoutAssignee() {
        NewTodoData request = new NewTodoRequest("Tarefa", null, null, false);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMembershipRepository.existsByProjectAndUser(project, actor)).thenReturn(true);
        ArgumentCaptor<Todo> todoCaptor = ArgumentCaptor.forClass(Todo.class);
        when(todoRepository.save(todoCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        Todo result = addTodoToProject.execute(1L, actor, request);

        assertThat(result.getAssignee()).isNull();
        verify(userRepository, never()).findById(any());
    }

    @Test
    void shouldThrowWhenProjectNotFound() {
        NewTodoData request = new NewTodoRequest("Tarefa", null, null, false);
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addTodoToProject.execute(99L, actor, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowForbiddenWhenNotProjectMember() {
        NewTodoData request = new NewTodoRequest("Tarefa", null, null, false);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMembershipRepository.existsByProjectAndUser(project, actor)).thenReturn(false);

        assertThatThrownBy(() -> addTodoToProject.execute(1L, actor, request))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void shouldThrowWhenAssigneeNotFound() {
        NewTodoData request = new NewTodoRequest("Tarefa", 99L, null, false);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMembershipRepository.existsByProjectAndUser(project, actor)).thenReturn(true);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addTodoToProject.execute(1L, actor, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldAddTodoVisibleToClient() {
        NewTodoData request = new NewTodoRequest("Tarefa Pública", null, null, true);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMembershipRepository.existsByProjectAndUser(project, actor)).thenReturn(true);
        ArgumentCaptor<Todo> todoCaptor = ArgumentCaptor.forClass(Todo.class);
        when(todoRepository.save(todoCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        Todo result = addTodoToProject.execute(1L, actor, request);

        assertThat(result.isVisibleToClient()).isTrue();
    }
}
