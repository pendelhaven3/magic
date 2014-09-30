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
  UNIT_CONV_CSE integer(5) null,
  UNIT_CONV_TIE integer(5) null,
  UNIT_CONV_CTN integer(5) null,
  UNIT_CONV_DOZ integer(5) null,
  UNIT_CONV_PCS integer(5) null,
  GROSS_COST_CSE number(10, 2) default 0 not null,
  GROSS_COST_TIE number(10, 2) default 0 not null,
  GROSS_COST_CTN number(10, 2) default 0 not null,
  GROSS_COST_DOZ number(10, 2) default 0 not null,
  GROSS_COST_PCS number(10, 2) default 0 not null,
  FINAL_COST_CSE number(10, 2) default 0 not null,
  FINAL_COST_TIE number(10, 2) default 0 not null,
  FINAL_COST_CTN number(10, 2) default 0 not null,
  FINAL_COST_DOZ number(10, 2) default 0 not null,
  FINAL_COST_PCS number(10, 2) default 0 not null,
  constraint PRODUCT$PK primary key (ID),
  constraint PRODUCT$CODE$UK unique (CODE),
  constraint PRODUCT$FK foreign key (MANUFACTURER_ID) references MANUFACTURER (ID),
  constraint PRODUCT$FK2 foreign key (CATEGORY_ID) references PRODUCT_CATEGORY (ID)
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
  PRICING_SCHEME_ID integer not null,
  MODE varchar2(10) null,
  REMARKS varchar2(100) null,
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
  PASSWORD varchar2(100) not null,
  constraint USER$PK primary key (ID),
  constraint USER$UK unique (USERNAME)
);

