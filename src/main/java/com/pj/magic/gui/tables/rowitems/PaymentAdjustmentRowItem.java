package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;

import org.springframework.util.StringUtils;

import com.pj.magic.model.PaymentAdjustment;

public class PaymentAdjustmentRowItem {

	private PaymentAdjustment adjustment;
	private String adjustmentType;
	private String referenceNumber;
	private BigDecimal amount;

	public PaymentAdjustmentRowItem(PaymentAdjustment adjustment) {
		this.adjustment = adjustment;
		
		adjustmentType = adjustment.getAdjustmentType();
		referenceNumber = adjustment.getReferenceNumber();
		amount = adjustment.getAmount();
	}
	
	public PaymentAdjustment getAdjustment() {
		return adjustment;
	}

	public void setAdjustment(PaymentAdjustment adjustment) {
		this.adjustment = adjustment;
	}

	public String getAdjustmentType() {
		return adjustmentType;
	}

	public void setAdjustmentType(String adjustmentType) {
		this.adjustmentType = adjustmentType;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void reset() {
		adjustmentType = adjustment.getAdjustmentType();
		referenceNumber = adjustment.getReferenceNumber();
		amount = adjustment.getAmount();
	}

	public boolean isValid() {
		return !StringUtils.isEmpty(adjustmentType) && amount != null;
	}

	public boolean isUpdating() {
		return adjustment.getId() != null;
	}

}