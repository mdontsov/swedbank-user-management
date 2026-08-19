package com.example.usermanagement.user.domain;

import com.example.usermanagement.user.api.UserRequest;
import com.example.usermanagement.user.api.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse create(UserRequest request);

    List<UserResponse> findAll();

    UserResponse update(long id, UserRequest request);
}
