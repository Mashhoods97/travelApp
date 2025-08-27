package com.example.TP.payload.request;

import lombok.Data;

import java.util.Date;

@Data
public class PackageRequest extends BaseRequest{
    private String name;
    private String code;
    private String description;
    private Integer durationDays;
    private Integer durationNights;
    private Integer type;
    private Date validFrom;
    private Date validTo;
    private Long destinationId;
    private Long hotelId;
}
