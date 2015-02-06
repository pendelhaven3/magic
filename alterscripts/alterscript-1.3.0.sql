alter table SALES_INVOICE_ITEM add COST numeric(10, 2) null;

update SALES_INVOICE_ITEM a
set a.COST = ( select FINAL_COST_CSE from PRODUCT b where b.ID = a.PRODUCT_ID )
where a.COST is null
and a.UNIT = 'CSE';

update SALES_INVOICE_ITEM a
set a.COST = ( select FINAL_COST_TIE from PRODUCT b where b.ID = a.PRODUCT_ID )
where a.COST is null
and a.UNIT = 'TIE';

update SALES_INVOICE_ITEM a
set a.COST = ( select FINAL_COST_CTN from PRODUCT b where b.ID = a.PRODUCT_ID )
where a.COST is null
and a.UNIT = 'CTN';

update SALES_INVOICE_ITEM a
set a.COST = ( select FINAL_COST_DOZ from PRODUCT b where b.ID = a.PRODUCT_ID )
where a.COST is null
and a.UNIT = 'DOZ';

update SALES_INVOICE_ITEM a
set a.COST = ( select FINAL_COST_PCS from PRODUCT b where b.ID = a.PRODUCT_ID )
where a.COST is null
and a.UNIT = 'PCS';

alter table SALES_INVOICE_ITEM add COST numeric(10, 2) not null;
