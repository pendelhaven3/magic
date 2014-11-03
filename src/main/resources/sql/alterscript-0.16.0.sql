create table AREA (
  ID integer auto_increment,
  NAME varchar(50) not null,
  constraint AREA$PK primary key (ID),
  constraint AREA$UK unique (NAME)
);