package com.example.demo.entity;


import lombok.Data;

import java.util.UUID;

@Data
public class ReservationDto {
    private UUID id;
    private Integer checkinDate;
    private Integer checkoutDate;
    private String status;
    private String roomId;
    private Integer number;
    private String guest;
}
