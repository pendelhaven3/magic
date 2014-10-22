alter table USER add SUPERVISOR_IND char(1) default 'N' not null;
update USER set SUPERSIVOR_IND = 'Y' where ID = 1;
alter table PRODUCT add COMPANY_LIST_PRICE numeric(10, 2) default 0 not null;