package com.atomikos.examples.jpa;

public interface TestDao {

	Account create(Account a);

	Account retreive(int id);

}