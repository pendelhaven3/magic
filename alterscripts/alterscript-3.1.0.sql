alter table PURCHASE_RETURN_BAD_STOCK add PAID_IND char(1) null;
alter table PURCHASE_RETURN_BAD_STOCK add PAID_DT datetime null;
alter table PURCHASE_RETURN_BAD_STOCK add PAID_BY integer null;

update PURCHASE_RETURN_BAD_STOCK set PAID_IND = POST_IND;
update PURCHASE_RETURN_BAD_STOCK set PAID_DT = now(), PAID_BY = 1 where PAID_IND = 'Y';

alter table PURCHASE_RETURN_BAD_STOCK modify column PAID_IND char(1) default 'N' not null;
