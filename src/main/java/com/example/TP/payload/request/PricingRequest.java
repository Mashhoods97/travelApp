package com.example.TP.payload.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PricingRequest extends BaseRequest{
    private Long packageId;
    private BigDecimal basePrice;
    private BigDecimal discountPrice;
    private BigDecimal infantPrice;
    private String currency;
    private BigDecimal taxPercentage;
    private Date validFrom;
    private Date validTo;
}
