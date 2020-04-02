package com.guidodelbo.usercrud.service;

import com.guidodelbo.usercrud.CustomGlobalExceptionHandler;
import com.guidodelbo.usercrud.entity.User;
import com.guidodelbo.usercrud.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User saveUser(User user) {
        return repository.save(user);
    }

    public List<User> saveUsers(List<User> users) {
        return repository.saveAll(users);
    }

    public List<User> getUsers() {
        return repository.findAll();
    }

    public User getUserByDni(int dni) {
        return repository.findByDni(dni);
    }

    public User getUserByName(String name) {
        return repository.findByName(name);
    }

    public String deleteUser(int id) {
        repository.deleteById(id);

        return "User removed: ID " + id;
    }

    public User updateUser(User user) {
        User existingUser = repository.findById(user.getId()).orElse(null);

        existingUser.setDni(user.getDni());
        existingUser.setName(user.getName());
        existingUser.setSurname(user.getSurname());
        existingUser.setAge(user.getAge());

        return repository.save(existingUser);
    }
}
