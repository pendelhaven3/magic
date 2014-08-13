package com.pj.magic.model;

public class User {

	private Long id;
	private String username;

	public User() {
		// default constructor
	}
	
	public User(long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
