insert into SEQUENCE (NAME) values ('SALES_REQUISITION_NO_SEQ');
insert into SEQUENCE (NAME) values ('SALES_INVOICE_NO_SEQ');
insert into SEQUENCE (NAME) values ('STOCK_QTY_CONV_NO_SEQ');
insert into SEQUENCE (NAME) values ('PURCHASE_ORDER_NO_SEQ');
insert into SEQUENCE (NAME) values ('RECEIVING_RECEIPT_NO_SEQ');
insert into SEQUENCE (NAME) values ('ADJUSTMENT_OUT_NO_SEQ');
insert into SEQUENCE (NAME) values ('ADJUSTMENT_IN_NO_SEQ');

insert into PRICING_SCHEME (ID, NAME) values (1, 'CANVASSER');

insert into USER (ID, USERNAME, PASSWORD, SUPERVISOR_IND) values (1, 'ADMIN', 'jGl25bVBBBW96Qi9Te4V37Fnqchz/Eu4qB9vKrRIqRg=', 'Y');

insert into PRODUCT_CATEGORY (ID, NAME) values (1, 'FOOD');
insert into PRODUCT_CATEGORY (ID, NAME) values (2, 'NON-FOOD');

insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'BAKING ITEMS');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'BEVERAGE - CARBONATED');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'BEVERAGE - MILK, CHOCOLATE & FRUIT BASE');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'BEVERAGE - POWDERED JUICE');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'BEVERAGE - READY TO DRINK JUICE');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'BEVERAGE - TEA');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'BEVERAGE - WATER');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CANNED GOODS - CORNED BEEF');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CANNED GOODS - FISH');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CANNED GOODS - FRUITS');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CANNED GOODS - LUNCHEON MEAT');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CANNED GOODS - MEAT/BEEF/CHICKEN LOAF');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CANNED GOODS - SAUSAGE');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CANNED GOODS - SQUID');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CANNED GOODS - OTHERS');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CHEESE');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CHOCOLATE DRINK');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'COFFEE');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'COFFEE CREAMER');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CONDIMENTS - CATSUP');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CONDIMENTS - CREAM');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CONDIMENTS - FISH SAUCE');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CONDIMENTS - SALT');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CONDIMENTS - SAUCE');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CONDIMENTS - SEASONING');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CONDIMENTS - SOY SAUCE');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CONDIMENTS - VINEGAR');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CONDIMENTS - OTHERS');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CONFECTIONERIES - CANDY');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CONFECTIONERIES - CHOCOLATE');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CONFECTIONERIES - GUM');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'CONFECTIONERIES - OTHERS');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'COOKING OIL');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'ENERGY DRINK');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'INSTANT MEALS - CEREALS');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'INSTANT MEALS - NOODLES IN CUP/BOWL');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'INSTANT MEALS - NOODLES IN POUCH');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'INSTANT MEALS - OTHERS');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'LIQUOR');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'LIQUOR MIX');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'MARGARINE');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'MILK - LIQUID');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'MILK - POWDER');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'NOODLES');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'NOODLES - PASTA');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'SANDWICH SPREAD');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'SNACKS - BISCUITS, CAKES, COOKIES, CRACKERS, WAFERS');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'SNACKS - CHIPS, NUTS, ETC.');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'SOUP');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (1, 'SUGAR');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'ALCOHOL');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'BABY OIL');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'BABY POWDER');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'BLADE & RAZOR');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'BODY CARE - BABY BATH');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'BODY CARE - BODY WASH');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'BODY CARE - COLOGNE');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'BODY CARE - DEODORANT');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'BODY CARE - LOTION');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'BODY CARE - SOAP');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'CANDLES');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'COTTON');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'DIAPER');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'FACIAL CARE - CLEANSER');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'FACIAL CARE - CREAM, SCRUB & WASH');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'FACIAL CARE - EXFOLIANT');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'FEMININE CARE - FEMININE WASH');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'FEMININE CARE - PANTYLINER');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'FEMININE CARE - SANITARY NAPKIN');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'FLASHLIGHT & BATTERIES');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'HAIR CARE - CONDITIONER');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'HAIR CARE - SHAMPOO');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'HAIR CARE - STYLING');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'HAIR CARE - OTHERS');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'HOUSEHOLD CLEANERS - CLEANSER');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'HOUSEHOLD CLEANERS - DISHWASHING');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'HOUSEHOLD CLEANERS - FLOORWAX');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'HOUSEHOLD CLEANERS - OTHERS');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'LAUNDRY PRODUCTS - DETERGENT BAR');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'LAUNDRY PRODUCTS - DETERGENT POWDER');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'LAUNDRY PRODUCTS - FABRIC CONDITIONER');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'LAUNDRY PRODUCTS - LIQUID DETERGENT');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'LIGHTERS & MATCHES');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'MOSQUITO COIL');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'NAIL CARE');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'ORAL CARE - MOUTHWASH');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'ORAL CARE - TOOTHBRUSH');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'ORAL CARE - TOOTHPASTE');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'OTC DRUGS - CAPSULE');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'OTC DRUGS - TABLET');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'OTC DRUGS - OTHERS');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'PAPER PRODUCTS');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'PLASTIC PRODUCTS');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'SHOE CARE');
insert into PRODUCT_SUBCATEGORY (PRODUCT_CATEGORY_ID, NAME) values (2, 'TOOTHPICK');