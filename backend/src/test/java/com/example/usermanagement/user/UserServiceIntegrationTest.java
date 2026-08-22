package com.example.usermanagement.user;

import com.example.usermanagement.exception.DuplicatedEmailException;
import com.example.usermanagement.exception.UserNotFoundException;
import com.example.usermanagement.api.UserRequest;
import com.example.usermanagement.api.UserResponse;
import com.example.usermanagement.service.UserService;
import com.example.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clearDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateLoadAndUpdateUserAgainstH2() {
        UserResponse created = userService.create(new UserRequest(" Ada ", " Lovelace ", " ada@example.com "));

        assertThat(created.id()).isNotNull();
        assertThat(created.firstName()).isEqualTo("Ada");
        assertThat(userService.findAll()).containsExactly(created);

        UserResponse updated = userService.update(created.id(),
                new UserRequest("Augusta Ada", "Lovelace", "ada.lovelace@example.com"));

        assertThat(updated.firstName()).isEqualTo("Augusta Ada");
        assertThat(userRepository.findById(created.id())).get()
                .extracting("email")
                .isEqualTo("ada.lovelace@example.com");
    }

    @Test
    void shouldRejectUpdateWhenUserDoesNotExist() {
        UserRequest request = new UserRequest("Ada", "Lovelace", "ada@example.com");

        assertThatThrownBy(() -> userService.update(999_999, request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("999999");
    }

    @Test
    void shouldDeleteExistingUser() {
        UserResponse created = userService.create(
                new UserRequest("Ada", "Lovelace", "ada@example.com"));

        userService.delete(created.id());

        assertThat(userRepository.findById(created.id())).isEmpty();
    }

    @Test
    void shouldRejectDeleteWhenUserDoesNotExist() {
        assertThatThrownBy(() -> userService.delete(999_999))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("999999");
    }

    @Test
    void shouldRejectDuplicateEmailIgnoringCaseAndWhitespace() {
        userService.create(new UserRequest("Ada", "Lovelace", "Ada@Example.com"));

        assertThatThrownBy(() -> userService.create(
                new UserRequest("Grace", "Hopper", "  ada@example.com  ")))
                .isInstanceOf(DuplicatedEmailException.class)
                .hasMessage("Email is already registered");
    }

    @Test
    void shouldAllowKeepingOwnEmailButRejectUsingAnotherUsersEmail() {
        UserResponse ada = userService.create(new UserRequest("Ada", "Lovelace", "ada@example.com"));
        UserResponse grace = userService.create(new UserRequest("Grace", "Hopper", "grace@example.com"));

        assertThat(userService.update(ada.id(),
                new UserRequest("Ada", "Lovelace", "ADA@EXAMPLE.COM")).email())
                .isEqualTo("ada@example.com");

        assertThatThrownBy(() -> userService.update(grace.id(),
                new UserRequest("Grace", "Hopper", "ada@example.com")))
                .isInstanceOf(DuplicatedEmailException.class);
    }
}
