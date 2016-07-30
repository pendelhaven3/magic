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

create table PRODUCT_QUANTITY_DISCREPANCY_REPORT (
  DATE date not null,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  PREVIOUS_QTY integer not null,
  QTY_MOVED integer not null,
  NEW_QTY integer not null,
  primary key (DATE, PRODUCT_ID),
  constraint PRODUCT_QUANTITY_DISCREPANCY_REPORT$FK foreign key (PRODUCT_ID) references PRODUCT (ID)
);
