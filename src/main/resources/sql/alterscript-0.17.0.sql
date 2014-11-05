create table SYSTEM_PARAMETER (
  NAME varchar(50) not null,
  VALUE varchar(100) not null,
  constraint SYSTEM_PARAMETER$PK primary key (NAME)
);

insert into SYSTEM_PARAMETER (NAME, VALUE) values ('VERSION', '0.16.0');