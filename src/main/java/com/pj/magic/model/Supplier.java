package com.pj.magic.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Supplier {

	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private String address;
	
	public Supplier() {
		// default constructor
	}
	
	public Supplier(Long id) {
		this.id = id;
	}

	@Column(name="CONTACT_NUMBER")
	private String contactNumber;
	
	@Column(name="CONTACT_PERSON")
	private String contactPerson;

	@Column(name="FAX_NUMBER")
	private String faxNumber;
	
	@Column(name="EMAIL_ADDRESS")
	private String emailAddress;

	private String tin;
	private PaymentTerm paymentTerm;
	
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getTin() {
		return tin;
	}

	public void setTin(String tin) {
		this.tin = tin;
	}

	public PaymentTerm getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(PaymentTerm paymentTerm) {
		this.paymentTerm = paymentTerm;
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
