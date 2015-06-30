alter table BAD_STOCK_RETURN_ITEM add SALES_INVOICE_NO integer null;
alter table BAD_STOCK_RETURN_ITEM add constraint BAD_STOCK_RETURN_ITEM$FK3 foreign key (SALES_INVOICE_NO) references SALES_INVOICE (SALES_INVOICE_NO);
