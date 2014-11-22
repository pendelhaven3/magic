drop table PAYMENT_ITEM;
drop table PAYMENT;

create table PAYMENT (
  ID integer auto_increment,
  PAYMENT_NO integer not null,
  CUSTOMER_ID integer not null,
  POST_IND char(1) default 'N' not null,
  constraint PAYMENT$PK primary key (ID),
  constraint PAYMENT$FK foreign key (CUSTOMER_ID) references CUSTOMER (ID)
);

create table PAYMENT_SALES_INVOICE (
  ID integer auto_increment,
  PAYMENT_ID integer not null,
  SALES_INVOICE_ID integer not null,
  constraint PAYMENT_SALES_INVOICE$PK primary key (ID),
  constraint PAYMENT_SALES_INVOICE$UK unique (SALES_INVOICE_ID),
  constraint PAYMENT_SALES_INVOICE$FK foreign key (PAYMENT_ID) references PAYMENT (ID),
  constraint PAYMENT_SALES_INVOICE$FK2 foreign key (SALES_INVOICE_ID) references SALES_INVOICE (ID)
);

insert into SEQUENCE (NAME) values ('PAYMENT_NO_SEQ');