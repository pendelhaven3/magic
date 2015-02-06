create table SUPP_PAYMENT_BANK_TRANSFER (
  ID integer auto_increment,
  SUPPLIER_PAYMENT_ID integer not null,
  BANK varchar(20) not null,
  REFERENCE_NO varchar(20) not null,
  AMOUNT numeric(10, 2) not null,
  TRANSFER_DT date not null,
  constraint SUPP_PAYMENT_BANK_TRANSFER$PK primary key (ID),
  constraint SUPP_PAYMENT_BANK_TRANSFER$FK foreign key (SUPPLIER_PAYMENT_ID) references SUPPLIER_PAYMENT (ID)
);

create table CREDIT_CARD (
  ID integer auto_increment,
  USER varchar(20) not null,
  BANK varchar(20) not null,
  CARD_NUMBER varchar(20) not null,
  constraint CREDIT_CARD$PK primary key (ID)
);

drop table SUPP_PAYMENT_CREDITCARD_PYMNT (

create table SUPP_PAYMENT_CREDITCARD_PYMNT (
  ID integer auto_increment,
  SUPPLIER_PAYMENT_ID integer not null,
  AMOUNT numeric(10, 2) not null,
  CREDIT_CARD_ID integer not null,
  TRANSACTION_DT date not null,
  APPROVAL_CODE varchar(20) not null,
  constraint SUPP_PAYMENT_CREDITCARD_PYMNT$PK primary key (ID),
  constraint SUPP_PAYMENT_CREDITCARD_PYMNT$FK foreign key (SUPPLIER_PAYMENT_ID) references SUPPLIER_PAYMENT (ID),
  constraint SUPP_PAYMENT_CREDITCARD_PYMNT$FK2 foreign key (CREDIT_CARD_ID) references CREDIT_CARD (ID)
);
