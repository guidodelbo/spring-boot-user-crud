package com.guidodelbo.usercrud.shared;

import com.guidodelbo.usercrud.shared.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

public class EmailScheduler implements Runnable, Serializable {

    @Autowired
    private AmazonSES amazonSES;

    private UserDto userDto;

    public EmailScheduler(UserDto userDto) {
        this.userDto = userDto;
    }

    @Override
    public void run() {
        try {

            amazonSES.verifyEmail(userDto);

        } catch(Exception e) {
            //TODO: loggear exception
            System.out.println(e.getMessage());
        }
    }
}
