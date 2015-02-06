alter table PURCHASE_ORDER add VAT_INCLUSIVE char(1) default 'Y' not null;
alter table RECEIVING_RECEIPT add VAT_INCLUSIVE char(1) default 'Y' not null;
alter table RECEIVING_RECEIPT change VAT_INCLUSIVE VAT_INCLUSIVE char(1) not null;
alter table RECEIVING_RECEIPT add VAT_RATE numeric(4, 2) null;
update RECEIVING_RECEIPT set VAT_RATE = 0.12;
alter table RECEIVING_RECEIPT change VAT_RATE VAT_RATE numeric(4, 2) not null;
