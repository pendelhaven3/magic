alter table PRODUCT add ACTIVE_UNIT_IND_TIE char(1) default 'N' not null;
alter table PRODUCT add ACTIVE_UNIT_IND_CTN char(1) default 'N' not null;
alter table PRODUCT add ACTIVE_UNIT_IND_DOZ char(1) default 'N' not null;
alter table PRODUCT add ACTIVE_UNIT_IND_PCS char(1) default 'N' not null;

update PRODUCT set ACTIVE_UNIT_IND_TIE = 'Y' where UNIT_IND_TIE = 'Y';
update PRODUCT set ACTIVE_UNIT_IND_CTN = 'Y' where UNIT_IND_CTN = 'Y';
update PRODUCT set ACTIVE_UNIT_IND_DOZ = 'Y' where UNIT_IND_DOZ = 'Y';
update PRODUCT set ACTIVE_UNIT_IND_PCS = 'Y' where UNIT_IND_PCS = 'Y';
