alter table BAD_STOCK_RETURN add CANCEL_IND char(1) default 'N' not null;
alter table BAD_STOCK_RETURN add CANCEL_DT datetime null;
alter table BAD_STOCK_RETURN add CANCEL_BY integer null;
alter table BAD_STOCK_RETURN add constraint BAD_STOCK_RETURN$FK5 foreign key (CANCEL_BY) references USER (ID);

