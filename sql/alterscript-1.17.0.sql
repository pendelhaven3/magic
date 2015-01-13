alter table BAD_STOCK_RETURN_ITEM add COST numeric(10, 2) null;

update BAD_STOCK_RETURN_ITEM a
set a.COST = (
  select FINAL_COST_PCS
  from PRODUCT b
  where b.ID = a.PRODUCT_ID
)
where a.UNIT = 'PCS';

update BAD_STOCK_RETURN_ITEM a
set a.COST = (
  select FINAL_COST_DOZ
  from PRODUCT b
  where b.ID = a.PRODUCT_ID
)
where a.UNIT = 'DOZ';

update BAD_STOCK_RETURN_ITEM a
set a.COST = (
  select FINAL_COST_CTN
  from PRODUCT b
  where b.ID = a.PRODUCT_ID
)
where a.UNIT = 'CTN';

update BAD_STOCK_RETURN_ITEM a
set a.COST = (
  select FINAL_COST_TIE
  from PRODUCT b
  where b.ID = a.PRODUCT_ID
)
where a.UNIT = 'TIE';

update BAD_STOCK_RETURN_ITEM a
set a.COST = (
  select FINAL_COST_CSE
  from PRODUCT b
  where b.ID = a.PRODUCT_ID
)
where a.UNIT = 'CSE';
