package com.guidodelbo.usercrud.service;

import com.guidodelbo.usercrud.shared.dto.UserDto;

public interface SchedulingService {

    void scheduleEmail(UserDto userDto);
}
