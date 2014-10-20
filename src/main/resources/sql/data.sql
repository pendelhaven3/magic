insert into SEQUENCE (NAME) values ('SALES_REQUISITION_NO_SEQ');
insert into SEQUENCE (NAME) values ('SALES_INVOICE_NO_SEQ');
insert into SEQUENCE (NAME) values ('STOCK_QTY_CONV_NO_SEQ');
insert into SEQUENCE (NAME) values ('PURCHASE_ORDER_NO_SEQ');
insert into SEQUENCE (NAME) values ('RECEIVING_RECEIPT_NO_SEQ');
insert into SEQUENCE (NAME) values ('ADJUSTMENT_OUT_NO_SEQ');
insert into SEQUENCE (NAME) values ('ADJUSTMENT_IN_NO_SEQ');

insert into USER (ID, USERNAME, PASSWORD) values (1, 'PJ', 'n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg=');
insert into USER (ID, USERNAME, PASSWORD) values (2, 'IRENE', 'n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg=');
insert into USER (ID, USERNAME, PASSWORD) values (3, 'JOY', 'n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg=');

insert into MANUFACTURER (NAME) values ('PEDIAHEALTH CORPORATION');
insert into MANUFACTURER (NAME) values ('PHARMA EAST MEDICAL CORP.');
insert into MANUFACTURER (NAME) values ('QUANTUM RUBBER PRODUCTS CORP.');
insert into MANUFACTURER (NAME) values ('NUTRAHERB INC.');
insert into MANUFACTURER (NAME) values ('ALKAPURE WATER RESOURCES INC.');
insert into MANUFACTURER (NAME) values ('CROFS CONSTRUCTION AND DEVELOPMENT CO.');
insert into MANUFACTURER (NAME) values ('FIL-SCAN EXPORTS, INC.');
insert into MANUFACTURER (NAME) values ('STARWORKS ENTERPRISE');
insert into MANUFACTURER (NAME) values ('FIRST PHILIPPINE SCALES, INC.');
insert into MANUFACTURER (NAME) values ('PHILIPPINE ADHESIVES INC.');

insert into PAYMENT_TERM (NAME, NUMBER_OF_DAYS) values ('COD', 0);
insert into PAYMENT_TERM (NAME, NUMBER_OF_DAYS) values ('7 DAYS', 7);

insert into SUPPLIER (CODE, NAME) values ('AMICI', 'AMICI WATER SYSTEMS, PHILIPPINES');
insert into SUPPLIER (CODE, NAME, ADDRESS, PAYMENT_TERM_ID) values ('ABENSON', 'ABENSON AVANT', '36 GEN. P. ALVAREZ ST., BRGY. 69, CALOOCAN CITY', 1);
insert into SUPPLIER (CODE, NAME) values ('KSERVICO', 'K SERVICO TRADE, INC.');
insert into SUPPLIER (CODE, NAME) values ('PHILGROCER', 'PHILGROCER');

insert into PRICING_SCHEME (ID, NAME) values (1, 'CANVASSER');
insert into PRICING_SCHEME (ID, NAME) values (2, 'MARKETING');