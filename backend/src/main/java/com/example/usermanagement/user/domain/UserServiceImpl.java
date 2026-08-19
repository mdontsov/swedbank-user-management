package com.example.usermanagement.user.domain;

import com.example.usermanagement.common.error.UserNotFoundException;
import com.example.usermanagement.user.api.UserRequest;
import com.example.usermanagement.user.api.UserResponse;
import com.example.usermanagement.user.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponse create(UserRequest request) {
        User user = new User(clean(request.firstName()), clean(request.lastName()), clean(request.email()));
        return UserResponse.from(userRepository.save(user));
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll(Sort.by("lastName", "firstName", "id"))
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse update(long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.update(clean(request.firstName()), clean(request.lastName()), clean(request.email()));
        return UserResponse.from(user);
    }

    private String clean(String value) {
        return value.trim();
    }
}
