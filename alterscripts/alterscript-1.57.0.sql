create table CREDIT_CARD_STATEMENT (
  ID integer auto_increment,
  STATEMENT_NO integer not null,
  CREDIT_CARD_ID integer not null,
  STATEMENT_DT date not null,
  primary key (ID),
  unique key (STATEMENT_NO),
  constraint CREDIT_CARD_STATEMENT$FK foreign key (CREDIT_CARD_ID) references CREDIT_CARD (ID)
);

create table CREDIT_CARD_STATEMENT_ITEM (
  ID integer auto_increment,
  CREDIT_CARD_STATEMENT_ID integer not null,
  PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT_ID integer not null,
  primary key (ID),
  constraint CREDIT_CARD_STATEMENT_ITEM$FK foreign key (CREDIT_CARD_STATEMENT_ID) references CREDIT_CARD_STATEMENT (ID),
  constraint CREDIT_CARD_STATEMENT_ITEM$FK2 foreign key (PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT_ID) references PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT (ID)
);

insert into SEQUENCE (NAME) values ('CREDIT_CARD_STATEMENT_NO_SEQ');
