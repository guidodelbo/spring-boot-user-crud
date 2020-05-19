package com.guidodelbo.usercrud.ui.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class UserRest {

    private String userId;
    private String name;
    private String surname;
    private String email;
    private List<AddressRest> addresses;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public List<AddressRest> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressRest> addresses) {
        this.addresses = addresses;
    }
}
