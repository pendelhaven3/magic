create table ADJUSTMENT_TYPE (
  ID integer auto_increment,
  CODE varchar(12) not null,
  DESCRIPTION varchar(100) not null,
  constraint ADJUSTMENT_TYPE$PK primary key (ID),
  constraint ADJUSTMENT_TYPE$UK unique (CODE)
);

insert into ADJUSTMENT_TYPE (ID, CODE, DESCRIPTION) values (1, 'SR', 'SALES RETURN');
insert into ADJUSTMENT_TYPE (ID, CODE, DESCRIPTION) values (2, 'BSR', 'BAD STOCK RETURN');
