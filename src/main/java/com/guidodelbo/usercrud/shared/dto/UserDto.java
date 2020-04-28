package com.guidodelbo.usercrud.shared.dto;

import com.guidodelbo.usercrud.ui.model.request.AddressRequestModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
public class UserDto implements Serializable {

    private static final long serialVersionUID = 6835192601898364280L;
    private long id;
    private String userId;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String encryptedPassword;
    private String emailVerificationToken;
    private Boolean emailVerificationStatus = false;
    private List<AddressDto> addresses;
}