package com.example.TP.payload.request;

import lombok.Data;

@Data
public class ItineraryRequest extends BaseRequest{
    private Long packageId;
    private Integer dayNumber;
    private String description;
    private String title;
    private Boolean mealsInclude;
    private Boolean accommodation;
    private Boolean sightseeing;
}
