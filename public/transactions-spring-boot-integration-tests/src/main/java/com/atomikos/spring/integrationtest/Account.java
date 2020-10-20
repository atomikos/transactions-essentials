package com.atomikos.spring.integrationtest;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    Account() {
    }

    public Account(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

}
