alter table PURCHASE_RETURN add PAID_IND char(1) default 'N' not null;
alter table PURCHASE_RETURN add PAID_DT date null;
alter table PURCHASE_RETURN add PAID_BY integer null;
alter table PURCHASE_RETURN add constraint PURCHASE_RETURN$FK3 foreign key (PAID_BY) references USER (ID);
