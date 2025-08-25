package com.example.TP.payload.request;

import lombok.Data;

@Data
public class UserRequest extends BaseRequest{
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private Long roleId;
    private Integer type;
    private BusinessRequest businessRequest;
}
