package com.hotel.customerservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.customerservice.entities.AnalyticsDatum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producers {

    private static final Logger logger = LoggerFactory.getLogger(Producers.class);

    private static final String TOPIC = "notification";

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    public void sendMessage(String message){
        logger.info(String.format("The notification message is --> %s",message));

        this.kafkaTemplate.send(TOPIC,message);
    }

    public void sendAnalyticsPayload (String type,String message, Object payload) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();

        AnalyticsDatum datum = new AnalyticsDatum();
        datum.setType(type);
        datum.setMessage(message);
        String payloadJson = mapper.writeValueAsString(payload);
        datum.setPayload(payloadJson);
        String outMessage = mapper.writeValueAsString(datum);
        sendMessage(outMessage);

    }
}
