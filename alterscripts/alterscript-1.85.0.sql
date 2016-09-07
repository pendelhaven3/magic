insert into SEQUENCE (NAME) values ('INVENTORY_CORRECTION_NO_SEQ');

drop table INVENTORY_CORRECTION;

create table INVENTORY_CORRECTION (
  ID integer auto_increment,
  INVENTORY_CORRECTION_NO integer not null,
  POST_DT datetime not null,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  QUANTITY integer not null,
  UPDATE_DT datetime not null,
  UPDATE_BY integer not null,
  DELETE_IND char(1) default 'N' not null,
  REMARKS varchar(100) null,
  primary key (ID),
  constraint INVENTORY_CORRECTION$FK foreign key (PRODUCT_ID) references PRODUCT (ID),
  constraint INVENTORY_CORRECTION$FK2 foreign key (UPDATE_BY) references USER (ID)
);
