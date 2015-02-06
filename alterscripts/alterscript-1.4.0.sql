alter table PAYMENT add PAYMENT_TERMINAL_ID integer null;
alter table PAYMENT add constraint PAYMENT$FK3 foreign key (PAYMENT_TERMINAL_ID) references PAYMENT_TERMINAL (ID);

update PAYMENT set PAYMENT_TERMINAL_ID = 2 where POST_IND = 'Y';

alter table PAYMENT change POST_DT POST_DT datetime null;
