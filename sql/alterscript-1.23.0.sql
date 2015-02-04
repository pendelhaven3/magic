insert into SEQUENCE (NAME) values ('PURCHASE_RETURN_BAD_STOCK_NO_SEQ');

create table BAD_PURCHASE_RETURN (
  ID integer auto_increment,
  BAD_PURCHASE_RETURN_NO integer not null,
  SUPPLIER_ID integer not null,
  POST_IND char(1) default 'N' not null,
  POST_DT date null,
  POST_BY integer null,
  REMARKS varchar(100) null,
  constraint BAD_PURCHASE_RETURN$PK primary key (ID),
  constraint BAD_PURCHASE_RETURN$UK unique (BAD_PURCHASE_RETURN_NO),
  constraint BAD_PURCHASE_RETURN$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID),
  constraint BAD_PURCHASE_RETURN$FK2 foreign key (POST_BY) references USER (ID)
);

create table BAD_PURCHASE_RETURN_ITEM (
  ID integer auto_increment,
  BAD_PURCHASE_RETURN_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  QUANTITY integer not null,
  UNIT_COST numeric(10, 2) not null,
  constraint BAD_PURCHASE_RETURN_ITEM$PK primary key (ID),
  constraint BAD_PURCHASE_RETURN_ITEM$FK foreign key (BAD_PURCHASE_RETURN_ID) references BAD_PURCHASE_RETURN (ID),
  constraint BAD_PURCHASE_RETURN_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);
