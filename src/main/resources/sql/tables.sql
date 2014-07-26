create table PRODUCT (
  ID integer,
  CODE varchar2(12),
  DESCRIPTION varchar2(50),
  UNIT_IND_CSE varchar2(1) null,
  UNIT_IND_CTN varchar2(1) null,
  UNIT_IND_DOZ varchar2(1) null,
  UNIT_IND_PCS varchar2(1) null,
  AVAIL_QTY_CSE integer(4) default 0 not null,
  AVAIL_QTY_CTN integer(4) default 0 not null,
  AVAIL_QTY_DOZ integer(4) default 0 not null,
  AVAIL_QTY_PCS integer(4) default 0 not null,
  constraint PRODUCT$PK primary key (ID),
  constraint PRODUCT$CODE$UK unique (CODE)
);

create table PRODUCT_PRICE (
  PRODUCT_ID integer,
  UNIT_PRICE_CSE number(10, 2) null,
  UNIT_PRICE_CTN number(10, 2) null,
  UNIT_PRICE_DOZ number(10, 2) null,
  UNIT_PRICE_PCS number(10, 2) null,
  constraint PRODUCT_PRICE$FK foreign key (PRODUCT_ID) references PRODUCT (ID)
);
