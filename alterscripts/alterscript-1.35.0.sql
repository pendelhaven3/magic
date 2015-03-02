insert into SEQUENCE (NAME) values ('PROMO_NO_SEQ');

create table PROMO_REDEMPTION_SEQUENCE (
  PROMO_ID integer not null,
  VALUE integer default 0 not null,
  constraint PROMO_REDEMPTION_SEQUENCE$UK unique (PROMO_ID),
  constraint PROMO_REDEMPTION_SEQUENCE$FK foreign key (PROMO_ID) references PROMO (ID) 
);

alter table PROMO_REDEMPTION add constraint PROMO_REDEMPTION$UK unique key (PROMO_ID, PROMO_REDEMPTION_NO);

insert into PROMO_REDEMPTION_SEQUENCE (PROMO_ID, VALUE) values (1, (select VALUE from SEQUENCE where NAME = 'PROMO_REDEMPTION_NO_SEQ'));

delete from SEQUENCE where NAME = 'PROMO_REDEMPTION_NO_SEQ';

alter table PROMO add PROMO_TYPE_ID integer default 1 not null;
alter table PROMO change PROMO_TYPE_ID PROMO_TYPE_ID integer not null;

alter table PROMO change TARGET_AMOUNT TARGET_AMOUNT numeric(8, 2) null;
alter table PROMO change MANUFACTURER_ID MANUFACTURER_ID integer null;
alter table PROMO change PRODUCT_ID PRODUCT_ID integer null;
alter table PROMO change UNIT UNIT char(3) null;
alter table PROMO change QUANTITY QUANTITY integer null;
