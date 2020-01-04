insert into SEQUENCE (NAME) values ('BAD_STOCK_ADJUSTMENT_IN_NO_SEQ');
insert into SEQUENCE (NAME) values ('BAD_STOCK_ADJUSTMENT_OUT_NO_SEQ');
insert into SEQUENCE (NAME) values ('BAD_STOCK_REPORT_NO_SEQ');

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
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  QUANTITY integer not null,
  constraint BAD_STOCK_ADJUSTMENT_IN_ITEM$PK primary key (ID),
  constraint BAD_STOCK_ADJUSTMENT_IN_ITEM$FK foreign key (BAD_STOCK_ADJUSTMENT_IN_ID) references BAD_STOCK_ADJUSTMENT_IN (ID),
  constraint BAD_STOCK_ADJUSTMENT_IN_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table BAD_STOCK_ADJUSTMENT_OUT (
  ID integer auto_increment,
  BAD_STOCK_ADJUSTMENT_OUT_NO integer not null,
  POST_IND char(1) default 'N' not null,
  REMARKS varchar(100) null,
  POST_DT datetime null,
  POSTED_BY integer null,
  constraint BAD_STOCK_ADJUSTMENT_OUT$PK primary key (ID),
  constraint BAD_STOCK_ADJUSTMENT_OUT$UK unique (BAD_STOCK_ADJUSTMENT_OUT_NO),
  constraint BAD_STOCK_ADJUSTMENT_OUT$FK foreign key (POSTED_BY) references USER (ID)
);

create table BAD_STOCK_ADJUSTMENT_OUT_ITEM (
  ID integer auto_increment,
  BAD_STOCK_ADJUSTMENT_OUT_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  QUANTITY integer not null,
  constraint BAD_STOCK_ADJUSTMENT_OUT_ITEM$PK primary key (ID),
  constraint BAD_STOCK_ADJUSTMENT_OUT_ITEM$FK foreign key (BAD_STOCK_ADJUSTMENT_OUT_ID) references BAD_STOCK_ADJUSTMENT_OUT (ID),
  constraint BAD_STOCK_ADJUSTMENT_OUT_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table BAD_STOCK_REPORT (
  ID integer auto_increment,
  BAD_STOCK_REPORT_NO integer not null,
  POST_IND char(1) default 'N' not null,
  LOCATION varchar(50) not null,
  REMARKS varchar(100) null,
  POST_DT datetime null,
  POST_BY integer null,
  constraint BAD_STOCK_REPORT$PK primary key (ID),
  constraint BAD_STOCK_REPORT$UK unique (BAD_STOCK_REPORT_NO),
  constraint BAD_STOCK_REPORT$FK foreign key (POST_BY) references USER (ID)
);

create table BAD_STOCK_REPORT_ITEM (
  ID integer auto_increment,
  BAD_STOCK_REPORT_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  QUANTITY integer not null,
  constraint BAD_STOCK_REPORT_ITEM$PK primary key (ID),
  constraint BAD_STOCK_REPORT_ITEM$FK foreign key (BAD_STOCK_REPORT_ID) references BAD_STOCK_REPORT (ID),
  constraint BAD_STOCK_REPORT_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);
