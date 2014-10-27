create table INVENTORY_CHECK_SUMMARY_ITEM (
  ID integer auto_increment,
  INVENTORY_CHECK_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  BEGINNING_INV integer(6) not null
  ACTUAL_COUNT integer(6) not null,
  COST numeric(10, 2) not null,
  constraint INVENTORY_CHECK_SUMMARY_ITEM$PK primary key (ID),
  constraint INVENTORY_CHECK_SUMMARY_ITEM$FK foreign key (INVENTORY_CHECK_ID) references INVENTORY_CHECK (ID),
  constraint INVENTORY_CHECK_SUMMARY_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);