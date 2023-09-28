package com.hotel.customerservice.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FullReserveDtls
{
    private String username;
    private String phone;
    private String email;
    private String message;
    private String status;
    private Long reservationId;

}
