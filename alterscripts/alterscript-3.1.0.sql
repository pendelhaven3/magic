alter table PURCHASE_RETURN_BAD_STOCK add PAID_IND char(1) null;
alter table PURCHASE_RETURN_BAD_STOCK add PAID_DT datetime null;
alter table PURCHASE_RETURN_BAD_STOCK add PAID_BY integer null;

update PURCHASE_RETURN_BAD_STOCK set PAID_IND = POST_IND;
update PURCHASE_RETURN_BAD_STOCK set PAID_DT = now(), PAID_BY = 1 where PAID_IND = 'Y';

alter table PURCHASE_RETURN_BAD_STOCK modify column PAID_IND char(1) default 'N' not null;

insert into SEQUENCE (NAME) values ('BAD_STOCK_INVENTORY_CHECK_NO_SEQ');

create table BAD_STOCK_INVENTORY_CHECK (
  ID integer auto_increment,
  BAD_STOCK_INVENTORY_CHECK_NO integer not null,
  REMARKS varchar(100) null,
  POST_IND char(1) default 'N' not null,
  POST_DT datetime null,
  POST_BY integer null,
  constraint BAD_STOCK_INVENTORY_CHECK$PK primary key (ID),
  constraint BAD_STOCK_INVENTORY_CHECK$UK unique (BAD_STOCK_INVENTORY_CHECK_NO),
  constraint BAD_STOCK_INVENTORY_CHECK$FK foreign key (POST_BY) references USER (ID)
);

create table BAD_STOCK_INVENTORY_CHECK_ITEM (
  ID integer auto_increment,
  BAD_STOCK_INVENTORY_CHECK_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  QUANTITY integer not null,
  QUANTITY_CHANGE integer null,
  constraint BAD_STOCK_INVENTORY_CHECK_ITEM$PK primary key (ID),
  constraint BAD_STOCK_INVENTORY_CHECK_ITEM$FK foreign key (BAD_STOCK_INVENTORY_CHECK_ID) references BAD_STOCK_INVENTORY_CHECK (ID),
  constraint BAD_STOCK_INVENTORY_CHECK_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);
