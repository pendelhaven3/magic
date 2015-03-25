alter table PROMO add START_DT date null;
update PROMO set START_DT = '2015-02-23';
alter table PROMO add START_DT date not null;

update SYSTEM_PARAMETER set VALUE = '1.44.0';