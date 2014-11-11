alter table SALES_INVOICE add VAT_AMOUNT numeric(8,2) null;
update SALES_INVOICE set VAT_AMOUNT = 0;
alter table SALES_INVOICE modify column VAT_AMOUNT numeric(8,2) not null;
update SYSTEM_PARAMETER set VALUE = '0.18.0' where NAME = 'VERSION';