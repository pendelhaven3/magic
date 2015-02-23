alter table AREA_INV_REPORT add CREATE_BY integer default 1 not null;
alter table AREA_INV_REPORT change CREATE_BY CREATE_BY integer not null;
alter table AREA_INV_REPORT add constraint AREA_INV_REPORT$FK3 foreign key (CREATE_BY) references USER (ID);
alter table AREA_INV_REPORT add REVIEW_IND char(1) default 'N' not null;
alter table AREA_INV_REPORT add REVIEWER varchar(50) null;
