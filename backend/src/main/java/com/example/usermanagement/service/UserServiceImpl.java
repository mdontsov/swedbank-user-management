package com.example.usermanagement.service;

import com.example.usermanagement.exception.DuplicatedEmailException;
import com.example.usermanagement.exception.UserNotFoundException;
import com.example.usermanagement.api.UserRequest;
import com.example.usermanagement.api.UserResponse;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponse create(UserRequest request) {
        String email = format(request.email());
        ensureEmailAvailable(email);
        User user = new User(normalize(request.firstName()), normalize(request.lastName()), email);
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
        String email = format(request.email());
        ensureEmailAvailableForUpdate(email, id);
        user.update(normalize(request.firstName()), normalize(request.lastName()), email);
        return UserResponse.from(user);
    }

    private void ensureEmailAvailable(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicatedEmailException();
        }
    }

    private void ensureEmailAvailableForUpdate(String email, long id) {
        if (userRepository.existsByEmailAndIdNot(email, id)) {
            throw new DuplicatedEmailException();
        }
    }

    private String normalize(String value) {
        return value.trim();
    }

    private String format(String email) {
        return normalize(email).toLowerCase(Locale.ROOT);
    }
}
