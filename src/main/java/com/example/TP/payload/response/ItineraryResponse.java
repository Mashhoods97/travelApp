package com.example.TP.payload.response;

import lombok.Data;

@Data
public class ItineraryResponse extends BaseResponse{
    private Long packageId;
    private Integer dayNumber;
    private String description;
    private String title;
    private Boolean mealsInclude;
    private Boolean accommodation;
    private Boolean sightseeing;
}
