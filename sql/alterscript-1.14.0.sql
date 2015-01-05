alter table SUPPLIER add VAT_INCLUSIVE default 'Y' char(1) not null;
alter table SUPPLIER change VAT_INCLUSIVE VAT_INCLUSIVE char(1) not null;
