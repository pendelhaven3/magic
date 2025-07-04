package com.pj.magic.gui.tables.rowitems;

import com.pj.magic.model.SalesComplianceProjectSalesInvoiceItem;
import com.pj.magic.util.NumberUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesComplianceProjectSalesInvoiceItemRowItem {

	private SalesComplianceProjectSalesInvoiceItem item;
	private String quantity;

	public SalesComplianceProjectSalesInvoiceItemRowItem(SalesComplianceProjectSalesInvoiceItem item) {
		this.item = item;
		quantity = item.getQuantity().toString();
	}
	
	public boolean isValid() {
		return NumberUtil.isAmount(quantity);
	}

}
