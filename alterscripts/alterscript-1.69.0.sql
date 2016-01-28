create table PROMO_TYPE_5_RULE (
  ID integer auto_increment,
  PROMO_ID integer not null,
  TARGET_AMOUNT numeric(8, 2) not null,
  REBATE numeric(8, 2) not null,
  primary key (ID),
  constraint PROMO_TYPE_5_RULE$FK foreign key (PROMO_ID) references PROMO (ID)
);

create table PROMO_TYPE_5_RULE_PROMO_PRODUCT (
  ID integer auto_increment,
  PROMO_TYPE_5_RULE_ID integer not null,
  PRODUCT_ID integer not null,
  primary key (ID),
  unique key (PROMO_TYPE_5_RULE_ID, PRODUCT_ID),
  constraint PROMO_TYPE_5_RULE_PROMO_PRODUCT$FK foreign key (PROMO_TYPE_5_RULE_ID) references PROMO_TYPE_5_RULE (ID),
  constraint PROMO_TYPE_5_RULE_PROMO_PRODUCT$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table PROMO_REDEMPTION_REBATE (
  ID integer auto_increment,
  PROMO_REDEMPTION_ID integer not null,
  PAYMENT_ADJUSTMENT_ID integer not null,
  primary key (ID),
  constraint PROMO_REDEMPTION_REBATE$FK foreign key (PROMO_REDEMPTION_ID) references PROMO_REDEMPTION (ID),
  constraint PROMO_REDEMPTION_REBATE$FK2 foreign key (PAYMENT_ADJUSTMENT_ID) references PAYMENT_ADJUSTMENT (ID)
);
