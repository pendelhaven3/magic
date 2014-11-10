alter table SALES_INVOICE add VAT_AMOUNT numeric(8,2) null;
update SALES_INVOICE set VAT_AMOUNT = 0;
alter table SALES_INVOICE modify column VAT_AMOUNT numeric(8,2) not null;