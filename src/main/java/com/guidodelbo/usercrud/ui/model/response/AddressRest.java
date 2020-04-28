package com.guidodelbo.usercrud.ui.model.response;

import com.guidodelbo.usercrud.shared.dto.UserDto;
import lombok.Data;

@Data
public class AddressRest {

    private String addressId;
    private String city;
    private String country;
    private String streetName;
    private String postalCode;
    private String type;
}
