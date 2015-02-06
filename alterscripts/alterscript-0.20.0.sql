create table PAYMENT_TERMINAL (
  ID integer auto_increment,
  NAME varchar(50) not null,
  constraint PAYMENT_TERMINAL$PK primary key (ID),
  constraint PAYMENT_TERMINAL$UK unique (NAME)
);

create table PAYMENT_TERMINAL_USER (
  USER_ID integer not null,
  PAYMENT_TERMINAL_ID integer not null,
  constraint PAYMENT_TERMINAL_USER$UK unique (USER_ID),
  constraint PAYMENT_TERMINAL_USER$FK foreign key (PAYMENT_TERMINAL_ID) references PAYMENT_TERMINAL (ID),
  constraint PAYMENT_TERMINAL_USER$FK2 foreign key (USER_ID) references USER (ID)
);

insert into PAYMENT_TERMINAL (ID, NAME) values (1, 'CASHIER');
insert into PAYMENT_TERMINAL (ID, NAME) values (2, 'OFFICE');

alter table PAYMENT add RECEIVED_BY integer null;
update PAYMENT set RECEIVED_BY = 1;
alter table PAYMENT modify column RECEIVED_BY integer not null;
alter table PAYMENT add constraint PAYMENT$FK2 foreign key (RECEIVED_BY) references USER (ID);

alter table PAYMENT add PAYMENT_TERMINAL_ID integer null;
update PAYMENT set PAYMENT_TERMINAL_ID = 1;
alter table PAYMENT modify column PAYMENT_TERMINAL_ID integer not null;
alter table PAYMENT add constraint PAYMENT$FK3 foreign key (PAYMENT_TERMINAL_ID) references PAYMENT_TERMINAL (ID);
