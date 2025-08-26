package com.example.TP.payload.response;

import lombok.Data;

@Data
public class RoleResponse extends BaseResponse{
    private String title;
    private String description;
}
