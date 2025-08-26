package com.example.TP.controller;

import com.example.TP.enums.ResponseEnum;
import com.example.TP.payload.request.UserRequest;
import com.example.TP.payload.response.PageResponse;
import com.example.TP.payload.response.ResponseModel;
import com.example.TP.payload.response.UserResponse;
import com.example.TP.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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


    @PostMapping
    @PreAuthorize("hasRole('USER_CREATE')")
    public ResponseModel<?> create(@RequestBody UserRequest userRequest, @AuthenticationPrincipal UserDetails userDetails) {
        return userService.createUser(userRequest, userDetails);
    }

    @GetMapping("/paged")
    @PreAuthorize("hasRole('USER_VIEW')")
    public ResponseModel<PageResponse<UserResponse>> getAllUsers(
            Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "phone", required = false) String phone) {

        return userService.getAllUsers(pageable, userDetails, name, email, phone);
    }

    @ExceptionHandler(Exception.class)
    public ResponseModel<?> handleException(Exception e) {
        log.error("Exception occurred: {}", e.getMessage());
        if (e.getClass().getSimpleName().equalsIgnoreCase("AccessDeniedException")) {
            return new ResponseModel<>().failed(ResponseEnum.FORBIDDEN.getStatus(), "AccessDenied");
        } else {
            return new ResponseModel<>().failed(ResponseEnum.INTERNAL_SERVER_ERROR.getStatus(), e.getMessage());
        }
    }
}
