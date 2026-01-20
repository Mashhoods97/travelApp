package com.example.TP.payload.response;

import lombok.Data;

@Data
public class CustomerResponse extends BaseResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String phone;
}
