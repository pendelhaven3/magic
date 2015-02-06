alter table SALES_INVOICE_ITEM add DISCOUNT_1 numeric(4, 2) default 0 not null;
alter table SALES_INVOICE_ITEM add DISCOUNT_2 numeric(4, 2) default 0 not null;
alter table SALES_INVOICE_ITEM add DISCOUNT_3 numeric(4, 2) default 0 not null;
alter table SALES_INVOICE_ITEM add FLAT_RATE_DISCOUNT numeric(8, 2) default 0 not null;