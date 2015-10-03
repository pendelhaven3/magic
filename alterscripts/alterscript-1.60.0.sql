alter table PAYMENT modify CREATE_DT datetime not null;
alter table PAYMENT modify CANCEL_DT datetime null;

alter table PAYMENT add CASH_AMOUNT_GIVEN numeric(10, 2) null;