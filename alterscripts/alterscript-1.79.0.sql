create table INVENTORY_CORRECTION (
  ID integer auto_increment,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  NEW_QUANTITY integer not null,
  OLD_QUANTITY integer not null,
  COST numeric(10, 2) null,
  POST_DT datetime not null,
  POST_BY integer not null,
  REMARKS varchar(100) null,
  primary key (ID),
  constraint INVENTORY_CORRECTION$FK foreign key (PRODUCT_ID) references PRODUCT (ID),
  constraint INVENTORY_CORRECTION$FK2 foreign key (POST_BY) references USER (ID)
);
