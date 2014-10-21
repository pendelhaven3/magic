alter table USER add SUPERVISOR_IND char(1) default 'N' not null;
update USER set SUPERSIVOR_IND = 'Y' where ID = 1;