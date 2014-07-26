insert into PRODUCT (ID, CODE, DESCRIPTION, UNIT_IND_CSE, UNIT_IND_CTN, UNIT_IND_DOZ, UNIT_IND_PCS, AVAIL_QTY_CSE, AVAIL_QTY_CTN, AVAIL_QTY_DOZ, AVAIL_QTY_PCS) 
values (1, 'ZONRED250', 'ZONROX RED 250mlx48 ORIGINAL', 'Y', null, null, 'Y', 10, 0, 0, 10);

insert into PRODUCT (ID, CODE, DESCRIPTION, UNIT_IND_CSE, UNIT_IND_CTN, UNIT_IND_DOZ, UNIT_IND_PCS, AVAIL_QTY_CSE, AVAIL_QTY_CTN, AVAIL_QTY_DOZ, AVAIL_QTY_PCS) 
values (2, 'ZONRED500', 'ZONROX RED 500mlx36 ORIGINAL', 'Y', null, null, 'Y', 8, 0, 0, 8);

insert into PRODUCT (ID, CODE, DESCRIPTION, UNIT_IND_CSE, UNIT_IND_CTN, UNIT_IND_DOZ, UNIT_IND_PCS, AVAIL_QTY_CSE, AVAIL_QTY_CTN, AVAIL_QTY_DOZ, AVAIL_QTY_PCS)
values (3, 'ZONRE1000', 'ZONROX RED 1000mlx24 ORIGINAL', 'Y', null, null, 'Y', 5, 0, 0, 5);
 
insert into PRODUCT (ID, CODE, DESCRIPTION, UNIT_IND_CSE, UNIT_IND_CTN, UNIT_IND_DOZ, UNIT_IND_PCS, AVAIL_QTY_CSE, AVAIL_QTY_CTN, AVAIL_QTY_DOZ, AVAIL_QTY_PCS)
values (4, 'ZONPLS250', 'ZONROX PLUS 225mlx48', 'Y', null, null, 'Y', 3, 0, 0, 3);

insert into PRODUCT (ID, CODE, DESCRIPTION, UNIT_IND_CSE, UNIT_IND_CTN, UNIT_IND_DOZ, UNIT_IND_PCS, AVAIL_QTY_CSE, AVAIL_QTY_CTN, AVAIL_QTY_DOZ, AVAIL_QTY_PCS) 
values (5, 'ZONPLS500', 'ZONROX PLUS 450mlx48', 'Y', null, null, 'Y', 6, 0, 0, 6);

insert into PRODUCT (ID, CODE, DESCRIPTION, UNIT_IND_CSE, UNIT_IND_CTN, UNIT_IND_DOZ, UNIT_IND_PCS, AVAIL_QTY_CSE, AVAIL_QTY_CTN, AVAIL_QTY_DOZ, AVAIL_QTY_PCS) 
values (6, 'ZONPL1000', 'ZONROX PLUS 900mlx48', 'Y', null, null, 'Y', 2, 0, 0, 2);

--

insert into PRODUCT_PRICE (ID, PRODUCT_ID, UNIT_PRICE_CSE, UNIT_PRICE_CTN, UNIT_PRICE_DOZ, UNIT_PRICE_PCS) 
values (1, 1, 472.80, null, null, 9.85);

insert into PRODUCT_PRICE (ID, PRODUCT_ID, UNIT_PRICE_CSE, UNIT_PRICE_CTN, UNIT_PRICE_DOZ, UNIT_PRICE_PCS) 
values (2, 2, 547.20, null, null, 15.20);

insert into PRODUCT_PRICE (ID, PRODUCT_ID, UNIT_PRICE_CSE, UNIT_PRICE_CTN, UNIT_PRICE_DOZ, UNIT_PRICE_PCS) 
values (3, 3, 612.00, null, null, 25.50);

insert into PRODUCT_PRICE (ID, PRODUCT_ID, UNIT_PRICE_CSE, UNIT_PRICE_CTN, UNIT_PRICE_DOZ, UNIT_PRICE_PCS) 
values (4, 4, 660.00, null, null, 13.75);

insert into PRODUCT_PRICE (ID, PRODUCT_ID, UNIT_PRICE_CSE, UNIT_PRICE_CTN, UNIT_PRICE_DOZ, UNIT_PRICE_PCS) 
values (5, 5, 783.00, null, null, 21.75);

insert into PRODUCT_PRICE (ID, PRODUCT_ID, UNIT_PRICE_CSE, UNIT_PRICE_CTN, UNIT_PRICE_DOZ, UNIT_PRICE_PCS) 
values (6, 6, 900.00, null, null, 37.50);
