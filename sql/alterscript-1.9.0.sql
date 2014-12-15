create table BAD_STOCK_RETURN (
  ID integer auto_increment,
  BAD_STOCK_RETURN_NO integer not null,
  CUSTOMER_ID integer not null,
  POST_IND char(1) default 'N' not null,
  POST_DT datetime null,
  POST_BY integer null,
  constraint BAD_STOCK_RETURN$PK primary key (ID),
  constraint BAD_STOCK_RETURN$UK unique (BAD_STOCK_RETURN_NO),
  constraint BAD_STOCK_RETURN$FK foreign key (CUSTOMER_ID) references CUSTOMER (ID),
  constraint BAD_STOCK_RETURN$FK2 foreign key (POST_BY) references USER (ID)
);

create table BAD_STOCK_RETURN_ITEM (
  ID integer auto_increment,
  BAD_STOCK_RETURN_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  QUANTITY integer not null,
  UNIT_PRICE numeric(10, 2) not null,
  constraint BAD_STOCK_RETURN_ITEM$PK primary key (ID),
  constraint BAD_STOCK_RETURN_ITEM$FK foreign key (BAD_STOCK_RETURN_ID) references BAD_STOCK_RETURN (ID),
  constraint BAD_STOCK_RETURN_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

insert into SEQUENCE (NAME) values ('BAD_STOCK_RETURN_NO_SEQ');
