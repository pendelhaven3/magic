alter table SUPP_PAYMENT_PAYMNT_ADJ drop column ADJUSTMENT_TYPE_ID;
alter table SUPP_PAYMENT_PAYMNT_ADJ change PURCHASE_PAYMENT_ADJ_TYPE_ID PURCHASE_PAYMENT_ADJ_TYPE_ID integer not null;
alter table SUPP_PAYMENT_PAYMNT_ADJ change REFERENCE_NO REFERENCE_NO varchar(30) not null;

rename table SUPPLIER_PAYMENT to PURCHASE_PAYMENT;