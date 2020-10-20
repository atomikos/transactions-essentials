package com.atomikos.spring.integrationtest;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Messages {

    @JmsListener(destination = "accounts")
    public void onMessage(String content) {
        System.out.println("----> " + content);
    }

}
