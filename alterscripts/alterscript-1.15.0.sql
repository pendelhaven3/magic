create table PRODUCT_PRICE_HISTORY (
  ID integer auto_increment,
  PRICING_SCHEME_ID integer default 1 not null,
  PRODUCT_ID integer not null,
  UPDATE_DT datetime not null,
  UPDATE_BY integer not null,
  UNIT_PRICE_CSE numeric(10, 2) null,
  UNIT_PRICE_TIE numeric(10, 2) null,
  UNIT_PRICE_CTN numeric(10, 2) null,
  UNIT_PRICE_DOZ numeric(10, 2) null,
  UNIT_PRICE_PCS numeric(10, 2) null,
  constraint PRODUCT_PRICE_HISTORY$PK primary key (ID),
  constraint PRODUCT_PRICE_HISTORY$FK foreign key (PRICING_SCHEME_ID) references PRICING_SCHEME (ID),
  constraint PRODUCT_PRICE_HISTORY$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID),
  constraint PRODUCT_PRICE_HISTORY$FK3 foreign key (UPDATE_BY) references USER (ID)
);
