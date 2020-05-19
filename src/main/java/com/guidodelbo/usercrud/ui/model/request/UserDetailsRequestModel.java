package com.guidodelbo.usercrud.ui.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;


public class UserDetailsRequestModel {

//    @NotEmpty(message = "Please provide a name")
    private String name;

//    @NotEmpty(message = "Please provide a surname")
    private String surname;

//    @NotEmpty(message = "Please provide an email")
    private String email;

//    @NotEmpty(message = "Please provide a password")
    private String password;

    private List<AddressRequestModel> addresses;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<AddressRequestModel> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressRequestModel> addresses) {
        this.addresses = addresses;
    }
}
