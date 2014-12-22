alter table BAD_STOCK_RETURN add PAID_IND char(1) default 'N' not null;
alter table BAD_STOCK_RETURN add PAID_DT datetime null;
alter table BAD_STOCK_RETURN add PAID_BY integer null;
alter table BAD_STOCK_RETURN add PAYMENT_TERMINAL_ID integer null;
alter table BAD_STOCK_RETURN add constraint BAD_STOCK_RETURN$FK3 foreign key (PAID_BY) references USER (ID);
alter table BAD_STOCK_RETURN add constraint BAD_STOCK_RETURN$FK4 foreign key (PAYMENT_TERMINAL_ID) references PAYMENT_TERMINAL (ID);

update BAD_STOCK_RETURN set PAID_IND = 'Y', PAID_DT = POST_DT, PAID_BY = POST_BY, PAYMENT_TERMINAL_ID = 2
where POST_IND = 'Y';

alter table SALES_RETURN add REMARKS varchar(100) null;
alter table BAD_STOCK_RETURN add REMARKS varchar(100) null;
