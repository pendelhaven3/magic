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

create table PAYMENT_ADJUSTMENT (
  ID integer auto_increment,
  PAYMENT_ID integer not null,
  ADJUSTMENT_TYPE varchar(20) not null,
  REFERENCE_NO varchar(30) null,
  AMOUNT numeric(10, 2) not null,
  constraint PAYMENT_ADJUSTMENT$PK primary key (ID),
  constraint PAYMENT_ADJUSTMENT$FK foreign key (PAYMENT_ID) references PAYMENT (ID)
);

alter table PAYMENT add POST_DT date null;
alter table PAYMENT add POST_BY integer null;
alter table PAYMENT add constraint PAYMENT$FK2 foreign key (POST_BY) references USER (ID);
