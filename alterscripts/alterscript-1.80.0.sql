create table DAILY_PRODUCT_STARTING_QUANTITY_CHECK (
  DATE date not null,
  COUNT integer not null,
  primary key (DATE)
);

create table DAILY_PRODUCT_STARTING_QUANTITY (
  DATE date not null,
  PRODUCT_ID integer not null,
  AVAIL_QTY_CSE integer default 0 not null,
  AVAIL_QTY_TIE integer default 0 not null,
  AVAIL_QTY_CTN integer default 0 not null,
  AVAIL_QTY_DOZ integer default 0 not null,
  AVAIL_QTY_PCS integer default 0 not null,
  primary key (DATE, PRODUCT_ID),
  constraint DAILY_PRODUCT_STARTING_QUANTITY$PK foreign key (PRODUCT_ID) references PRODUCT (ID)
);
