package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentEcashPayment {

	private Long id;
	private Payment parent;
	private BigDecimal amount;
	private EcashReceiver ecashReceiver;
	private String referenceNumber;
	private Date receivedDate;
	private User receivedBy;

}