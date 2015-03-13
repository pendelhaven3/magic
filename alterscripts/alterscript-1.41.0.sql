create unique index PAYMENT$UK on PAYMENT (PAYMENT_NO);
create unique index PURCHASE_PAYMENT$UK on PURCHASE_PAYMENT (PURCHASE_PAYMENT_NO);
create unique index SUPPLIER$UK2 on SUPPLIER (CODE);

alter table SALES_INVOICE change POST_DT POST_DT datetime not null;
alter table SALES_INVOICE change CANCEL_DT CANCEL_DT datetime null;
alter table RECEIVING_RECEIPT change POST_DT POST_DT datetime null;
alter table ADJUSTMENT_OUT change POST_DT POST_DT datetime null;
alter table ADJUSTMENT_IN change POST_DT POST_DT datetime null;
alter table STOCK_QTY_CONVERSION change POST_DT POST_DT datetime null;
alter table SALES_RETURN change POST_DT POST_DT datetime null;

alter table INVENTORY_CHECK add POST_DT datetime null;
alter table INVENTORY_CHECK add POST_BY integer default 1 not null;
alter table INVENTORY_CHECK add constraint INVENTORY_CHECK$FK foreign key (POST_BY) references USER (ID);
alter table INVENTORY_CHECK change POST_BY POST_BY integer not null;
update INVENTORY_CHECK set POST_DT = INVENTORY_DT;

commit;

alter table PROMO_REDEMPTION change POST_DT POST_DT datetime null;

