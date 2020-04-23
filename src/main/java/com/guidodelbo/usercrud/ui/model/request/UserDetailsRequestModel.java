package com.guidodelbo.usercrud.ui.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
public class UserDetailsRequestModel {

//    @NotEmpty(message = "Please provide a name")
    private String name;

//    @NotEmpty(message = "Please provide a surname")
    private String surname;

//    @NotEmpty(message = "Please provide an email")
    private String email;

//    @NotEmpty(message = "Please provide a password")
    private String password;
}
