package com.example.usermanagement.user;

import com.example.usermanagement.exception.DuplicatedEmailException;
import com.example.usermanagement.exception.UserNotFoundException;
import com.example.usermanagement.api.UserRequest;
import com.example.usermanagement.api.UserResponse;
import com.example.usermanagement.api.controller.UserController;
import com.example.usermanagement.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldCreateUserAndReturnCreatedResponse() throws Exception {
        UserResponse response = new UserResponse(1L, "Ada", "Lovelace", "ada@example.com");
        when(userService.create(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Ada","lastName":"Lovelace","email":"ada@example.com"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("ada@example.com"));

        verify(userService).create(any(UserRequest.class));
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of(
                new UserResponse(1L, "Ada", "Lovelace", "ada@example.com")));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Ada"));
    }

    @Test
    void shouldDeleteUserAndReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/42"))
                .andExpect(status().isNoContent());

        verify(userService).delete(42);
    }

    @Test
    void shouldReturnFieldErrorsWhenCreateRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"","lastName":"","email":"not-an-email"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors.firstName").exists())
                .andExpect(jsonPath("$.fieldErrors.lastName").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatedUserDoesNotExist() throws Exception {
        when(userService.update(anyLong(), any(UserRequest.class)))
                .thenThrow(new UserNotFoundException(42));

        mockMvc.perform(put("/api/users/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Ada","lastName":"Lovelace","email":"ada@example.com"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User with id 42 was not found"))
                .andExpect(jsonPath("$.path").value("/api/users/42"));
    }

    @Test
    void shouldReturnSanitizedErrorForUnexpectedRuntimeException() throws Exception {
        when(userService.findAll()).thenThrow(new RuntimeException("secret internal detail"));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("An unexpected error occurred"));
    }

    @Test
    void shouldReturnConflictWhenEmailIsAlreadyRegistered() throws Exception {
        when(userService.create(any(UserRequest.class)))
                .thenThrow(new DuplicatedEmailException());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Ada","lastName":"Lovelace","email":"ada@example.com"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email is already registered"))
                .andExpect(jsonPath("$.fieldErrors.email").value("Email is already registered"));
    }
}
