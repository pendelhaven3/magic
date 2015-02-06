alter table STOCK_QTY_CONVERSION change POST_DATE POST_DT date null;

insert into SEQUENCE (NAME) values ('SUPPLIER_PAYMENT_NO_SEQ');
insert into SEQUENCE (NAME) values ('SUPP_PAYMENT_ADJUSTMENT_NO_SEQ');

create table SUPPLIER_PAYMENT (
  ID integer auto_increment,
  SUPPLIER_PAYMENT_NO integer not null,
  SUPPLIER_ID integer not null,
  POST_IND char(1) default 'N' not null,
  POST_DT datetime null,
  POST_BY integer null,
  CREATE_DT date not null,
  ENCODER integer not null,
  CANCEL_IND char(1) default 'N' not null,
  CANCEL_DT date null,
  CANCEL_BY integer null,
  constraint SUPPLIER_PAYMENT$PK primary key (ID),
  constraint SUPPLIER_PAYMENT$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID),
  constraint SUPPLIER_PAYMENT$FK2 foreign key (POST_BY) references USER (ID),
  constraint SUPPLIER_PAYMENT$FK3 foreign key (ENCODER) references USER (ID),
  constraint SUPPLIER_PAYMENT$FK4 foreign key (CANCEL_BY) references USER (ID)
);

create table SUPP_PAYMENT_RECV_RCPT (
  ID integer auto_increment,
  SUPPLIER_PAYMENT_ID integer not null,
  RECEIVING_RECEIPT_ID integer not null,
  constraint SUPP_PAYMENT_RECV_RCPT$PK primary key (ID),
  constraint SUPP_PAYMENT_RECV_RCPT$FK foreign key (SUPPLIER_PAYMENT_ID) references SUPPLIER_PAYMENT (ID),
  constraint SUPP_PAYMENT_RECV_RCPT$FK2 foreign key (RECEIVING_RECEIPT_ID) references RECEIVING_RECEIPT (ID)
);

create table SUPP_PAYMENT_CASH_PYMNT (
  ID integer auto_increment,
  SUPPLIER_PAYMENT_ID integer not null,
  AMOUNT numeric(10, 2) not null,
  PAID_DT date not null,
  PAID_BY integer not null,
  constraint SUPP_PAYMENT_CASH_PYMNT$PK primary key (ID),
  constraint SUPP_PAYMENT_CASH_PYMNT$FK foreign key (SUPPLIER_PAYMENT_ID) references SUPPLIER_PAYMENT (ID),
  constraint SUPP_PAYMENT_CASH_PYMNT$FK2 foreign key (PAID_BY) references USER (ID)
);

create table SUPP_PAYMENT_CHECK_PYMNT (
  ID integer auto_increment,
  SUPPLIER_PAYMENT_ID integer not null,
  BANK varchar(30) not null,
  CHECK_DT date not null,
  CHECK_NO varchar(50) not null,
  AMOUNT numeric(10, 2) not null,
  constraint SUPP_PAYMENT_CHECK_PYMNT$PK primary key (ID),
  constraint SUPP_PAYMENT_CHECK_PYMNT$FK foreign key (SUPPLIER_PAYMENT_ID) references SUPPLIER_PAYMENT (ID)
);

create table SUPP_PAYMENT_CREDITCARD_PYMNT (
  ID integer auto_increment,
  SUPPLIER_PAYMENT_ID integer not null,
  BANK varchar(30) not null,
  AMOUNT numeric(10, 2) not null,
  PAID_DT date not null,
  PAID_BY integer not null,
  constraint SUPP_PAYMENT_CREDITCARD_PYMNT$PK primary key (ID),
  constraint SUPP_PAYMENT_CREDITCARD_PYMNT$FK foreign key (SUPPLIER_PAYMENT_ID) references SUPPLIER_PAYMENT (ID),
  constraint SUPP_PAYMENT_CREDITCARD_PYMNT$FK2 foreign key (PAID_BY) references USER (ID)
);

alter table PAYMENT_PAYMENT_ADJUSTMENT drop column ADJUSTMENT_TYPE;

create table SUPP_PAYMENT_ADJUSTMENT (
  ID integer auto_increment,
  SUPP_PAYMENT_ADJUSTMENT_NO integer not null,
  SUPPLIER_ID integer not null,
  ADJUSTMENT_TYPE_ID integer not null,
  AMOUNT numeric(8, 2) not null,
  POST_IND char(1) default 'N' not null,
  POST_DT date null,
  POST_BY integer null,
  REMARKS varchar(100) null,
  constraint SUPP_PAYMENT_ADJUSTMENT$PK primary key (ID),
  constraint SUPP_PAYMENT_ADJUSTMENT$UK unique (SUPP_PAYMENT_ADJUSTMENT_NO),
  constraint SUPP_PAYMENT_ADJUSTMENT$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID),
  constraint SUPP_PAYMENT_ADJUSTMENT$FK2 foreign key (ADJUSTMENT_TYPE_ID) references ADJUSTMENT_TYPE (ID),
  constraint SUPP_PAYMENT_ADJUSTMENT$FK3 foreign key (POST_BY) references USER (ID)
);

create table SUPP_PAYMENT_PAYMNT_ADJ (
  ID integer auto_increment,
  SUPPLIER_PAYMENT_ID integer not null,
  ADJUSTMENT_TYPE_ID integer not null,
  REFERENCE_NO varchar(30) null,
  AMOUNT numeric(10, 2) not null,
  constraint SUPP_PAYMENT_PAYMNT_ADJ$PK primary key (ID),
  constraint SUPP_PAYMENT_PAYMNT_ADJ$FK foreign key (SUPPLIER_PAYMENT_ID) references SUPPLIER_PAYMENT (ID),
  constraint SUPP_PAYMENT_PAYMNT_ADJ$FK2 foreign key (ADJUSTMENT_TYPE_ID) references ADJUSTMENT_TYPE (ID)
);
