alter table PAYMENT add ENCODER integer null;
alter table PAYMENT add constraint PAYMENT$FK4 foreign key (ENCODER) references USER (ID);
update PAYMENT set ENCODER = 1;
alter table PAYMENT change ENCODER ENCODER integer not null;

alter table PAYMENT add CANCEL_IND char(1) default 'N' not null;
alter table PAYMENT add CANCEL_DT date null;
alter table PAYMENT add CANCEL_BY integer null;
alter table PAYMENT add constraint PAYMENT$FK5 foreign key (CANCEL_BY) references USER (ID);
