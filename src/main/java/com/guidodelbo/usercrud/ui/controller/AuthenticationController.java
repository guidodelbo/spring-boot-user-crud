package com.guidodelbo.usercrud.ui.controller;

import com.guidodelbo.usercrud.ui.model.request.UserLoginRequestModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @ApiOperation("User Login")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Response Headers", responseHeaders = {
                @ResponseHeader(name = "authorization", description = "Bearer <JWT value here>"),
                @ResponseHeader(name = "userId", description = "<Public User Id value here>")
            })
    })
    @PostMapping("/users/login")
    public void theFakeLogin(@RequestBody UserLoginRequestModel loginRequestModel)
    {
        throw new IllegalStateException("This method should not be called. This method is implemented by Spring Security");
    }
}
