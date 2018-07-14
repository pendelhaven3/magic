create table SCHEDULED_PRICE_CHANGE (
  ID integer auto_increment,
  EFFECTIVE_DT date not null,
  PRODUCT_ID integer not null,
  PRICING_SCHEME_ID integer default 1 not null,
  UNIT_PRICE_CSE numeric(10, 2) default 0 not null,
  UNIT_PRICE_TIE numeric(10, 2) default 0 not null,
  UNIT_PRICE_CTN numeric(10, 2) default 0 not null,
  UNIT_PRICE_DOZ numeric(10, 2) default 0 not null,
  UNIT_PRICE_PCS numeric(10, 2) default 0 not null,
  COMPANY_LIST_PRICE numeric(10, 2) null,
  APPLIED char(1) default 'N' not null,
  CREATE_DT datetime not null,
  CREATE_BY integer not null,
  primary key (ID),
  constraint SCHEDULED_PRICE_CHANGE$FK foreign key (PRODUCT_ID) references PRODUCT (ID),
  constraint SCHEDULED_PRICE_CHANGE$FK2 foreign key (PRICING_SCHEME_ID) references PRICING_SCHEME (ID),
  constraint SCHEDULED_PRICE_CHANGE$FK3 foreign key (CREATE_BY) references USER (ID)
);
