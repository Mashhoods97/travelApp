package com.example.TP.payload.request;

import lombok.Data;

@Data
public class CustomerRequest extends BaseRequest{
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
