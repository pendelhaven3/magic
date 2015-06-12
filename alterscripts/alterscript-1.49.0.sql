alter table PROMO_TYPE_3_RULE add PRICING_SCHEME_ID integer null;
alter table PROMO_TYPE_3_RULE add constraint PROMO_TYPE_3_RULE$FK3 foreign key (PRICING_SCHEME_ID) references PRICING_SCHEME (ID);