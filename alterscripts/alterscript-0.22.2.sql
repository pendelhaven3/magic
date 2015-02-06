alter table ADJUSTMENT_OUT_ITEM add UNIT_PRICE numeric(10, 2) null;

update ADJUSTMENT_OUT_ITEM a
set a.UNIT_PRICE = (
  select UNIT_PRICE_CSE 
  from PRODUCT_PRICE p 
  where p.PRICING_SCHEME_ID = 1
  and p.PRODUCT_ID = a.PRODUCT_ID
)
where exists(
  select 1
  from ADJUSTMENT_OUT ao
  where ao.ID = a.ADJUSTMENT_OUT_ID
  and ao.POST_IND = 'Y'
)
and a.UNIT = 'CSE';

update ADJUSTMENT_OUT_ITEM a
set a.UNIT_PRICE = (
  select UNIT_PRICE_TIE 
  from PRODUCT_PRICE p 
  where p.PRICING_SCHEME_ID = 1
  and p.PRODUCT_ID = a.PRODUCT_ID
)
where exists(
  select 1
  from ADJUSTMENT_OUT ao
  where ao.ID = a.ADJUSTMENT_OUT_ID
  and ao.POST_IND = 'Y'
)
and a.UNIT = 'TIE';

update ADJUSTMENT_OUT_ITEM a
set a.UNIT_PRICE = (
  select UNIT_PRICE_CTN 
  from PRODUCT_PRICE p 
  where p.PRICING_SCHEME_ID = 1
  and p.PRODUCT_ID = a.PRODUCT_ID
)
where exists(
  select 1
  from ADJUSTMENT_OUT ao
  where ao.ID = a.ADJUSTMENT_OUT_ID
  and ao.POST_IND = 'Y'
)
and a.UNIT = 'CTN';

update ADJUSTMENT_OUT_ITEM a
set a.UNIT_PRICE = (
  select UNIT_PRICE_DOZ 
  from PRODUCT_PRICE p 
  where p.PRICING_SCHEME_ID = 1
  and p.PRODUCT_ID = a.PRODUCT_ID
)
where exists(
  select 1
  from ADJUSTMENT_OUT ao
  where ao.ID = a.ADJUSTMENT_OUT_ID
  and ao.POST_IND = 'Y'
)
and a.UNIT = 'DOZ';

update ADJUSTMENT_OUT_ITEM a
set a.UNIT_PRICE = (
  select UNIT_PRICE_PCS 
  from PRODUCT_PRICE p 
  where p.PRICING_SCHEME_ID = 1
  and p.PRODUCT_ID = a.PRODUCT_ID
)
where exists(
  select 1
  from ADJUSTMENT_OUT ao
  where ao.ID = a.ADJUSTMENT_OUT_ID
  and ao.POST_IND = 'Y'
)
and a.UNIT = 'PCS';
