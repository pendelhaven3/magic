alter table CREDIT_CARD add CUSTOMER_NUMBER varchar(30) null;

alter table CREDIT_CARD_STATEMENT drop foreign key CREDIT_CARD_STATEMENT$FK;
alter table CREDIT_CARD_STATEMENT drop index CREDIT_CARD_ID;
alter table CREDIT_CARD_STATEMENT modify column CREDIT_CARD_ID integer null;

alter table CREDIT_CARD_STATEMENT add CUSTOMER_NUMBER varchar(30) null;

alter table SALES_RETURN add PAYMENT_NO integer null;
alter table SALES_RETURN add constraint SALES_RETURN$FK6 foreign key (PAYMENT_NO) references PAYMENT (PAYMENT_NO);
