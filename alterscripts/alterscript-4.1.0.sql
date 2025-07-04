create table SALES_COMPLIANCE_PROJECT (
  ID integer auto_increment,
  SALES_COMPLIANCE_PROJECT_NO integer not null,
  NAME varchar(50) not null,
  START_DT date not null,
  END_DT date not null,
  TARGET_AMOUNT numeric(10) not null,
  primary key (ID)
);


create table SALES_COMPLIANCE_PROJECT_SALES_INVOICE (
  ID integer auto_increment,
  SALES_COMPLIANCE_PROJECT_ID integer not null,
  SALES_INVOICE_ID integer not null,
  constraint SALES_COMPLIANCE_PROJECT_SALES_INVOICE$PK primary key (ID),
  constraint SALES_COMPLIANCE_PROJECT_SALES_INVOICE$FK foreign key (SALES_COMPLIANCE_PROJECT_ID) references SALES_COMPLIANCE_PROJECT (ID),
  constraint SALES_COMPLIANCE_PROJECT_SALES_INVOICE$FK2 foreign key (SALES_INVOICE_ID) references SALES_INVOICE (ID)  
);


create table SALES_COMPLIANCE_PROJECT_SALES_INVOICE_ITEM (
  ID integer auto_increment,
  SALES_COMPLIANCE_PROJECT_SALES_INVOICE_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  QUANTITY integer not null,
  ORIGINAL_QUANTITY integer not null,
  UNIT_PRICE numeric(10, 2) not null,
  COST numeric(10, 2) not null,
  DISCOUNT_1 numeric(4, 2) default 0 not null,
  DISCOUNT_2 numeric(4, 2) default 0 not null,
  DISCOUNT_3 numeric(4, 2) default 0 not null,
  FLAT_RATE_DISCOUNT numeric(8, 2) default 0 not null,
  constraint SALES_COMPLIANCE_PROJECT_SALES_INVOICE_ITEM$PK primary key (ID),
  constraint SALES_COMPLIANCE_PROJECT_SALES_INVOICE_ITEM$FK foreign key (SALES_COMPLIANCE_PROJECT_SALES_INVOICE_ID) references SALES_COMPLIANCE_PROJECT_SALES_INVOICE (ID),
  constraint SALES_COMPLIANCE_PROJECT_SALES_INVOICE_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);


insert into SEQUENCE (NAME) values ('SALES_COMPLIANCE_PROJECT_NO_SEQ');

update SYSTEM_PARAMETER set value = '4.1.0';
