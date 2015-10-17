alter table CREDIT_CARD add CUSTOMER_NUMBER varchar(30) null;

alter table CREDIT_CARD_STATEMENT drop foreign key CREDIT_CARD_STATEMENT$FK;
alter table CREDIT_CARD_STATEMENT drop index CREDIT_CARD_ID;
alter table CREDIT_CARD_STATEMENT modify column CREDIT_CARD_ID integer null;

alter table CREDIT_CARD_STATEMENT add CUSTOMER_NUMBER varchar(30) null;
