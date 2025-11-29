alter table PROMO_TYPE_6_RULE_PROMO_PRODUCT add column UNIT char(3);

update PROMO_TYPE_6_RULE_PROMO_PRODUCT set UNIT = 'CSE'

--alter table PROMO_TYPE_6_RULE_PROMO_PRODUCT modify column UNIT char(3) not null;


