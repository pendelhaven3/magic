package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.pj.magic.model.EcashReceiver;
import com.pj.magic.model.PaymentEcashPayment;
import com.pj.magic.model.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentEcashPaymentRowItem {

	private PaymentEcashPayment ecashPayment;
	private BigDecimal amount;
	private EcashReceiver ecashReceiver;
	private String referenceNumber;
	private Date receivedDate;
	private User receivedBy;

	public PaymentEcashPaymentRowItem(PaymentEcashPayment ecashPayment) {
		this.ecashPayment = ecashPayment;
		amount = ecashPayment.getAmount();
		ecashReceiver = ecashPayment.getEcashReceiver();
		referenceNumber = ecashPayment.getReferenceNumber();
		receivedDate = ecashPayment.getReceivedDate();
		receivedBy = ecashPayment.getReceivedBy();
	}
	
	public void reset() {
		amount = ecashPayment.getAmount();
		ecashReceiver = ecashPayment.getEcashReceiver();
		referenceNumber = ecashPayment.getReferenceNumber();
		receivedDate = ecashPayment.getReceivedDate();
		receivedBy = ecashPayment.getReceivedBy();
	}

	public boolean isValid() {
		return amount != null && ecashReceiver != null && !StringUtils.isEmpty(referenceNumber)
				&& receivedDate != null && receivedBy != null;
	}

	public boolean isUpdating() {
		return ecashPayment.getId() != null;
	}

}