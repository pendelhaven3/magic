create table MANUFACTURER (
  ID integer auto_increment,
  NAME varchar2(50) not null,
  constraint MANUFACTURER$PK primary key (ID),
  constraint MANUFACTURER$UK unique (NAME)
);

create table PRODUCT_CATEGORY (
  ID integer auto_increment,
  NAME varchar2(50) not null,
  constraint PRODUCT_CATEGORY$PK primary key (ID),
  constraint PRODUCT_CATEGORY$UK unique (NAME)
);

create table PRODUCT (
  ID integer auto_increment,
  CODE varchar2(12) not null,
  DESCRIPTION varchar2(50) not null,
  MAX_STOCK_LEVEL integer(4) default 0,
  MIN_STOCK_LEVEL integer(4) default 0,
  ACTIVE_IND varchar2(1) default 'Y',
  UNIT_IND_CSE varchar2(1) null,
  UNIT_IND_TIE varchar2(1) null,
  UNIT_IND_CTN varchar2(1) null,
  UNIT_IND_DOZ varchar2(1) null,
  UNIT_IND_PCS varchar2(1) null,
  AVAIL_QTY_CSE integer(4) default 0 not null,
  AVAIL_QTY_TIE integer(4) default 0 not null,
  AVAIL_QTY_CTN integer(4) default 0 not null,
  AVAIL_QTY_DOZ integer(4) default 0 not null,
  AVAIL_QTY_PCS integer(4) default 0 not null,
  MANUFACTURER_ID integer null,
  CATEGORY_ID integer null,
  constraint PRODUCT$PK primary key (ID),
  constraint PRODUCT$CODE$UK unique (CODE),
  constraint PRODUCT$FK foreign key (MANUFACTURER_ID) references MANUFACTURER (ID),
  constraint PRODUCT$FK2 foreign key (CATEGORY_ID) references PRODUCT_CATEGORY (ID)
);

create table PRODUCT_PRICE (
  ID integer auto_increment,
  PRODUCT_ID integer not null,
  UNIT_PRICE_CSE number(10, 2) default 0 not null,
  UNIT_PRICE_TIE number(10, 2) default 0 not null,
  UNIT_PRICE_CTN number(10, 2) default 0 not null,
  UNIT_PRICE_DOZ number(10, 2) default 0 not null,
  UNIT_PRICE_PCS number(10, 2) default 0 not null,
  constraint PRODUCT_PRICE$PK primary key (ID),
  constraint PRODUCT_PRICE$FK foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table CUSTOMER (
  ID integer auto_increment,
  CODE varchar2(12) not null,
  NAME varchar2(50) not null,
  ADDRESS varchar2(100) null,
  CONTACT_PERSON varchar2(100) null,
  CONTACT_NUMBER varchar2(100) null,
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

create table SUPPLIER (
  ID integer auto_increment,
  NAME varchar2(50) not null,
  ADDRESS varchar2(200) null,
  CONTACT_NUMBER varchar2(100) null,
  CONTACT_PERSON varchar2(100) null,
  FAX_NUMBER varchar2(100) null,
  EMAIL_ADDRESS varchar2(50) null,
  TIN varchar2(20) null,
  PAYMENT_TERM_ID integer null,
  constraint SUPPLIER$PK primary key (ID),
  constraint SUPPLIER$UK unique (NAME)
);

create table SUPPLIER_PRODUCT (
  SUPPLIER_ID integer not null,
  PRODUCT_ID integer not null,
  constraint SUPPLIER_PRODUCT$PK primary key (SUPPLIER_ID, PRODUCT_ID),
  constraint SUPPLIER_PRODUCT$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID),
  constraint SUPPLIER_PRODUCT$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);
 
create table PAYMENT_TERM (
  ID integer auto_increment,
  NAME varchar2(50),
  NUMBER_OF_DAYS integer(3),
  constraint PAYMENT_TERM$PK primary key (ID),
  constraint PAYMENT_tERM$UK unique (NAME)
);