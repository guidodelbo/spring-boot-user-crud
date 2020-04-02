package com.guidodelbo.usercrud.controller;

import com.guidodelbo.usercrud.entity.User;
import com.guidodelbo.usercrud.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserService service;
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public List<User> findAllUsers() {
        return service.getUsers();
    }
    @GetMapping
    public User findUserByDni(@RequestParam(value = "dni") @Min(1) int dni) {
        return service.getUserByDni(dni);
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return service.saveUser(user);
    }
    @PostMapping("/list")
    public List<User> addUsers(@Valid @RequestBody List<User> users) {
        return service.saveUsers(users);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return service.updateUser(user);
    }

    @DeleteMapping
    public String deleteUser(@Valid @RequestParam(value = "id") int id) {
        return service.deleteUser(id);
    }
}
