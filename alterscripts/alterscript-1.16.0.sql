delete from SEQUENCE where NAME = 'ACCT_RECEIVABLE_SUMMARY_NO_SEQ';
insert into SEQUENCE (NAME) values ('NO_MORE_STOCK_ADJUSTMENT_NO_SEQ');

create table NO_MORE_STOCK_ADJUSTMENT (
  ID integer auto_increment,
  NO_MORE_STOCK_ADJUSTMENT_NO integer not null,
  SALES_INVOICE_ID integer not null,
  POST_IND char(1) default 'N' not null,
  POST_DT datetime null,
  POST_BY integer null,
  PAID_IND char(1) default 'N' not null,
  PAID_DT datetime null,
  PAID_BY integer null,
  PAYMENT_TERMINAL_ID integer null,
  REMARKS varchar(100) null,
  constraint NO_MORE_STOCK_ADJUSTMENT$PK primary key (ID),
  constraint NO_MORE_STOCK_ADJUSTMENT$UK unique (NO_MORE_STOCK_ADJUSTMENT_NO),
  constraint NO_MORE_STOCK_ADJUSTMENT$FK foreign key (SALES_INVOICE_ID) references SALES_INVOICE (ID),
  constraint NO_MORE_STOCK_ADJUSTMENT$FK2 foreign key (POST_BY) references USER (ID),
  constraint NO_MORE_STOCK_ADJUSTMENT$FK3 foreign key (PAID_BY) references USER (ID),
  constraint NO_MORE_STOCK_ADJUSTMENT$FK4 foreign key (PAYMENT_TERMINAL_ID) references PAYMENT_TERMINAL (ID)
);

create table NO_MORE_STOCK_ADJUSTMENT_ITEM (
  ID integer auto_increment,
  NO_MORE_STOCK_ADJUSTMENT_ID integer not null,
  SALES_INVOICE_ITEM_ID integer not null,
  QUANTITY integer not null,
  constraint NO_MORE_STOCK_ADJUSTMENT_ITEM$PK primary key (ID),
  constraint NO_MORE_STOCK_ADJUSTMENT_ITEM$FK foreign key (NO_MORE_STOCK_ADJUSTMENT_ID) references NO_MORE_STOCK_ADJUSTMENT (ID),
  constraint NO_MORE_STOCK_ADJUSTMENT_ITEM$FK2 foreign key (SALES_INVOICE_ITEM_ID) references SALES_INVOICE_ITEM (ID)
);

alter table PAYMENT_ADJUSTMENT drop column ADJUSTMENT_TYPE;
