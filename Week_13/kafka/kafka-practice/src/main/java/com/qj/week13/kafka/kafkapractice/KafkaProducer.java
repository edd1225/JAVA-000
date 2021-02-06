package com.qj.week13.kafka.kafkapractice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<Integer,String> kafkaTemplate;

    public void send(){
        kafkaTemplate.send("test",1,"msgData");
    }

}
