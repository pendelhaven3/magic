alter table CUSTOMER change ADDRESS BUSINESS_ADDRESS varchar(100) not null;
alter table CUSTOMER add DELIVERY_ADDRESS varchar(100) null;
alter table CUSTOMER add TIN varchar(20) null;
alter table CUSTOMER add APPROVED_CREDIT_LINE numeric(10, 2) null;
alter table CUSTOMER add BUSINESS_TYPE varchar(15) null;
alter table CUSTOMER add OWNERS varchar(500) null;
alter table CUSTOMER add BANK_REFERENCES varchar(500) null;
alter table CUSTOMER add HOLD_IND char(1) default 'N' not null;
alter table CUSTOMER add REMARKS varchar(300) null;