create table BAD_STOCK (
  PRODUCT_ID integer not null,
  AVAIL_QTY_CSE integer(4),
  AVAIL_QTY_TIE integer(4),
  AVAIL_QTY_CTN integer(4),
  AVAIL_QTY_DOZ integer(4),
  AVAIL_QTY_PCS integer(4),
  primary key (PRODUCT_ID),
  constraint BAD_STOCK$FK foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table BAD_STOCK_ADJUSTMENT_IN (
  ID integer auto_increment,
  BAD_STOCK_ADJUSTMENT_IN_NO integer not null,
  POST_IND char(1) default 'N' not null,
  REMARKS varchar(100) null,
  POST_DT datetime null,
  POSTED_BY integer null,
  constraint BAD_STOCK_ADJUSTMENT_IN$PK primary key (ID),
  constraint BAD_STOCK_ADJUSTMENT_IN$UK unique (BAD_STOCK_ADJUSTMENT_IN_NO),
  constraint BAD_STOCK_ADJUSTMENT_IN$FK foreign key (POSTED_BY) references USER (ID)
);

create table BAD_STOCK_ADJUSTMENT_IN_ITEM (
  ID integer auto_increment,
  BAD_STOCK_ADJUSTMENT_IN_ID integer not null,
  BAD_STOCK_ID integer not null,
  UNIT char(3) not null,
  QUANTITY integer not null,
  constraint BAD_STOCK_ADJUSTMENT_IN_ITEM$PK primary key (ID),
  constraint BAD_STOCK_ADJUSTMENT_IN_ITEM$FK foreign key (BAD_STOCK_ADJUSTMENT_IN_ID) references BAD_STOCK_ADJUSTMENT_IN (ID),
  constraint BAD_STOCK_ADJUSTMENT_IN_ITEM$FK2 foreign key (BAD_STOCK_ID) references BAD_STOCK (PRODUCT_ID)
);
