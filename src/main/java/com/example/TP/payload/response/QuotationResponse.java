package com.example.TP.payload.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class QuotationResponse extends BaseResponse {
    private String title;
    private Long packageId;
    private Long userId;
    private Long customerId;
    private String customerName;
    private Date bookingDate;
    private Date travelDate;
    private int paxCount;
    private BigDecimal totalPrice;
    private int status;
}
