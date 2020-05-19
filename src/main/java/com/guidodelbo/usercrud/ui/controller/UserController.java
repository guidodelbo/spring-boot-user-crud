package com.guidodelbo.usercrud.ui.controller;

import com.guidodelbo.usercrud.exception.UserServiceException;
import com.guidodelbo.usercrud.service.impl.AddressServiceImpl;
import com.guidodelbo.usercrud.shared.Roles;
import com.guidodelbo.usercrud.shared.dto.AddressDto;
import com.guidodelbo.usercrud.shared.dto.UserDto;
import com.guidodelbo.usercrud.ui.model.request.PasswordResetModel;
import com.guidodelbo.usercrud.ui.model.request.PasswordResetRequestModel;
import com.guidodelbo.usercrud.ui.model.request.UserDetailsRequestModel;
import com.guidodelbo.usercrud.service.impl.UserServiceImpl;
import com.guidodelbo.usercrud.ui.model.response.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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

    /*
     * http://localhost:8080/user-crud/users/
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<UserRest>> getAllUsers(@RequestParam(value = "page", defaultValue = "1") int page,
                                                      @RequestParam(value = "limit", defaultValue = "10") int limit) {

        ModelMapper modelMapper = new ModelMapper();
        List<UserRest> returnValue = new ArrayList<>();
        List<UserDto> users = userService.getUsers(page, limit);

        for (UserDto userDto : users) {
            UserRest userModel = modelMapper.map(userDto, UserRest.class);
            returnValue.add(userModel);
        }

        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }

    /*
     * http://localhost:8080/user-crud/users/{userId}
     */
    @PostAuthorize(value = "hasRole('ADMIN') or returnObject.userId == principal.userId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUserByUserId(@PathVariable String id) {

        UserDto userDto = userService.getUserByUserId(id);

        return new ModelMapper().map(userDto, UserRest.class);
    }

    /*
     * http://localhost:8080/user-crud/users/{userId}/addresses
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(path = "/{id}/addresses",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public List<AddressRest> getUserAddresses(@PathVariable String id) {

        List<AddressDto> addressDto = addressService.getAddresses(id);

        List<AddressRest> returnValue = new ArrayList<>();

        if (addressDto != null && !addressDto.isEmpty()) {
            Type listType = new TypeToken<List<AddressRest>>() {
            }.getType();
            returnValue = new ModelMapper().map(addressDto, listType);
        }

        return returnValue;
    }

    /*
     * http://localhost:8080/user-crud/users/{userId}/addresses/{addressId}
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(path = "/{userId}/addresses/{addressId}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public AddressRest getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

        AddressDto addressDto = addressService.getAddress(userId, addressId);

        return new ModelMapper().map(addressDto, AddressRest.class);
    }

    /*
     *   http://localhost:8080/user-crud/users/email-verification?token=
     **/
    @GetMapping(path = "/email-verification", produces = {MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token);

        if (isVerified)
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        else
            returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        return returnValue;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest addUser(@RequestBody UserDetailsRequestModel userDetails) {

        if (userDetails.getName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);
        userDto.setRoles(new HashSet<>(Arrays.asList(Roles.ROLE_USER.name())));

        UserDto createdUser = userService.saveUser(userDto);

        return modelMapper.map(createdUser, UserRest.class);
    }

//    @PostMapping("/list")
//    public List<UserDetailsRequestModel> addUsers(@Valid @RequestBody List<UserDetailsRequestModel> users) {
//        return service.saveUsers(users);
//    }


    /*
     * http://localhost:8080/user-crud/users/{id}
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
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

    /*
     * http://localhost:8080/user-crud/users/{id}
     */
//    @Secured("ROLE_ADMIN")
//    @PreAuthorize(value = "hasAuthority('DELETE_AUTHORITY')")
    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or #id == principal.userId")
//    @PostAuthorize
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @DeleteMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return returnValue;
    }


    /*
     * http://localhost:8080/user-crud/users/password-reset-request
     * */
    @PostMapping(path = "/password-reset-request",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel requestReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {

        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());

        returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if (operationResult)
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return returnValue;
    }


    /*
     * http://localhost:8080/user-crud/users/password-reset
     * */
    @PostMapping(path = "/password-reset",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.resetPassword(
                passwordResetModel.getToken(),
                passwordResetModel.getPassword());

        returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if (operationResult) {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return returnValue;
    }
}
