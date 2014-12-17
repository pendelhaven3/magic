alter table RECEIVING_RECEIPT add POST_DT date null;
alter table RECEIVING_RECEIPT add POST_BY integer null;
alter table RECEIVING_RECEIPT add CANCEL_IND char(1) default 'N' not null;
alter table RECEIVING_RECEIPT add CANCEL_DT date null;
alter table RECEIVING_RECEIPT add CANCEL_BY integer null;
alter table RECEIVING_RECEIPT add constraint RECEIVING_RECEIPT$FK5 foreign key (POST_BY) references USER (ID);
alter table RECEIVING_RECEIPT add constraint RECEIVING_RECEIPT$FK6 foreign key (CANCEL_BY) references USER (ID);

update RECEIVING_RECEIPT set POST_BY = 1, POST_DT = RECEIVED_DT where POST_IND = 'Y';
