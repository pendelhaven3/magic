create table SEQUENCE (
  NAME varchar(50) not null,
  VALUE integer default 0 not null,
  constraint SEQUENCE$PK unique (NAME)
);

create table SYSTEM_PARAMETER (
  NAME varchar(50) not null,
  VALUE varchar(100) not null,
  constraint SYSTEM_PARAMETER$PK primary key (NAME)
);

create table MANUFACTURER (
  ID integer auto_increment,
  NAME varchar(50) not null,
  constraint MANUFACTURER$PK primary key (ID),
  constraint MANUFACTURER$UK unique (NAME)
);

create table PRODUCT_CATEGORY (
  ID integer auto_increment,
  NAME varchar(60) not null,
  constraint PRODUCT_CATEGORY$PK primary key (ID),
  constraint PRODUCT_CATEGORY$UK unique (NAME)
);

create table PRODUCT_SUBCATEGORY (
  ID integer auto_increment,
  PRODUCT_CATEGORY_ID integer not null,
  NAME varchar(60) not null,
  constraint PRODUCT_SUBCATEGORY$PK primary key (ID),
  constraint PRODUCT_SUBCATEGORY$UK unique (NAME),
  constraint PRODUCT_SUBCATEGORY$FK foreign key (PRODUCT_CATEGORY_ID) references PRODUCT_CATEGORY (ID)
);

create table PRODUCT (
  ID integer auto_increment,
  CODE varchar(12) not null,
  DESCRIPTION varchar(50) not null,
  MAX_STOCK_LEVEL integer(4) default 0,
  MIN_STOCK_LEVEL integer(4) default 0,
  ACTIVE_IND char(1) default 'Y',
  UNIT_IND_CSE char(1) null,
  UNIT_IND_TIE char(1) null,
  UNIT_IND_CTN char(1) null,
  UNIT_IND_DOZ char(1) null,
  UNIT_IND_PCS char(1) null,
  AVAIL_QTY_CSE integer(4) default 0 not null,
  AVAIL_QTY_TIE integer(4) default 0 not null,
  AVAIL_QTY_CTN integer(4) default 0 not null,
  AVAIL_QTY_DOZ integer(4) default 0 not null,
  AVAIL_QTY_PCS integer(4) default 0 not null,
  MANUFACTURER_ID integer null,
  CATEGORY_ID integer null,
  SUBCATEGORY_ID integer null,
  UNIT_CONV_CSE integer(5) null,
  UNIT_CONV_TIE integer(5) null,
  UNIT_CONV_CTN integer(5) null,
  UNIT_CONV_DOZ integer(5) null,
  UNIT_CONV_PCS integer(5) null,
  GROSS_COST_CSE numeric(10, 2) default 0 not null,
  GROSS_COST_TIE numeric(10, 2) default 0 not null,
  GROSS_COST_CTN numeric(10, 2) default 0 not null,
  GROSS_COST_DOZ numeric(10, 2) default 0 not null,
  GROSS_COST_PCS numeric(10, 2) default 0 not null,
  FINAL_COST_CSE numeric(10, 2) default 0 not null,
  FINAL_COST_TIE numeric(10, 2) default 0 not null,
  FINAL_COST_CTN numeric(10, 2) default 0 not null,
  FINAL_COST_DOZ numeric(10, 2) default 0 not null,
  FINAL_COST_PCS numeric(10, 2) default 0 not null,
  COMPANY_LIST_PRICE numeric(10, 2) default 0 not null,
  constraint PRODUCT$PK primary key (ID),
  constraint PRODUCT$CODE$UK unique (CODE),
  constraint PRODUCT$FK foreign key (MANUFACTURER_ID) references MANUFACTURER (ID),
  constraint PRODUCT$FK2 foreign key (CATEGORY_ID) references PRODUCT_CATEGORY (ID),
  constraint PRODUCT$FK3 foreign key (SUBCATEGORY_ID) references PRODUCT_SUBCATEGORY (ID)
);

