package com.pj.magic.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Supplier {

	private Long id;
	private String code;
	private String name;
	private String address;
	private String remarks;
	private String contactNumber;
	private String contactPerson;
	private String emailAddress;
	private String faxNumber;
	private String tin;
	private PaymentTerm paymentTerm;
	private String discount;
	private boolean vatInclusive;
	
	public Supplier() {
		// default constructor
	}
	
	public Supplier(Long id) {
		super();
		this.id = id;
	}

	public Supplier(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Supplier(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof Supplier)) {
            return false;
        }
        Supplier other = (Supplier)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}
	
	@Override
	public String toString() {
		return name;
	}

}