alter table STOCK_QTY_CONVERSION add POST_BY integer null;
alter table STOCK_QTY_CONVERSION add constraint STOCK_QTY_CONVERSION$FK foreign key (POST_BY) references USER (ID);

update STOCK_QTY_CONVERSION set POST_BY = 1 where POST_IND = 'Y';
