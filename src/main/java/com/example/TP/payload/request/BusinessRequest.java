package com.example.TP.payload.request;

import lombok.Data;

@Data
public class BusinessRequest extends BaseRequest{
    private String title;
    private String description;
}
