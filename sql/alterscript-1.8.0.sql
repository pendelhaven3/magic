alter table SALES_RETURN change POST_DT POST_DT datetime null;

create table PAYMENT_SALES_RETURN (
  PAYMENT_ID integer not null,
  SALES_RETURN_ID integer not null,
  constraint PAYMENT_SALES_RETURN$PK primary key (PAYMENT_ID, SALES_RETURN_ID),
  constraint PAYMENT_SALES_RETURN$FK foreign key (PAYMENT_ID) references PAYMENT (ID),
  constraint PAYMENT_SALES_RETURN$FK2 foreign key (SALES_RETURN_ID) references SALES_RETURN (ID)
);
