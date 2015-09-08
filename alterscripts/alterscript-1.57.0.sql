create table CREDIT_CARD_STATEMENT (
  ID integer auto_increment,
  CREDIT_CARD_ID integer not null,
  STATEMENT_DT date not null,
  POST_IND varchar(1) default 'N' not null,
  primary key (ID),
  unique key (CREDIT_CARD_ID, STATEMENT_DT),
  constraint CREDIT_CARD_STATEMENT$FK foreign key (CREDIT_CARD_ID) references CREDIT_CARD (ID)
);

create table CREDIT_CARD_STATEMENT_ITEM (
  ID integer auto_increment,
  CREDIT_CARD_STATEMENT_ID integer not null,
  PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT_ID integer not null,
  PAID_IND varchar(1) default 'N' not null,
  PAID_DT date null,
  primary key (ID),
  constraint CREDIT_CARD_STATEMENT_ITEM$FK foreign key (CREDIT_CARD_STATEMENT_ID) references CREDIT_CARD_STATEMENT (ID),
  constraint CREDIT_CARD_STATEMENT_ITEM$FK2 foreign key (PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT_ID) references PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT (ID)
);

alter table CREDIT_CARD add CUTOFF_DT integer null;

alter table PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT drop MARK_IND;
alter table PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT drop STATEMENT_DT;
