create table PROMO_RAFFLE_PARTICIPATING_ITEMS (
  PROMO_ID integer not null,
  PRODUCT_ID integer not null,
  constraint PROMO_RAFFLE_PARTICIPATING_ITEMS$PK primary key (PROMO_ID, PRODUCT_ID),
  constraint PROMO_RAFFLE_PARTICIPATING_ITEMS$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);
