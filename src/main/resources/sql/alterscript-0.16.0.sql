create table AREA (
  ID integer auto_increment,
  NAME varchar(50) not null,
  constraint AREA$PK primary key (ID),
  constraint AREA$UK unique (NAME)
);

alter table AREA_INV_REPORT drop AREA;
alter table AREA_INV_REPORT add AREA_ID integer null;

insert into AREA (ID, NAME) values (1, 'BODEGA');

update AREA_INV_REPORT set AREA_ID = 1;