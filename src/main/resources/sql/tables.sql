create table PRODUCT (
  ID integer auto_increment,
  CODE varchar2(12) not null,
  DESCRIPTION varchar2(50) not null,
  UNIT_IND_CSE varchar2(1) null,
  UNIT_IND_CTN varchar2(1) null,
  UNIT_IND_DOZ varchar2(1) null,
  UNIT_IND_PCS varchar2(1) null,
  AVAIL_QTY_CSE integer(4) default 0 not null,
  AVAIL_QTY_CTN integer(4) default 0 not null,
  AVAIL_QTY_DOZ integer(4) default 0 not null,
  AVAIL_QTY_PCS integer(4) default 0 not null,
  constraint PRODUCT$PK primary key (ID),
  constraint PRODUCT$CODE$UK unique (CODE)
);

create table PRODUCT_PRICE (
  ID integer auto_increment,
  PRODUCT_ID integer not null,
  UNIT_PRICE_CSE number(10, 2) null,
  UNIT_PRICE_CTN number(10, 2) null,
  UNIT_PRICE_DOZ number(10, 2) null,
  UNIT_PRICE_PCS number(10, 2) null,
  constraint PRODUCT_PRICE$PK primary key (ID),
  constraint PRODUCT_PRICE$FK foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table SALES_REQUISITION (
  ID integer auto_increment,
  SALES_REQUISITION_NO integer auto_increment,
  CUSTOMER_NAME varchar2(30),
  CREATE_DT date not null,
  ENCODER varchar2(30) not null,
  constraint SALES_REQUISITION$PK primary key (ID),
  constraint SALES_REQUISITION$UK unique (SALES_REQUISITION_NO)
);

create table SALES_REQUISITION_ITEM (
  ID integer auto_increment,
  SALES_REQUISITION_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT varchar2(3) not null,
  QUANTITY integer not null,
  constraint SALES_REQUISITION_ITEM$PK primary key (ID),
  constraint SALES_REQUISITION_ITEM$FK foreign key (SALES_REQUISITION_ID) references SALES_REQUISITION (ID),
  constraint SALES_REQUISITION_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

