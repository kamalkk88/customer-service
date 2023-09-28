package com.hotel.customerservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hotel.customerservice.entities.*;
import com.hotel.customerservice.services.Producers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class CustomerServiceController {

    @Autowired
    CustomerCredentialRepository  credentialRepository;

    @Autowired
    CustomerDetailsRepository customerDetailsRepository;

    @Autowired
    WebClient.Builder webClientBuilder;

    Logger logger = LoggerFactory.getLogger(CustomerServiceController.class);

    @Autowired
    Producers producers;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody CustomerCredential credential) throws JsonProcessingException {

        credentialRepository.save(credential);

        logger.info("New SignUp Has been completed for a customer to the hotel.");

        return new ResponseEntity<>("New Signup Successful", HttpStatus.OK);
    }

    @PostMapping("/save/customer-details")
    public ResponseEntity<String> saveCustomerDtls(@RequestBody CustomerDetails customerDetails) throws Exception {
        logger.info("Inside save of Customer Details");

        customerDetailsRepository.save(customerDetails);

        return new ResponseEntity<>("Customer details saved properly.",HttpStatus.OK);
    }

    @GetMapping("get/customer/detail/{username}")
    public ResponseEntity<CustomerDetails> getCustomerDtl(@PathVariable("username") String userName){
        logger.info("Inside getCustomerDtl for a provided customer");

        CustomerDetails custDtls= customerDetailsRepository.findById(userName).get();

        return new ResponseEntity<>(custDtls,HttpStatus.OK);
    }

    @PostMapping("/hotel/checkin/{username}/{roomId}")
    public ResponseEntity<String> checkIntoHotel(@PathVariable("username") String userName,@PathVariable("roomId") String roomId){

        logger.info("Inside check in of user name : "+ userName);

        String response =webClientBuilder.build().post()
                .uri("http://localhost:8072/hotelmanage-service/api/v1/room/checkin/"+userName+"/"+roomId)
                .retrieve().bodyToMono(String.class).block();

        return new ResponseEntity<>("Check In Successfull "+response,HttpStatus.OK);
    }

    @PostMapping("/hotel/checkout/{username}/{reservationId}/{paymentType}")
    public ResponseEntity<String> checkOutFromHotel(@PathVariable("username") String userName,
                                                    @PathVariable("reservationId") String reservationId,
                                                    @PathVariable("paymentType") String paymentType) throws JsonProcessingException {
        String response = webClientBuilder.build().post()
                .uri("http://localhost:8072/reservation-service/api/v1/room/checkout/"
                +userName+"/"+reservationId+"/"+paymentType).retrieve().bodyToMono(String.class).block();

        CustomerDetails custDtls = customerDetailsRepository.findById(userName).get();

        FullReserveDtls reserveDtls = getFullReserveDtls(reservationId, custDtls);

        if(response.equalsIgnoreCase("CheckOut Done")){
            //send notification -Kafka
            producers.sendAnalyticsPayload("Checkout","Payment Done",reserveDtls);
        }
        else {
            response = "Not sent";
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    private static FullReserveDtls getFullReserveDtls(String reservationId, CustomerDetails custDtls) {
        FullReserveDtls reserveDtls = new FullReserveDtls();
        reserveDtls.setReservationId(Long.getLong(reservationId));
        reserveDtls.setPhone(custDtls.getPhone());
        reserveDtls.setEmail(custDtls.getEmail());
        return reserveDtls;
    }
}