create table PAYMENT_TERM (
  ID integer auto_increment,
  NAME varchar(50),
  NUMBER_OF_DAYS integer(3),
  constraint PAYMENT_TERM$PK primary key (ID),
  constraint PAYMENT_tERM$UK unique (NAME)
);

create table CUSTOMER (
  ID integer auto_increment,
  CODE varchar(12) not null,
  NAME varchar(50) not null,
  BUSINESS_ADDRESS varchar(100) null,
  CONTACT_PERSON varchar(100) null,
  CONTACT_NUMBER varchar(100) null,
  PAYMENT_TERM_ID integer null,
  constraint CUSTOMER$PK primary key (ID),
  constraint CUSTOMER$UK unique (CODE),
  constraint CUSTOMER$FK foreign key (PAYMENT_TERM_ID) references PAYMENT_TERM (ID)
);

create table PRICING_SCHEME (
  ID integer auto_increment,
  NAME varchar(30),
  constraint PRICING_SCHEME$PK primary key (ID),
  constraint PRICING_SCHEME$UK unique (NAME)
);

create table USER (
  ID integer auto_increment,
  USERNAME varchar(15) not null,
  PASSWORD varchar(100) not null,
  SUPERVISOR_IND char(1) default 'N' not null,
  constraint USER$PK primary key (ID),
  constraint USER$UK unique (USERNAME)
);

create table SALES_REQUISITION (
  ID integer auto_increment,
  SALES_REQUISITION_NO integer not null,
  CUSTOMER_ID integer null,
  CREATE_DT date not null,
  TRANSACTION_DT date not null,
  ENCODER integer not null,
  POST_IND char(1) default 'N' not null,
  PRICING_SCHEME_ID integer not null,
  MODE varchar(10) null,
  REMARKS varchar(100) null,
  PAYMENT_TERM_ID integer not null,
  constraint SALES_REQUISITION$PK primary key (ID),
  constraint SALES_REQUISITION$UK unique (SALES_REQUISITION_NO),
  constraint SALES_REQUISITION$FK1 foreign key (PRICING_SCHEME_ID) references PRICING_SCHEME (ID),
  constraint SALES_REQUISITION$FK2 foreign key (CUSTOMER_ID) references CUSTOMER (ID),
  constraint SALES_REQUISITION$FK3 foreign key (ENCODER) references USER (ID),
  constraint SALES_REQUISITION$FK4 foreign key (PAYMENT_TERM_ID) references PAYMENT_TERM (ID)
);

