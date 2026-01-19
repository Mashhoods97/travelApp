package com.example.TP.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "quotations")
@Data
@SuperBuilder
@NoArgsConstructor
@ToString
public class Quotation extends ExtendedModel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "package_id")
    private Long packageId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "booking_date")
    @Temporal(TemporalType.DATE)
    private Date bookingDate;

    @Column(name = "travel_date")
    @Temporal(TemporalType.DATE)
    private Date travelDate;

    @Column(name = "pac_count")
    private int paxCount;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "status")
    private int status;
}
