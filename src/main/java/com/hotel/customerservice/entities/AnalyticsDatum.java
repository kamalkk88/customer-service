package com.hotel.customerservice.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnalyticsDatum {
    String type;
    String message;
    String payload;
}
