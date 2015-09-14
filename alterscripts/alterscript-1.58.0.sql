alter table CREDIT_CARD_STATEMENT_ITEM drop PAID_IND;
alter table CREDIT_CARD_STATEMENT_ITEM drop PAID_DT;

create table CREDIT_CARD_STATEMENT_PAYMENT (
  ID integer auto_increment,
  CREDIT_CARD_STATEMENT_ID integer not null,
  AMOUNT numeric(10, 2) not null,
  PAYMENT_DT date not null,
  PAYMENT_TYPE varchar(20) not null,
  REMARKS varchar(100) not null,
  primary key (ID),
  constraint CREDIT_CARD_STATEMENT_PAYMENT$FK foreign key (CREDIT_CARD_STATEMENT_ID) references CREDIT_CARD_STATEMENT (ID)
);
