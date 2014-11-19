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

insert into SEQUENCE (NAME) values ('ACCT_RECEIVABLE_SUMMARY_NO_SEQ');