create table SALES_REQUISITION_ITEM (
  ID integer auto_increment,
  SALES_REQUISITION_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  QUANTITY integer not null,
  constraint SALES_REQUISITION_ITEM$PK primary key (ID),
  constraint SALES_REQUISITION_ITEM$FK foreign key (SALES_REQUISITION_ID) references SALES_REQUISITION (ID),
  constraint SALES_REQUISITION_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table SALES_INVOICE (
  ID integer auto_increment,
  SALES_INVOICE_NO integer not null,
  CUSTOMER_ID integer not null,
  CREATE_DT date not null,
  ENCODER integer not null,
  TRANSACTION_DT date not null,
  RELATED_SALES_REQUISITION_NO integer not null,
  PRICING_SCHEME_ID integer not null,
  MODE varchar(10) not null,
  REMARKS varchar(100) null,
  PAYMENT_TERM_ID integer not null,
  POST_DT date null,
  POST_BY integer not null,
  MARK_IND char(1) default 'N' not null,
  MARK_DT date null,
  MARK_BY integer null,
  CANCEL_IND char(1) default 'N' not null,
  CANCEL_DT date null,
  CANCEL_BY integer null,
  VAT_AMOUNT numeric(8, 2) not null,
  constraint SALES_INVOICE$PK primary key (ID),
  constraint SALES_INVOICE$UK unique (SALES_INVOICE_NO),
  constraint SALES_INVOICE$FK foreign key (CUSTOMER_ID) references CUSTOMER (ID),
  constraint SALES_INVOICE$FK2 foreign key (ENCODER) references USER (ID),
  constraint SALES_INVOICE$FK3 foreign key (RELATED_SALES_REQUISITION_NO) references SALES_REQUISITION (SALES_REQUISITION_NO),
  constraint SALES_INVOICE$FK4 foreign key (PAYMENT_TERM_ID) references PAYMENT_TERM (ID),
  constraint SALES_INVOICE$FK5 foreign key (PRICING_SCHEME_ID) references PRICING_SCHEME (ID),
  constraint SALES_INVOICE$FK6 foreign key (POST_BY) references USER (ID),
  constraint SALES_INVOICE$FK7 foreign key (MARK_BY) references USER (ID),
  constraint SALES_INVOICE$FK8 foreign key (CANCEL_BY) references USER (ID)
);

create table SALES_INVOICE_ITEM (
  ID integer auto_increment,
  SALES_INVOICE_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  QUANTITY integer not null,
  UNIT_PRICE numeric(10, 2) not null,
  DISCOUNT_1 numeric(4, 2) default 0 not null,
  DISCOUNT_2 numeric(4, 2) default 0 not null,
  DISCOUNT_3 numeric(4, 2) default 0 not null,
  FLAT_RATE_DISCOUNT numeric(8, 2) default 0 not null,
  constraint SALES_INVOICE_ITEM$PK primary key (ID),
  constraint SALES_INVOICE_ITEM$FK foreign key (SALES_INVOICE_ID) references SALES_INVOICE (ID),
  constraint SALES_INVOICE_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table SUPPLIER (
  ID integer auto_increment,
  CODE varchar(15) not null,
  NAME varchar(50) not null,
  ADDRESS varchar(200) null,
  CONTACT_NUMBER varchar(100) null,
  CONTACT_PERSON varchar(100) null,
  FAX_NUMBER varchar(100) null,
  EMAIL_ADDRESS varchar(50) null,
  TIN varchar(20) null,
  PAYMENT_TERM_ID integer null,
  REMARKS varchar(200) null,
  DISCOUNT varchar(30) null,
  constraint SUPPLIER$PK primary key (ID),
  constraint SUPPLIER$UK unique (NAME),
  constraint SUPPLIER$FK foreign key (PAYMENT_TERM_ID) references PAYMENT_TERM (ID)
);

create table SUPPLIER_PRODUCT (
  SUPPLIER_ID integer not null,
  PRODUCT_ID integer not null,
  constraint SUPPLIER_PRODUCT$PK primary key (SUPPLIER_ID, PRODUCT_ID),
  constraint SUPPLIER_PRODUCT$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID),
  constraint SUPPLIER_PRODUCT$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);
 
create table PRODUCT_PRICE (
  PRICING_SCHEME_ID integer default 1 not null,
  PRODUCT_ID integer not null,
  UNIT_PRICE_CSE numeric(10, 2) default 0 not null,
  UNIT_PRICE_TIE numeric(10, 2) default 0 not null,
  UNIT_PRICE_CTN numeric(10, 2) default 0 not null,
  UNIT_PRICE_DOZ numeric(10, 2) default 0 not null,
  UNIT_PRICE_PCS numeric(10, 2) default 0 not null,
  constraint PRODUCT_PRICE$PK primary key (PRICING_SCHEME_ID, PRODUCT_ID),
  constraint PRODUCT_PRICE$FK foreign key (PRICING_SCHEME_ID) references PRICING_SCHEME (ID),
  constraint PRODUCT_PRICE$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table STOCK_QTY_CONVERSION (
  ID integer auto_increment,
  STOCK_QTY_CONV_NO integer not null,
  REMARKS varchar(100) null,
  POST_IND char(1) default 'N' not null,
  POST_DATE date null,
  constraint STOCK_QTY_CONVERSION$PK primary key (ID),
  constraint STOCK_QTY_CONVERSION$UK unique (STOCK_QTY_CONV_NO)
);

create table STOCK_QTY_CONVERSION_ITEM (
  ID integer auto_increment,
  STOCK_QTY_CONVERSION_ID integer not null,
  PRODUCT_ID integer not null,
  FROM_UNIT char(3) not null,
  TO_UNIT char(3) not null,
  QUANTITY integer(3) not null,
  constraint STOCK_QTY_CONVERSION_ITEM$PK primary key (ID),
  constraint STOCK_QTY_CONVERSION_ITEM$FK foreign key (STOCK_QTY_CONVERSION_ID) references STOCK_QTY_CONVERSION (ID),
  constraint STOCK_QTY_CONVERSION_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table PURCHASE_ORDER (
  ID integer auto_increment,
  PURCHASE_ORDER_NO integer not null,
  SUPPLIER_ID integer not null,
  POST_IND char(1) default 'N' not null,
  DELIVERY_IND char(1) default 'N' not null,
  PAYMENT_TERM_ID integer null,
  REMARKS varchar(100) null,
  REFERENCE_NO varchar(30) null,
  POST_DT date null,
  CREATED_BY integer not null,
  constraint PURCHASE_ORDER$PK primary key (ID),
  constraint PURCHASE_ORDER$UK unique (PURCHASE_ORDER_NO),
  constraint PURCHASE_ORDER$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID),
  constraint PURCHASE_ORDER$FK2 foreign key (PAYMENT_TERM_ID) references PAYMENT_TERM (ID),
  constraint PURCHASE_ORDER$FK3 foreign key (CREATED_BY) references USER (ID)
);

create table PURCHASE_ORDER_ITEM (
  ID integer auto_increment,
  PURCHASE_ORDER_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  QUANTITY integer(4) not null,
  COST numeric(10, 2) not null,
  ACTUAL_QUANTITY integer(4) null,
  ORDER_IND char(1) default 'N' not null,
  constraint PURCHASE_ORDER_ITEM$PK primary key (ID),
  constraint PURCHASE_ORDER_ITEM$FK foreign key (PURCHASE_ORDER_ID) references PURCHASE_ORDER (ID),
  constraint PURCHASE_ORDER_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table RECEIVING_RECEIPT (
  ID integer auto_increment,
  RECEIVING_RECEIPT_NO integer not null,
  SUPPLIER_ID integer not null,
  POST_IND char(1) default 'N' not null,
  PAYMENT_TERM_ID integer not null,
  REMARKS varchar(100) null,
  REFERENCE_NO varchar(30) null,
  RECEIVED_DT datetime not null,
  RECEIVED_BY integer not null,
  RELATED_PURCHASE_ORDER_NO integer not null,
  constraint RECEIVING_RECEIPT$PK primary key (ID),
  constraint RECEIVING_RECEIPT$UK unique (RECEIVING_RECEIPT_NO),
  constraint RECEIVING_RECEIPT$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID),
  constraint RECEIVING_RECEIPT$FK2 foreign key (PAYMENT_TERM_ID) references PAYMENT_TERM (ID),
  constraint RECEIVING_RECEIPT$FK3 foreign key (RECEIVED_BY) references USER (ID),
  constraint RECEIVING_RECEIPT$FK4 foreign key (RELATED_PURCHASE_ORDER_NO) references PURCHASE_ORDER (PURCHASE_ORDER_NO)
);

create table RECEIVING_RECEIPT_ITEM (
  ID integer auto_increment,
  RECEIVING_RECEIPT_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  QUANTITY integer(4) not null,
  COST numeric(10, 2) not null,
  DISCOUNT_1 numeric(4, 2) default 0 not null,
  DISCOUNT_2 numeric(4, 2) default 0 not null,
  DISCOUNT_3 numeric(4, 2) default 0 not null,
  FLAT_RATE_DISCOUNT numeric(8, 2) default 0 not null,
  CURRENT_COST numeric(10, 2) null,
  constraint RECEIVING_RECEIPT_ITEM$PK primary key (ID),
  constraint RECEIVING_RECEIPT_ITEM$FK foreign key (RECEIVING_RECEIPT_ID) references RECEIVING_RECEIPT (ID),
  constraint RECEIVING_RECEIPT_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table ADJUSTMENT_OUT (
  ID integer auto_increment,
  ADJUSTMENT_OUT_NO integer not null,
  POST_IND char(1) default 'N' not null,
  REMARKS varchar(100) null,
  POST_DT date null,
  POSTED_BY integer null,
  constraint ADJUSTMENT_OUT$PK primary key (ID),
  constraint ADJUSTMENT_OUT$UK unique (ADJUSTMENT_OUT_NO),
  constraint ADJUSTMENT_OUT$FK foreign key (POSTED_BY) references USER (ID)
);

create table ADJUSTMENT_OUT_ITEM (
  ID integer auto_increment,
  ADJUSTMENT_OUT_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  QUANTITY integer not null,
  constraint ADJUSTMENT_OUT_ITEM$PK primary key (ID),
  constraint ADJUSTMENT_OUT_ITEM$FK foreign key (ADJUSTMENT_OUT_ID) references ADJUSTMENT_OUT (ID),
  constraint ADJUSTMENT_OUT_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table ADJUSTMENT_IN (
  ID integer auto_increment,
  ADJUSTMENT_IN_NO integer not null,
  POST_IND char(1) default 'N' not null,
  REMARKS varchar(100) null,
  POST_DT date null,
  POSTED_BY integer null,
  constraint ADJUSTMENT_IN$PK primary key (ID),
  constraint ADJUSTMENT_IN$UK unique (ADJUSTMENT_IN_NO),
  constraint ADJUSTMENT_IN$FK foreign key (POSTED_BY) references USER (ID)
);

create table ADJUSTMENT_IN_ITEM (
  ID integer auto_increment,
  ADJUSTMENT_IN_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  QUANTITY integer not null,
  constraint ADJUSTMENT_IN_ITEM$PK primary key (ID),
  constraint ADJUSTMENT_IN_ITEM$FK foreign key (ADJUSTMENT_IN_ID) references ADJUSTMENT_IN (ID),
  constraint ADJUSTMENT_IN_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table INVENTORY_CHECK (
  ID integer auto_increment,
  INVENTORY_DT date not null,
  POST_IND char(1) default 'N' not null,
  constraint INVENTORY_CHECK$PK primary key (ID),
  constraint INVENTORY_CHECK$UK unique (INVENTORY_DT)
);

create table AREA (
  ID integer auto_increment,
  NAME varchar(50) not null,
  constraint AREA$PK primary key (ID),
  constraint AREA$UK unique (NAME)
);

create table AREA_INV_REPORT (
  ID integer auto_increment,
  INVENTORY_CHECK_ID integer not null,
  REPORT_NO integer not null,
  AREA_ID integer null,
  CHECKER varchar(50) null,
  DOUBLE_CHECKER varchar(50) null,
  constraint AREA_INV_REPORT$PK primary key (ID),
  constraint AREA_INV_REPORT$UK unique (INVENTORY_CHECK_ID, REPORT_NO),
  constraint AREA_INV_REPORT$FK foreign key (INVENTORY_CHECK_ID) references AREA_INV_REPORT (ID),
  constraint AREA_INV_REPORT$FK2 foreign key (AREA_ID) references AREA (ID)
);

create table AREA_INV_REPORT_ITEM (
  ID integer auto_increment,
  AREA_INV_REPORT_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  QUANTITY integer not null,
  constraint AREA_INV_REPORT_ITEM$PK primary key (ID),
  constraint AREA_INV_REPORT_ITEM$FK foreign key (AREA_INV_REPORT_ID) references AREA_INV_REPORT (ID),
  constraint AREA_INV_REPORT_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table INVENTORY_CHECK_SUMMARY_ITEM (
  ID integer auto_increment,
  INVENTORY_CHECK_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  BEGINNING_INV integer(6) not null,
  ACTUAL_COUNT integer(6) not null,
  COST numeric(10, 2) not null,
  constraint INVENTORY_CHECK_SUMMARY_ITEM$PK primary key (ID),
  constraint INVENTORY_CHECK_SUMMARY_ITEM$FK foreign key (INVENTORY_CHECK_ID) references INVENTORY_CHECK (ID),
  constraint INVENTORY_CHECK_SUMMARY_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

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