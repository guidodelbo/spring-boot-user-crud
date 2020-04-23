package com.guidodelbo.usercrud.ui.controller;

import com.guidodelbo.usercrud.exception.UserServiceException;
import com.guidodelbo.usercrud.shared.dto.UserDto;
import com.guidodelbo.usercrud.ui.model.request.UserDetailsRequestModel;
import com.guidodelbo.usercrud.service.UserServiceImpl;
import com.guidodelbo.usercrud.ui.model.response.ErrorMessages;
import com.guidodelbo.usercrud.ui.model.response.OperationStatusModel;
import com.guidodelbo.usercrud.ui.model.response.RequestOperationStatus;
import com.guidodelbo.usercrud.ui.model.response.UserRest;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
//@Validated
public class UserController {

    private final UserServiceImpl service;

    public UserController(UserServiceImpl service) {
        this.service = service;
    }

    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<UserRest>> getAllUsers(@RequestParam(value = "page", defaultValue = "1") int page,
                                                      @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<UserRest> returnValue = new ArrayList<>();
        List<UserDto> users = service.getUsers(page, limit);

        for (UserDto userDto : users) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }

        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUserByUserId(@PathVariable String id) {

        UserDto userDto = service.getUserByUserId(id);

        UserRest returnValue = new UserRest();
        BeanUtils.copyProperties(userDto, returnValue);

        return returnValue;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest addUser(@RequestBody UserDetailsRequestModel userDetails) {

        if (userDetails.getName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        UserRest returnValue = new UserRest();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto createdUser = service.saveUser(userDto);
        BeanUtils.copyProperties(createdUser, returnValue);

        return returnValue;
    }

//    @PostMapping("/list")
//    public List<UserDetailsRequestModel> addUsers(@Valid @RequestBody List<UserDetailsRequestModel> users) {
//        return service.saveUsers(users);
//    }

    @PutMapping(path = "/{id}", consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updatedUser = service.updateUser(id, userDto);

        UserRest returnValue = new UserRest();
        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @DeleteMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        service.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return returnValue;
    }
}
