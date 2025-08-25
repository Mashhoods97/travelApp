package com.example.TP.controller;

import com.example.TP.enums.ResponseEnum;
import com.example.TP.payload.request.UserRequest;
import com.example.TP.payload.response.ResponseModel;
import com.example.TP.payload.response.UserResponse;
import com.example.TP.repository.UserRepo;
import com.example.TP.service.BusinessService;
import com.example.TP.service.UserService;
import com.example.TP.utils.GeneralException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
@Log4j2
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private BusinessService businessService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepo userRepo;


    @PostMapping
    @PreAuthorize("hasRole('USER_CREATE')")
    public ResponseModel<?> create(@RequestBody UserRequest userRequest, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            ResponseModel<?> responseModel = userService.createUser(userRequest, userDetails);
            if (responseModel.getData() != null) {
                UserResponse userResponse = modelMapper.map(responseModel.getData(), UserResponse.class);
                return new ResponseModel<UserResponse>().success(ResponseEnum.CREATED.getStatus(),"Entity Created Successfully" ,userResponse);
            } else {
                return new ResponseModel<UserResponse>().failed(responseModel.getStatus(), responseModel.getMessage(), null);
            }
        } catch (GeneralException e) {
            log.error("Error creating User: {}", e.getMessage());
            return new ResponseModel<UserResponse>().failed(e.getHttpStatus().value(), e.getMessage(), null);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseModel<?> handleException(Exception e) {
        log.error("Exception occurred: {}", e.getMessage());
        if (e.getClass().getSimpleName().equalsIgnoreCase("AccessDeniedException")) {
            return new ResponseModel<>().failed(ResponseEnum.FORBIDDEN.getStatus(), "AccessDenied", null);
        } else {
            return new ResponseModel<>().failed(ResponseEnum.INTERNAL_SERVER_ERROR.getStatus(), e.getMessage(), null);
        }
    }
}
