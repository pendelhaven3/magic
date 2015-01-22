rename TABLE PAYMENT_ADJUSTMENT to PAYMENT_PAYMENT_ADJUSTMENT;
alter table PAYMENT_PAYMENT_ADJUSTMENT drop foreign key PAYMENT_ADJUSTMENT$FK, add constraint PAYMENT_PAYMENT_ADJUSTMENT$FK foreign key (PAYMENT_ID) references PAYMENT (ID);
alter table PAYMENT_PAYMENT_ADJUSTMENT drop foreign key PAYMENT_ADJUSTMENT$FK2, add constraint PAYMENT_PAYMENT_ADJUSTMENT$FK2 foreign key (ADJUSTMENT_TYPE_ID) references ADJUSTMENT_TYPE (ID);

create table PAYMENT_ADJUSTMENT (
  ID integer auto_increment,
  PAYMENT_ADJUSTMENT_NO integer not null,
  CUSTOMER_ID integer not null,
  ADJUSTMENT_TYPE_ID integer not null,
  AMOUNT numeric(8, 2) not null,
  POST_IND char(1) default 'N' not null,
  POST_DT date null,
  POST_BY integer null,
  PAID_IND char(1) default 'N' not null,
  PAID_DT datetime null,
  PAID_BY integer null,
  PAYMENT_TERMINAL_ID integer null,
  REMARKS varchar(100) null,
  constraint PAYMENT_ADJUSTMENT$PK primary key (ID),
  constraint PAYMENT_ADJUSTMENT$UK unique (PAYMENT_ADJUSTMENT_NO),
  constraint PAYMENT_ADJUSTMENT$FK foreign key (CUSTOMER_ID) references CUSTOMER (ID),
  constraint PAYMENT_ADJUSTMENT$FK2 foreign key (ADJUSTMENT_TYPE_ID) references ADJUSTMENT_TYPE (ID),
  constraint PAYMENT_ADJUSTMENT$FK3 foreign key (POST_BY) references USER (ID),
  constraint PAYMENT_ADJUSTMENT$FK4 foreign key (PAID_BY) references USER (ID),
  constraint PAYMENT_ADJUSTMENT$FK5 foreign key (PAYMENT_TERMINAL_ID) references PAYMENT_TERMINAL (ID)
);

insert into SEQUENCE (NAME) values ('PAYMENT_ADJUSTMENT_NO_SEQ');
