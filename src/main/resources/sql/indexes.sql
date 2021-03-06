create index ADJUSTMENT_IN$IDX on ADJUSTMENT_IN (POST_IND);
create index ADJUSTMENT_IN$IDX2 on ADJUSTMENT_IN (POST_DT);

create index ADJUSTMENT_OUT$IDX on ADJUSTMENT_OUT (POST_IND);
create index ADJUSTMENT_OUT$IDX2 on ADJUSTMENT_OUT (POST_DT);

create index AREA_INV_REPORT$IDX on AREA_INV_REPORT (REVIEW_IND);
create index AREA_INV_REPORT_ITEM$IDX on AREA_INV_REPORT_ITEM (UNIT);

create index BAD_STOCK_RETURN$IDX on BAD_STOCK_RETURN (POST_IND);
create index BAD_STOCK_RETURN$IDX2 on BAD_STOCK_RETURN (PAID_IND);
create index BAD_STOCK_RETURN$IDX3 on BAD_STOCK_RETURN (POST_DT);
create index BAD_STOCK_RETURN$IDX4 on BAD_STOCK_RETURN (PAID_DT);

create index CUSTOMER$IDX on CUSTOMER (NAME);
create index CUSTOMER$IDX2 on CUSTOMER (ACTIVE_IND);

create index INVENTORY_CHECK$IDX on INVENTORY_CHECK (POST_IND);

create index NO_MORE_STOCK_ADJUSTMENT$IDX on NO_MORE_STOCK_ADJUSTMENT (POST_IND);
create index NO_MORE_STOCK_ADJUSTMENT$IDX2 on NO_MORE_STOCK_ADJUSTMENT (POST_DT);
create index NO_MORE_STOCK_ADJUSTMENT$IDX3 on NO_MORE_STOCK_ADJUSTMENT (PAID_IND);
create index NO_MORE_STOCK_ADJUSTMENT$IDX4 on NO_MORE_STOCK_ADJUSTMENT (PAID_DT);

create index PAYMENT_ADJUSTMENT$IDX on PAYMENT_ADJUSTMENT (POST_IND);
create index PAYMENT_ADJUSTMENT$IDX2 on PAYMENT_ADJUSTMENT (POST_DT);
create index PAYMENT_ADJUSTMENT$IDX3 on PAYMENT_ADJUSTMENT (PAID_IND);
create index PAYMENT_ADJUSTMENT$IDX4 on PAYMENT_ADJUSTMENT (PAID_DT);

create index PAYMENT$IDX on PAYMENT (POST_IND);
create index PAYMENT$IDX2 on PAYMENT (CANCEL_IND);
create index PAYMENT$IDX3 on PAYMENT (POST_DT);

create index PAYMENT_CHECK_PAYMENT$IDX on PAYMENT_CHECK_PAYMENT (CHECK_DT);

create index PRODUCT$IDX on PRODUCT (UNIT_IND_CSE);
create index PRODUCT$IDX2 on PRODUCT (UNIT_IND_TIE);
create index PRODUCT$IDX3 on PRODUCT (UNIT_IND_CTN);
create index PRODUCT$IDX4 on PRODUCT (UNIT_IND_DOZ);
create index PRODUCT$IDX5 on PRODUCT (UNIT_IND_PCS);
create index PRODUCT$IDX6 on PRODUCT (ACTIVE_IND);

create index PRODUCT_PRICE_HISTORY$IDX on PRODUCT_PRICE_HISTORY (UPDATE_DT);

create index PROMO$IDX on PROMO (ACTIVE_IND);
create index PROMO$IDX2 on PROMO (PROMO_TYPE_ID);

create index PROMO_REDEMPTION$IDX on PROMO_REDEMPTION (POST_IND);

create index PURCHASE_ORDER$IDX on PURCHASE_ORDER (POST_IND);

create index PURCHASE_PAYMENT$IDX on PURCHASE_PAYMENT (POST_IND);
create index PURCHASE_PAYMENT$IDX2 on PURCHASE_PAYMENT (CANCEL_IND);

create index PURCHASE_PAYMENT_ADJUSTMENT$IDX on PURCHASE_PAYMENT_ADJUSTMENT (POST_IND);
create index PURCHASE_PAYMENT_ADJUSTMENT$IDX2 on PURCHASE_PAYMENT_ADJUSTMENT (POST_DT);

create index PURCHASE_PAYMENT_BANK_TRANSFER$IDX on PURCHASE_PAYMENT_BANK_TRANSFER (TRANSFER_DT);

create index PURCHASE_PAYMENT_CASH_PAYMENT$IDX on PURCHASE_PAYMENT_CASH_PAYMENT (PAID_DT);

create index PURCHASE_PAYMENT_CHECK_PAYMENT$IDX on PURCHASE_PAYMENT_CHECK_PAYMENT (CHECK_DT);

create index PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT$IDX on PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT (TRANSACTION_DT);

create index PURCHASE_RETURN$IDX on PURCHASE_RETURN (POST_IND);
create index PURCHASE_RETURN$IDX2 on PURCHASE_RETURN (POST_DT);
create index PURCHASE_RETURN$IDX3 on PURCHASE_RETURN (PAID_IND);

create index PURCHASE_RETURN_BAD_STOCK$IDX on PURCHASE_RETURN_BAD_STOCK (POST_IND);
create index PURCHASE_RETURN_BAD_STOCK$IDX2 on PURCHASE_RETURN_BAD_STOCK (POST_DT);

create index RECEIVING_RECEIPT$IDX on RECEIVING_RECEIPT (POST_IND);
create index RECEIVING_RECEIPT$IDX2 on RECEIVING_RECEIPT (CANCEL_IND);
create index RECEIVING_RECEIPT$IDX3 on RECEIVING_RECEIPT (RECEIVED_DT);

create index RECEIVING_RECEIPT_ITEM$IDX on RECEIVING_RECEIPT_ITEM (UNIT);

create index SALES_INVOICE$IDX on SALES_INVOICE (MARK_IND);
create index SALES_INVOICE$IDX2 on SALES_INVOICE (CANCEL_IND);
create index SALES_INVOICE$IDX3 on SALES_INVOICE (TRANSACTION_DT);

create index SALES_REQUISITION$IDX on SALES_REQUISITION (POST_IND);

create index SALES_RETURN$IDX on SALES_RETURN (POST_IND);
create index SALES_RETURN$IDX2 on SALES_RETURN (POST_DT);
create index SALES_RETURN$IDX3 on SALES_RETURN (PAID_IND);
create index SALES_RETURN$IDX4 on SALES_RETURN (PAID_DT);

create index STOCK_QTY_CONVERSION$IDX on STOCK_QTY_CONVERSION (POST_IND);
create index STOCK_QTY_CONVERSION$IDX2 on STOCK_QTY_CONVERSION (POST_DT);
