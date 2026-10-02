package com.atomikos.examples.hibernate.entities;

public class User {
	
	private Long id;
	private String name;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return "a User(id=" + id + ", name=" + name + ")";
	}

}
