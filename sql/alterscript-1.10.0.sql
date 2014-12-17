alter table STOCK_QTY_CONVERSION add POST_BY integer null;
alter table STOCK_QTY_CONVERSION add constraint STOCK_QTY_CONVERSION$FK foreign key (POST_BY) references USER (ID);

update STOCK_QTY_CONVERSION set POST_BY = 1 where POST_IND = 'Y';

alter table SALES_RETURN add PAID_IND char(1) default 'N' not null;
alter table SALES_RETURN add PAID_DT datetime null;
alter table SALES_RETURN add PAID_BY integer null;
alter table SALES_RETURN add PAYMENT_TERMINAL_ID integer null;
alter table SALES_RETURN add constraint SALES_RETURN$FK3 foreign key (PAID_BY) references USER (ID);
alter table SALES_RETURN add constraint SALES_RETURN$FK4 foreign key (PAYMENT_TERMINAL_ID) references PAYMENT_TERMINAL (ID);

update SALES_RETURN set PAID_IND = 'Y', PAID_DT = POST_DT, PAID_BY = POST_BY, PAYMENT_TERMINAL_ID = 2
where POST_IND = 'Y';

alter table SALES_RETURN change POST_DT POST_DT date null;
