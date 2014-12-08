alter table PAYMENT add ENCODER integer null;
alter table PAYMENT add constraint PAYMENT$FK4 foreign key (ENCODER) references USER (ID);
update PAYMENT set ENCODER = 1;
alter table PAYMENT change ENCODER ENCODER integer not null;
