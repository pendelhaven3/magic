create table ACCT_RECEIVABLE_SUMMARY (
  ID integer auto_increment,
  ACCT_RECEIVABLE_SUMMARY_NO integer not null,
  CUSTOMER_ID integer not null,
  constraint ACCT_RECEIVABLE_SUMMARY$PK primary key (ID),
  constraint ACCT_RECEIVABLE_SUMMARY$UK unique (ACCT_RECEIVABLE_SUMMARY_NO),
  constraint ACCT_RECEIVABLE_SUMMARY$FK foreign key (CUSTOMER_ID) references CUSTOMER (ID)
);

create table ACCT_RECEIVABLE_SUMMARY_ITEM (
  ID integer auto_increment,
  ACCT_RECEIVABLE_SUMMARY_ID integer not null,
  SALES_INVOICE_ID integer not null,
  constraint ACCT_RECEIVABLE_SUMMARY_ITEM$PK primary key (ID),
  constraint ACCT_RECEIVABLE_SUMMARY_ITEM$FK foreign key (ACCT_RECEIVABLE_SUMMARY_ID) references ACCT_RECEIVABLE_SUMMARY (ID),
  constraint ACCT_RECEIVABLE_SUMMARY_ITEM$FK2 foreign key (SALES_INVOICE_ID) references SALES_INVOICE (ID)
);

create table SALES_RETURN (
  ID integer auto_increment,
  SALES_RETURN_NO integer not null,
  SALES_INVOICE_ID integer not null,
  POST_IND char(1) default 'N' not null,
  constraint SALES_RETURN$PK primary key (ID),
  constraint SALES_RETURN$UK unique (SALES_RETURN_NO),
  constraint SALES_RETURN$FK foreign key (SALES_INVOICE_ID) references SALES_INVOICE (ID)
);

create table SALES_RETURN_ITEM (
  ID integer auto_increment,
  SALES_RETURN_ID integer not null,
  SALES_INVOICE_ITEM_ID integer not null,
  QUANTITY integer not null,
  constraint SALES_RETURN_ITEM$PK primary key (ID),
  constraint SALES_RETURN_ITEM$FK foreign key (SALES_RETURN_ID) references SALES_RETURN (ID),
  constraint SALES_RETURN_ITEM$FK2 foreign key (SALES_INVOICE_ITEM_ID) references SALES_INVOICE_ITEM (ID)
);

insert into SEQUENCE (NAME) values ('ACCT_RECEIVABLE_SUMMARY_NO_SEQ');
insert into SEQUENCE (NAME) values ('SALES_RETURN_NO_SEQ');
