package com.example.TP.payload.response;

import lombok.Data;

@Data
public class DestinationResponse extends BaseResponse{
    private String name;
    private String slug;
    private String country;
    private String region;
    private String description;
    private String language;
    private String currency;
}
