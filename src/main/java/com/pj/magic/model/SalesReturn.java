package com.pj.magic.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

public class SalesReturn {

	private Long id;
	private Long salesReturnNumber;
	private SalesInvoice salesInvoice;
	private boolean posted;
	private List<SalesReturnItem> items = new ArrayList<>();

	public SalesReturn() {
		// default constructor
	}
	
	public SalesReturn(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSalesReturnNumber() {
		return salesReturnNumber;
	}

	public void setSalesReturnNumber(Long salesReturnNumber) {
		this.salesReturnNumber = salesReturnNumber;
	}

	public SalesInvoice getSalesInvoice() {
		return salesInvoice;
	}

	public void setSalesInvoice(SalesInvoice salesInvoice) {
		this.salesInvoice = salesInvoice;
	}

	public List<SalesReturnItem> getItems() {
		return items;
	}

	public void setItems(List<SalesReturnItem> items) {
		this.items = items;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public String getStatus() {
		return posted ? "Posted" : "New";
	}

	public List<SalesReturnItem> getItemsForEditing() {
		List<SalesReturnItem> items = new ArrayList<>();
		for (SalesInvoiceItem salesInvoiceItem : salesInvoice.getItems()) {
			final SalesInvoiceItem referenceItem = salesInvoiceItem;
			SalesReturnItem item = (SalesReturnItem)CollectionUtils.find(this.items, new Predicate() {
				
				@Override
				public boolean evaluate(Object object) {
					return ((SalesReturnItem)object).getItem().equals(referenceItem);
				}
			});
			if (item == null) {
				item = new SalesReturnItem();
				item.setItem(referenceItem);
			}
			items.add(item);
		}
		return items;
	}
	
}