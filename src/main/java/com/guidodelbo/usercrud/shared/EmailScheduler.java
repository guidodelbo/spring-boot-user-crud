package com.guidodelbo.usercrud.shared;

import com.guidodelbo.usercrud.shared.dto.UserDto;
import org.redisson.api.RedissonClient;
import org.redisson.api.annotation.RInject;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailScheduler implements Runnable {

    @Autowired
    private AmazonSES amazonSES;

    @RInject
    private RedissonClient redissonClient;

    private UserDto userDto;

    public EmailScheduler(UserDto userDto) {
        this.userDto = userDto;
    }

    @Override
    public void run() {
        amazonSES.verifyEmail(userDto);
    }
}
