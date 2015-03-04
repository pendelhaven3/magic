alter table PROMO add ACTIVE_IND char(1) default 'Y' not null;

create table PROMO_TYPE_1_RULE (
  ID integer auto_increment,
  PROMO_ID integer not null,
  TARGET_AMOUNT numeric(8, 2) not null,
  MANUFACTURER_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(3) not null,
  QUANTITY integer not null,
  primary key (ID),
  constraint PROMO_TYPE_1_RULE$FK foreign key (PROMO_ID) references PROMO (ID),
  constraint PROMO_TYPE_1_RULE$FK2 foreign key (MANUFACTURER_ID) references MANUFACTURER (ID),
  constraint PROMO_TYPE_1_RULE$FK3 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

insert into PROMO_TYPE_1_RULE
(PROMO_ID, TARGET_AMOUNT, MANUFACTURER_ID, PRODUCT_ID, UNIT, QUANTITY)
select ID, TARGET_AMOUNT, MANUFACTURER_ID, PRODUCT_ID, UNIT, QUANTITY
from PROMO
where ID = 1;

alter table PROMO drop foreign key PROMO$FK2;
alter table PROMO drop foreign key PROMO$FK;
alter table PROMO drop key PROMO$FK2;
alter table PROMO drop key PROMO$FK;

alter table PROMO drop column TARGET_AMOUNT;
alter table PROMO drop column MANUFACTURER_ID;
alter table PROMO drop column PRODUCT_ID;
alter table PROMO drop column UNIT;
alter table PROMO drop column QUANTITY;
