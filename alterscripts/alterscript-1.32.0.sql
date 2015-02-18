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
alter table PURCHASE_PAYMENT_RECEIVING_RECEIPT change SUPPLIER_PAYMENT_ID PURCHASE_PAYMENT_ID integer not null;
alter table PURCHASE_PAYMENT_RECEIVING_RECEIPT drop foreign key SUPP_PAYMENT_RECV_RCPT$FK, add constraint PURCHASE_PAYMENT_RECEIVING_RECEIPT$FK foreign key (PURCHASE_PAYMENT_ID) references PURCHASE_PAYMENT (ID);
alter table PURCHASE_PAYMENT_RECEIVING_RECEIPT drop foreign key SUPP_PAYMENT_RECV_RCPT$FK2, add constraint PURCHASE_PAYMENT_RECEIVING_RECEIPT$FK2 foreign key (RECEIVING_RECEIPT_ID) references RECEIVING_RECEIPT (ID);

rename table SUPP_PAYMENT_CASH_PYMNT to PURCHASE_PAYMENT_CASH_PAYMENT;
alter table PURCHASE_PAYMENT_CASH_PAYMENT change SUPPLIER_PAYMENT_ID PURCHASE_PAYMENT_ID integer not null;
alter table PURCHASE_PAYMENT_CASH_PAYMENT drop foreign key SUPP_PAYMENT_CASH_PYMNT$FK, add constraint PURCHASE_PAYMENT_CASH_PAYMENT$FK foreign key (PURCHASE_PAYMENT_ID) references PURCHASE_PAYMENT (ID);
alter table PURCHASE_PAYMENT_CASH_PAYMENT drop foreign key SUPP_PAYMENT_CASH_PYMNT$FK2, add constraint PURCHASE_PAYMENT_CASH_PAYMENT$FK2 foreign key (PAID_BY) references USER (ID);

rename table SUPP_PAYMENT_CHECK_PYMNT to PURCHASE_PAYMENT_CHECK_PAYMENT;
alter table PURCHASE_PAYMENT_CHECK_PAYMENT change SUPPLIER_PAYMENT_ID PURCHASE_PAYMENT_ID integer not null;
alter table PURCHASE_PAYMENT_CHECK_PAYMENT drop foreign key SUPP_PAYMENT_CHECK_PYMNT$FK, add constraint PURCHASE_PAYMENT_CHECK_PAYMENT$FK foreign key (PURCHASE_PAYMENT_ID) references PURCHASE_PAYMENT (ID);

rename table SUPP_PAYMENT_CREDITCARD_PYMNT to PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT;
alter table PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT change SUPPLIER_PAYMENT_ID PURCHASE_PAYMENT_ID integer not null;
alter table PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT drop foreign key SUPP_PAYMENT_CREDITCARD_PYMNT$FK, add constraint PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT$FK foreign key (PURCHASE_PAYMENT_ID) references PURCHASE_PAYMENT (ID);
alter table PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT drop foreign key SUPP_PAYMENT_CREDITCARD_PYMNT$FK2, add constraint PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT$FK2 foreign key (CREDIT_CARD_ID) references CREDIT_CARD (ID);

rename table SUPP_PAYMENT_BANK_TRANSFER to PURCHASE_PAYMENT_BANK_TRANSFER;
alter table PURCHASE_PAYMENT_BANK_TRANSFER change SUPPLIER_PAYMENT_ID PURCHASE_PAYMENT_ID integer not null;
alter table PURCHASE_PAYMENT_BANK_TRANSFER drop foreign key SUPP_PAYMENT_BANK_TRANSFER$FK, add constraint PURCHASE_PAYMENT_BANK_TRANSFER$FK foreign key (PURCHASE_PAYMENT_ID) references PURCHASE_PAYMENT (ID);

rename table SUPP_PAYMENT_ADJUSTMENT to PURCHASE_PAYMENT_ADJUSTMENT;
alter table PURCHASE_PAYMENT_ADJUSTMENT change SUPP_PAYMENT_ADJUSTMENT_NO PURCHASE_PAYMENT_ADJUSTMENT_NO integer not null;
alter table PURCHASE_PAYMENT_ADJUSTMENT drop index SUPP_PAYMENT_ADJUSTMENT$UK, add unique index PURCHASE_PAYMENT_ADJUSTMENT$UK (PURCHASE_PAYMENT_ADJUSTMENT_NO);
alter table PURCHASE_PAYMENT_ADJUSTMENT drop foreign key SUPP_PAYMENT_ADJUSTMENT$FK, add constraint PURCHASE_PAYMENT_ADJUSTMENT$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID);
alter table PURCHASE_PAYMENT_ADJUSTMENT drop foreign key SUPP_PAYMENT_ADJUSTMENT$FK2, add constraint PURCHASE_PAYMENT_ADJUSTMENT$FK2 foreign key (PURCHASE_PAYMENT_ADJ_TYPE_ID) references PURCHASE_PAYMENT_ADJ_TYPE (ID);
alter table PURCHASE_PAYMENT_ADJUSTMENT drop foreign key SUPP_PAYMENT_ADJUSTMENT$FK3, add constraint PURCHASE_PAYMENT_ADJUSTMENT$FK3 foreign key (POST_BY) references USER (ID);
update SEQUENCE set NAME = 'PURCHASE_PAYMENT_ADJUSTMENT_NO_SEQ' where NAME = 'SUPP_PAYMENT_ADJUSTMENT_NO_SEQ';

rename table SUPP_PAYMENT_PAYMNT_ADJ to PURCHASE_PAYMENT_PAYMENT_ADJUSTMENT;
alter table PURCHASE_PAYMENT_PAYMENT_ADJUSTMENT change SUPPLIER_PAYMENT_ID PURCHASE_PAYMENT_ID integer not null;
alter table PURCHASE_PAYMENT_PAYMENT_ADJUSTMENT drop foreign key SUPP_PAYMENT_PAYMNT_ADJ$FK, add constraint PURCHASE_PAYMENT_PAYMENT_ADJUSTMENT$FK foreign key (PURCHASE_PAYMENT_ID) references PURCHASE_PAYMENT (ID);
alter table PURCHASE_PAYMENT_PAYMENT_ADJUSTMENT drop foreign key SUPP_PAYMENT_PAYMNT_ADJ$FK2, add constraint PURCHASE_PAYMENT_PAYMENT_ADJUSTMENT$FK2 foreign key (PURCHASE_PAYMENT_ADJ_TYPE_ID) references PURCHASE_PAYMENT_ADJ_TYPE (ID);
