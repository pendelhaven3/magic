alter table PAYMENT_SALES_INVOICE drop foreign key PAYMENT_SALES_INVOICE$FK2;
alter table PAYMENT_SALES_INVOICE drop index PAYMENT_SALES_INVOICE$UK;
alter table PAYMENT_SALES_INVOICE add constraint PAYMENT_SALES_INVOICE$FK2 foreign key (SALES_INVOICE_ID) references SALES_INVOICE (ID);