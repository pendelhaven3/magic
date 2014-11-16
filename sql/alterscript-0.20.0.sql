create table PAYMENT_TERMINAL (
  ID integer auto_increment,
  NAME varchar(50) not null,
  constraint PAYMENT_TERMINAL$PK primary key (ID),
  constraint PAYMENT_TERMINAL$UK unique (NAME)
);