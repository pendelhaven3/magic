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

create table CUSTOMER (
  ID integer auto_increment,
  CODE varchar2(12) not null,
  NAME varchar2(30) not null,
  ADDRESS varchar2 (100) not null,
  constraint CUSTOMER$PK primary key (ID),
  constraint CUSTOMER$UK unique (CODE)
);

create table SALES_REQUISITION (
  ID integer auto_increment,
  SALES_REQUISITION_NO integer auto_increment,
  CUSTOMER_ID integer null,
  CREATE_DT date not null,
  ENCODER_ID integer not null,
  POST_IND varchar2(1) default 'N' not null,
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

create table SALES_INVOICE (
  ID integer auto_increment,
  SALES_INVOICE_NO integer auto_increment,
  CUSTOMER_ID integer not null,
  POST_DT date not null,
  POSTED_BY varchar2(30),
  SALES_INVOICE_ID integer not null,
  constraint SALES_INVOICE$PK primary key (ID)
);

create table SALES_INVOICE_ITEM (
  ID integer auto_increment,
  SALES_INVOICE_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT varchar2(3) not null,
  QUANTITY integer not null,
  UNIT_PRICE number(10, 2) not null,
  constraint SALES_INVOICE_ITEM$PK primary key (ID),
  constraint SALES_INVOICE_ITEM$FK foreign key (SALES_INVOICE_ID) references SALES_INVOICE (ID),
  constraint SALES_INVOICE_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table USER (
  ID integer auto_increment,
  USERNAME varchar2(15) not null,
  constraint USER$PK primary key (ID),
  constraint USER$UK unique (USERNAME)
);

insert into USER (ID, USERNAME) values (1, 'PJ');  