create table SUPPLIER (
  ID integer auto_increment,
  CODE varchar2(15) not null,
  NAME varchar2(50) not null,
  ADDRESS varchar2(200) null,
  CONTACT_NUMBER varchar2(100) null,
  CONTACT_PERSON varchar2(100) null,
  FAX_NUMBER varchar2(100) null,
  EMAIL_ADDRESS varchar2(50) null,
  TIN varchar2(20) null,
  PAYMENT_TERM_ID integer null,
  REMARKS varchar2(200) null,
  DISCOUNT varchar2(30) null,
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

create table PRICING_SCHEME (
  ID integer auto_increment,
  NAME varchar2(30),
  constraint PRICING_SCHEME$PK primary key (ID),
  constraint PRICING_SCHEME$UK unique (NAME)
);

create table PRODUCT_PRICE (
  PRICING_SCHEME_ID integer default 1 not null,
  PRODUCT_ID integer not null,
  UNIT_PRICE_CSE number(10, 2) default 0 not null,
  UNIT_PRICE_TIE number(10, 2) default 0 not null,
  UNIT_PRICE_CTN number(10, 2) default 0 not null,
  UNIT_PRICE_DOZ number(10, 2) default 0 not null,
  UNIT_PRICE_PCS number(10, 2) default 0 not null,
  constraint PRODUCT_PRICE$PK primary key (PRICING_SCHEME_ID, PRODUCT_ID),
  constraint PRODUCT_PRICE$FK foreign key (PRICING_SCHEME_ID) references PRICING_SCHEME (ID),
  constraint PRODUCT_PRICE$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table STOCK_QTY_CONVERSION (
  ID integer auto_increment,
  STOCK_QTY_CONV_NO integer auto_increment,
  REMARKS varchar2(100) null,
  POST_IND varchar2(1) default 'N' not null,
  POST_DATE date null,
  constraint STOCK_QTY_CONVERSION$PK primary key (ID),
  constraint STOCK_QTY_CONVERSION$UK unique (STOCK_QTY_CONV_NO)
);

create table STOCK_QTY_CONVERSION_ITEM (
  ID integer auto_increment,
  STOCK_QTY_CONVERSION_ID integer not null,
  PRODUCT_ID integer not null,
  FROM_UNIT varchar2(3) not null,
  TO_UNIT varchar2(3) not null,
  QUANTITY integer(3) not null,
  constraint STOCK_QTY_CONVERSION_ITEM$PK primary key (ID),
  constraint STOCK_QTY_CONVERSION_ITEM$FK foreign key (STOCK_QTY_CONVERSION_ID) references STOCK_QTY_CONVERSION (ID),
  constraint STOCK_QTY_CONVERSION_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table PURCHASE_ORDER (
  ID integer auto_increment,
  PURCHASE_ORDER_NO integer auto_increment,
  SUPPLIER_ID integer not null,
  POST_IND varchar2(1) default 'N' not null,
  ORDER_IND varchar2(1) default 'N' not null,
  PAYMENT_TERM_ID integer null,
  REMARKS varchar2(100) null,
  REFERENCE_NO varchar2(30) null,
  ORDER_DT date null,
  POST_DT date null,
  CREATED_BY integer not null,
  constraint PURCHASE_ORDER$PK primary key (ID),
  constraint PURCHASE_ORDER$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID)
);

create table PURCHASE_ORDER_ITEM (
  ID integer auto_increment,
  PURCHASE_ORDER_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT varchar2(3) not null,
  QUANTITY integer(4) not null,
  COST number(10, 2) not null,
  ACTUAL_QUANTITY integer(4) null,
  ORDER_IND varchar2(1) default 'N' not null,
  constraint PURCHASE_ORDER_ITEM$PK primary key (ID),
  constraint PURCHASE_ORDER_ITEM$FK foreign key (PURCHASE_ORDER_ID) references PURCHASE_ORDER (ID),
  constraint PURCHASE_ORDER_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table RECEIVING_RECEIPT (
  ID integer auto_increment,
  RECEIVING_RECEIPT_NO integer auto_increment,
  SUPPLIER_ID integer not null,
  POST_IND varchar2(1) default 'N' not null,
  PAYMENT_TERM_ID integer not null,
  REMARKS varchar2(100) null,
  REFERENCE_NO varchar2(30) null,
  RECEIVED_DT date not null,
  RECEIVED_BY integer not null,
  ORDER_DT date not null,
  RELATED_PURCHASE_ORDER_NO integer not null,
  constraint RECEIVING_RECEIPT$PK primary key (ID),
  constraint RECEIVING_RECEIPT$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID)
);

create table RECEIVING_RECEIPT_ITEM (
  ID integer auto_increment,
  RECEIVING_RECEIPT_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT varchar2(3) not null,
  QUANTITY integer(4) not null,
  COST number(10, 2) not null,
  DISCOUNT_1 number(4, 2) default 0 not null,
  DISCOUNT_2 number(4, 2) default 0 not null,
  DISCOUNT_3 number(4, 2) default 0 not null,
  FLAT_RATE_DISCOUNT number(8, 2) default 0 not null,
  constraint RECEIVING_RECEIPT_ITEM$PK primary key (ID),
  constraint RECEIVING_RECEIPT_ITEM$FK foreign key (RECEIVING_RECEIPT_ID) references RECEIVING_RECEIPT (ID),
  constraint RECEIVING_RECEIPT_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table ADJUSTMENT_OUT (
  ID integer auto_increment,
  ADJUSTMENT_OUT_NO integer auto_increment,
  POST_IND varchar2(1) default 'N' not null,
  REMARKS varchar2(100) null,
  POST_DT date null,
  POSTED_BY integer null,
  constraint ADJUSTMENT_OUT$PK primary key (ID),
  constraint ADJUSTMENT_OUT$UK unique (ADJUSTMENT_OUT_NO)
);

create table ADJUSTMENT_OUT_ITEM (
  ID integer auto_increment,
  ADJUSTMENT_OUT_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT varchar2(3) not null,
  QUANTITY integer not null,
  constraint ADJUSTMENT_OUT_ITEM$PK primary key (ID),
  constraint ADJUSTMENT_OUT_ITEM$FK foreign key (ADJUSTMENT_OUT_ID) references ADJUSTMENT_OUT (ID),
  constraint ADJUSTMENT_OUT_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table ADJUSTMENT_IN (
  ID integer auto_increment,
  ADJUSTMENT_IN_NO integer auto_increment,
  POST_IND varchar2(1) default 'N' not null,
  REMARKS varchar2(100) null,
  POST_DT date null,
  POSTED_BY integer null,
  constraint ADJUSTMENT_IN$PK primary key (ID),
  constraint ADJUSTMENT_IN$UK unique (ADJUSTMENT_IN_NO)
);

create table ADJUSTMENT_IN_ITEM (
  ID integer auto_increment,
  ADJUSTMENT_IN_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT varchar2(3) not null,
  QUANTITY integer not null,
  constraint ADJUSTMENT_IN_ITEM$PK primary key (ID),
  constraint ADJUSTMENT_IN_ITEM$FK foreign key (ADJUSTMENT_IN_ID) references ADJUSTMENT_IN (ID),
  constraint ADJUSTMENT_IN_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);