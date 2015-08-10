create table PROMO_POINTS_CLAIM (
  ID integer auto_increment,
  PROMO_ID integer not null,
  CLAIM_NO integer not null,
  CUSTOMER_ID integer not null,
  POINTS integer not null,
  REMARKS varchar(200) not null,
  CLAIM_DT datetime not null,
  CLAIM_BY integer not null,
  primary key (ID),
  unique key (CLAIM_NO),
  constraint PROMO_POINTS_CLAIM$FK foreign key (PROMO_ID) references PROMO (ID),
  constraint PROMO_POINTS_CLAIM$FK2 foreign key (CUSTOMER_ID) references CUSTOMER (ID),
  constraint PROMO_POINTS_CLAIM$FK3 foreign key (CLAIM_BY) references USER (ID)
);