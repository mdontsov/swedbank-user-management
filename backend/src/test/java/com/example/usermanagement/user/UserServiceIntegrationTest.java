package com.example.usermanagement.user;

import com.example.usermanagement.common.error.UserNotFoundException;
import com.example.usermanagement.user.api.UserRequest;
import com.example.usermanagement.user.api.UserResponse;
import com.example.usermanagement.user.domain.UserService;
import com.example.usermanagement.user.persistence.UserRepository;
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
    void createsLoadsAndUpdatesUserAgainstH2() {
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
    void rejectsUpdateForUnknownUser() {
        UserRequest request = new UserRequest("Ada", "Lovelace", "ada@example.com");

        assertThatThrownBy(() -> userService.update(999_999, request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("999999");
    }
}

