alter table SALES_REQUISITION add STOCK_QUANTITY_CONVERSION_ID integer null;
alter table SALES_REQUISITION add constraint SALES_REQUISITION$FK5 foreign key (STOCK_QTY_CONVERSION_ID) references STOCK_QTY_CONVERSION (ID);
alter table STOCK_QTY_CONVERSION add CREATE_DT datetime default now() not null;
alter table STOCK_QTY_CONVERSION add PRINT_IND char(1) default 'N' not null;

update STOCK_QTY_CONVERSION set PRINT_IND = 'Y';

create table SALES_REQUISITION_EXTRACTION_WHITELIST_ITEM (
  PRODUCT_ID integer not null,
  constraint SALES_REQUISITION_EXTRACTION_WHITELIST_ITEM$FK foreign key (PRODUCT_ID) references PRODUCT (ID)
);
