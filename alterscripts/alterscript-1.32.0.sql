alter table SUPP_PAYMENT_PAYMNT_ADJ drop column ADJUSTMENT_TYPE_ID;
alter table SUPP_PAYMENT_PAYMNT_ADJ change PURCHASE_PAYMENT_ADJ_TYPE_ID PURCHASE_PAYMENT_ADJ_TYPE_ID integer not null;
alter table SUPP_PAYMENT_PAYMNT_ADJ change REFERENCE_NO REFERENCE_NO varchar(30) not null;

rename table SUPPLIER_PAYMENT to PURCHASE_PAYMENT;
alter table PURCHASE_PAYMENT change SUPPLIER_PAYMENT_NO PURCHASE_PAYMENT_NO integer not null;

update SEQUENCE set NAME = 'PURCHASE_PAYMENT_NO_SEQ' where NAME = 'SUPPLIER_PAYMENT_NO_SEQ';
alter table PURCHASE_PAYMENT drop foreign key SUPPLIER_PAYMENT$FK, add constraint PURCHASE_PAYMENT$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID);
alter table PURCHASE_PAYMENT drop foreign key SUPPLIER_PAYMENT$FK2, add constraint PURCHASE_PAYMENT$FK2 foreign key (POST_BY) references USER (ID);
alter table PURCHASE_PAYMENT drop foreign key SUPPLIER_PAYMENT$FK3, add constraint PURCHASE_PAYMENT$FK3 foreign key (ENCODER) references USER (ID);
alter table PURCHASE_PAYMENT drop foreign key SUPPLIER_PAYMENT$FK4, add constraint PURCHASE_PAYMENT$FK4 foreign key (CANCEL_BY) references USER (ID);

rename table SUPP_PAYMENT_RECV_RCPT to PURCHASE_PAYMENT_RECEIVING_RECEIPT;

