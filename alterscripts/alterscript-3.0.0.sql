alter table BAD_STOCK_ADJUSTMENT_OUT add PILFERAGE boolean default false not null;
alter table BAD_STOCK_REPORT_ITEM add FORCE_CONVERSION boolean default false not null;
alter table BAD_STOCK_REPORT add column RECEIVED_DT date not null;

