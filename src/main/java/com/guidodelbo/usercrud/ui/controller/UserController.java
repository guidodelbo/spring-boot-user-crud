package com.guidodelbo.usercrud.ui.controller;

import com.guidodelbo.usercrud.exception.UserServiceException;
import com.guidodelbo.usercrud.service.impl.AddressServiceImpl;
import com.guidodelbo.usercrud.shared.dto.AddressDto;
import com.guidodelbo.usercrud.shared.dto.UserDto;
import com.guidodelbo.usercrud.ui.model.request.UserDetailsRequestModel;
import com.guidodelbo.usercrud.service.impl.UserServiceImpl;
import com.guidodelbo.usercrud.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
//@Validated
public class UserController {

    private final UserServiceImpl userService;
    private final AddressServiceImpl addressService;

    public UserController(UserServiceImpl userService, AddressServiceImpl addressService) {
        this.userService = userService;
        this.addressService = addressService;
    }

    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<UserRest>> getAllUsers(@RequestParam(value = "page", defaultValue = "1") int page,
                                                      @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<UserRest> returnValue = new ArrayList<>();
        List<UserDto> users = userService.getUsers(page, limit);

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

        UserDto userDto = userService.getUserByUserId(id);

        return new ModelMapper().map(userDto, UserRest.class);
    }

    @GetMapping(path = "/{id}/addresses",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public List<AddressRest> getUserAddresses(@PathVariable String id) {

        List<AddressDto> addressDto = addressService.getAddresses(id);

        List<AddressRest> returnValue = new ArrayList<>();

        if(addressDto != null && !addressDto.isEmpty())
        {
            Type listType = new TypeToken<List<AddressRest>>() {}.getType();
            returnValue = new ModelMapper().map(addressDto, listType);
        }

        return returnValue;
    }

    @GetMapping(path = "/{userId}/addresses/{addressId}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public AddressRest getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

        AddressDto addressDto = addressService.getAddress(userId, addressId);

        return new ModelMapper().map(addressDto, AddressRest.class);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest addUser(@RequestBody UserDetailsRequestModel userDetails) {

        if (userDetails.getName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.saveUser(userDto);

        return modelMapper.map(createdUser, UserRest.class);
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

        UserDto updatedUser = userService.updateUser(id, userDto);

        UserRest returnValue = new UserRest();
        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @DeleteMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return returnValue;
    }
}