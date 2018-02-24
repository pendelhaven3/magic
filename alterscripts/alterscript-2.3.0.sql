create table PROMO_TYPE_6_RULE (
  ID integer auto_increment,
  PROMO_ID integer not null,
  TARGET_QUANTITY numeric(8, 2) not null,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  QUANTITY integer not null,
  primary key (ID),
  constraint PROMO_TYPE_6_RULE$FK foreign key (PROMO_ID) references PROMO (ID),
  constraint PROMO_TYPE_6_RULE$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table PROMO_TYPE_6_RULE_PROMO_PRODUCT (
  ID integer auto_increment,
  PROMO_TYPE_6_RULE_ID integer not null,
  PRODUCT_ID integer not null,
  primary key (ID),
  unique key (PROMO_TYPE_6_RULE_ID, PRODUCT_ID),
  constraint PROMO_TYPE_6_RULE_PROMO_PRODUCT$FK foreign key (PROMO_TYPE_6_RULE_ID) references PROMO_TYPE_6_RULE (ID),
  constraint PROMO_TYPE_6_RULE_PROMO_PRODUCT$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);
