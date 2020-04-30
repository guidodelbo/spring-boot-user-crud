package com.guidodelbo.usercrud.service;

import com.guidodelbo.usercrud.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto saveUser(UserDto user);
    UserDto getUser(String email);
    UserDto getUserByUserId(String email);
    UserDto updateUser(String userId, UserDto user);
    void deleteUser(String userId);
    List<UserDto> getUsers(int page, int limit);
    boolean verifyEmailToken(String token);
}
