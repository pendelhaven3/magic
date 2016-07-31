alter table PROMO_REDEMPTION add CANCEL_IND char(1) default 'N' not null;
alter table PROMO_REDEMPTION add CANCEL_DT datetime null;
alter table PROMO_REDEMPTION add CANCEL_BY integer null;
alter table PROMO_REDEMPTION add constraint PROMO_REDEMPTION$FK4 foreign key (CANCEL_BY) references USER (ID);