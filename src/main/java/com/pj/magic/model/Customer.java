package com.pj.magic.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Entity
public class Customer {

	@Id
	@GeneratedValue
	private Long id;
	private String code;
	private String name;

	@Column(name = "BUSINESS_ADDRESS")
	private String businessAddress;

	@Column(name = "DELIVERY_ADDRESS")
	private String deliveryAddress;

	@Column(name = "CONTACT_PERSON")
	private String contactPerson;

	@Column(name = "CONTACT_NUMBER")
	private String contactNumber;

	private String tin;

	@Column(name = "APPROVED_CREDIT_LINE")
	private BigDecimal approvedCreditLine;

	@OneToOne
	@JoinColumn(name = "PAYMENT_TERM_ID")
	private PaymentTerm paymentTerm;

	@Column(name = "BUSINESS_TYPE")
	private String businessType;

	private String owners;

	@Column(name = "BANK_REFERENCES")
	private String bankReferences;

	@Column(name = "HOLD_IND")
	private String hold = "N"; // TODO: Convert to boolean

	private String remarks;

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

	public String getBusinessAddress() {
		return businessAddress;
	}

	public void setBusinessAddress(String businessAddress) {
		this.businessAddress = businessAddress;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(code).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Customer)) {
			return false;
		}
		Customer other = (Customer) obj;
		return new EqualsBuilder().append(code, other.getCode()).isEquals();
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public PaymentTerm getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(PaymentTerm paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public String getTin() {
		return tin;
	}

	public void setTin(String tin) {
		this.tin = tin;
	}

	public BigDecimal getApprovedCreditLine() {
		return approvedCreditLine;
	}

	public void setApprovedCreditLine(BigDecimal approvedCreditLine) {
		this.approvedCreditLine = approvedCreditLine;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getOwners() {
		return owners;
	}

	public void setOwners(String owners) {
		this.owners = owners;
	}

	public String getBankReferences() {
		return bankReferences;
	}

	public void setBankReferences(String bankReferences) {
		this.bankReferences = bankReferences;
	}

	public String getHold() {
		return hold;
	}

	public void setHold(String hold) {
		this.hold = hold;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
