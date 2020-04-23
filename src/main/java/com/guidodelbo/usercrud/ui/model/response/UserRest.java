package com.guidodelbo.usercrud.ui.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UserRest {

    private String userId;
    private String name;
    private String surname;
    private String email;
}
