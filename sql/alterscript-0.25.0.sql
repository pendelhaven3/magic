alter table PAYMENT_SALES_INVOICE add ADJUSTMENT_AMOUNT numeric(10, 2) null;
alter table PAYMENT_CHECK_PAYMENT add CHECK_DT date not null;

create table PAYMENT_CASH_PAYMENT (
  ID integer auto_increment,
  PAYMENT_ID integer not null,
  AMOUNT numeric(10, 2) not null,
  RECEIVED_DT date not null,
  RECEIVED_BY integer not null,
  constraint PAYMENT_CASH_PAYMENT$PK primary key (ID),
  constraint PAYMENT_CASH_PAYMENT$FK foreign key (PAYMENT_ID) references PAYMENT (ID),
  constraint PAYMENT_CASH_PAYMENT$FK2 foreign key (RECEIVED_BY) references USER (ID)
);
