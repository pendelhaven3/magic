alter table PROMO add PRICING_SCHEME_ID integer null;
alter table PROMO add constraint PROMO$FK foreign key (PRICING_SCHEME_ID) references PRICING_SCHEME (ID);

update PROMO a
set a.PRICING_SCHEME_ID = (select b.PRICING_SCHEME_ID from PROMO_TYPE_3_RULE b where b.PROMO_ID = a.ID)
where a.PROMO_TYPE_ID = 3;

alter table PROMO_TYPE_3_RULE drop foreign key PROMO_TYPE_3_RULE$FK3;
alter table PROMO_TYPE_3_RULE drop column PRICING_SCHEME_ID;