insert into SEQUENCE (NAME) values ('SALES_REQUISITION_NO_SEQ');
insert into SEQUENCE (NAME) values ('SALES_INVOICE_NO_SEQ');
insert into SEQUENCE (NAME) values ('STOCK_QTY_CONV_NO_SEQ');
insert into SEQUENCE (NAME) values ('PURCHASE_ORDER_NO_SEQ');
insert into SEQUENCE (NAME) values ('RECEIVING_RECEIPT_NO_SEQ');
insert into SEQUENCE (NAME) values ('ADJUSTMENT_OUT_NO_SEQ');
insert into SEQUENCE (NAME) values ('ADJUSTMENT_IN_NO_SEQ');

insert into SYSTEM_PARAMETER (NAME, VALUE) values ('VERSION', '0.17.0');

insert into PRICING_SCHEME (ID, NAME) values (1, 'CANVASSER');

insert into USER (ID, USERNAME, PASSWORD, SUPERVISOR_IND) values (1, 'TEST', 'jGl25bVBBBW96Qi9Te4V37Fnqchz/Eu4qB9vKrRIqRg=', 'Y');