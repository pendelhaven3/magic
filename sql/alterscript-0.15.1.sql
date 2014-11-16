alter table RECEIVING_RECEIPT_ITEM add CURRENT_COST numeric(10, 2) null;
update RECEIVING_RECEIPT_ITEM set CURRENT_COST = COST; --just in case there are existing records already