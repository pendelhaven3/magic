package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.gui.panels.EcashType;
import com.pj.magic.model.EcashReceiver;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentEcashPaymentSearchCriteria {

	private EcashReceiver ecashReceiver;
	private Date dateFrom;
	private Date dateTo;
	private EcashType ecashType;

}