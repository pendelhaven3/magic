create table PAYMENT (
  ID integer auto_increment,
  CUSTOMER_ID integer not null,
  PAYMENT_DT date not null,
  AMOUNT_RECEIVED numeric(8, 2) not null,
  constraint PAYMENT$PK primary key (ID),
  constraint PAYMENT$FK foreign key (CUSTOMER_ID) references CUSTOMER (ID)
);

create table PAYMENT_ITEM (
  ID integer auto_increment,
  PAYMENT_ID integer not null,
  SALES_INVOICE_ID integer not null,
  constraint PAYMENT_ITEM$PK primary key (ID),
  constraint PAYMENT_ITEM$FK foreign key (PAYMENT_ID) references PAYMENT (ID),
  constraint PAYMENT_ITEM$FK2 foreign key (SALES_INVOICE_ID) references SALES_INVOICE (ID)
);