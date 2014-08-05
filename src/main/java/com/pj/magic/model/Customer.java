package com.pj.magic.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Customer {

	private Long id;
	private String code;
	private String name;
	private String address;

	public Customer() {
	}
	
	public Customer(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(code)
			.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof Customer)) {
            return false;
        }
        Customer other = (Customer)obj;		
		return new EqualsBuilder()
			.append(code, other.getCode())
			.isEquals();
	}
	
}
