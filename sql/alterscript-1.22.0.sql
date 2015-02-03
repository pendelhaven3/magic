insert into SEQUENCE (NAME) values ('PURCHASE_RETURN_NO_SEQ');

create table PURCHASE_RETURN (
  ID integer auto_increment,
  PURCHASE_RETURN_NO integer not null,
  RECEIVING_RECEIPT_ID integer not null,
  POST_IND char(1) default 'N' not null,
  POST_DT date null,
  POST_BY integer null,
  REMARKS varchar(100) null,
  constraint PURCHASE_RETURN$PK primary key (ID),
  constraint PURCHASE_RETURN$UK unique (PURCHASE_RETURN_NO),
  constraint PURCHASE_RETURN$FK foreign key (RECEIVING_RECEIPT_ID) references RECEIVING_RECEIPT (ID),
  constraint PURCHASE_RETURN$FK2 foreign key (POST_BY) references USER (ID)
);

create table PURCHASE_RETURN_ITEM (
  ID integer auto_increment,
  PURCHASE_RETURN_ID integer not null,
  RECEIVING_RECEIPT_ITEM_ID integer not null,
  QUANTITY integer not null,
  constraint PURCHASE_RETURN_ITEM$PK primary key (ID),
  constraint PURCHASE_RETURN_ITEM$FK foreign key (PURCHASE_RETURN_ID) references PURCHASE_RETURN (ID),
  constraint PURCHASE_RETURN_ITEM$FK2 foreign key (RECEIVING_RECEIPT_ITEM_ID) references RECEIVING_RECEIPT_ITEM (ID)
);

create table PURCHASE_PAYMENT_ADJ_TYPE (
  ID integer auto_increment,
  CODE varchar(12) not null,
  DESCRIPTION varchar(100) not null,
  constraint PURCHASE_PAYMENT_ADJ_TYPE$PK primary key (ID),
  constraint PURCHASE_PAYMENT_ADJ_TYPE$UK unique (CODE)
);
