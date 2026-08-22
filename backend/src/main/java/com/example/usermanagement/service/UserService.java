package com.example.usermanagement.service;

import com.example.usermanagement.api.UserRequest;
import com.example.usermanagement.api.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse create(UserRequest request);

    List<UserResponse> findAll();

    UserResponse update(long id, UserRequest request);
}
