package com.example.TP.payload.request;

import lombok.Data;

import java.time.LocalTime;
import java.util.Map;

@Data
public class HotelRequest extends BaseRequest{
    private String name;
    private String slug;
    private String address;
    private String phone;
    private String description;
    private Long destinationId;
    private int starRating;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private Map<String, Object> amenities;
}
