package com.example.TP.payload.response;

import lombok.Data;

@Data
public class UserResponse extends BaseResponse {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private Long roleId;
    private Integer type;
    private BusinessResponse businessResponse;
}
