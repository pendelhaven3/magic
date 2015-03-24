alter table SALES_RETURN add CANCEL_IND char(1) default 'N' not null;
alter table SALES_RETURN add CANCEL_DT datetime null;
alter table SALES_RETURN add CANCEL_BY integer null;
alter table SALES_RETURN add constraint SALES_RETURN$FK5 foreign key (CANCEL_BY) references USER (ID);

update SYSTEM_PARAMETER set VALUE = '1.43.0' where NAME = 'VERSION';