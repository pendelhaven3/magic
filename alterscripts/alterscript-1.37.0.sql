create table PROMO_TYPE_3_RULE (
  ID integer auto_increment,
  PROMO_ID integer not null,
  TARGET_AMOUNT numeric(8, 2) not null,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  QUANTITY integer not null,
  primary key (ID),
  constraint PROMO_TYPE_3_RULE$FK foreign key (PROMO_ID) references PROMO (ID),
  constraint PROMO_TYPE_3_RULE$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table PROMO_TYPE_3_RULE_ITEM (
  ID integer auto_increment,
  PROMO_TYPE_3_RULE_ID integer not null,
  PRODUCT_ID integer not null,
  primary key (ID),
  constraint PROMO_TYPE_3_RULE_ITEM$FK foreign key (PROMO_TYPE_3_RULE_ID) references PROMO_TYPE_3_RULE (ID),
  constraint PROMO_TYPE_3_RULE_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);