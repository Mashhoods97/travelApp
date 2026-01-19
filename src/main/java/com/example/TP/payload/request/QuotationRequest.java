package com.example.TP.payload.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class QuotationRequest extends BaseRequest{
    private Long title;
    private Long packageId;
    private Long userId;
    private Long customerId;
    private Date bookingDate;
    private Date travelDate;
    private int paxCount;
    private BigDecimal totalPrice;
    private int status;
}
