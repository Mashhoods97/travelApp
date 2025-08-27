package com.example.TP.payload.request;

import lombok.Data;

@Data
public class DestinationRequest extends BaseRequest{
    private String name;
    private String slug;
    private String country;
    private String region;
    private String description;
    private String language;
    private String currency;
}
