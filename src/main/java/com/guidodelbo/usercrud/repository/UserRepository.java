package com.guidodelbo.usercrud.repository;

import com.guidodelbo.usercrud.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByName(String name);
}
