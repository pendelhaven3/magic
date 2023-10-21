create table PROMO_RAFFLE_TICKETS (
  ID integer auto_increment,
  PROMO_ID integer not null,
  TICKET_NUMBER integer not null,
  CUSTOMER_ID integer not null,
  constraint PROMO_RAFFLE_TICKETS$PK primary key (ID),
  constraint PROMO_RAFFLE_TICKETS$UK unique (PROMO_ID, TICKET_NUMBER),
  constraint PROMO_RAFFLE_TICKETS$FK foreign key (CUSTOMER_ID) references CUSTOMER (ID)
);

create table PROMO_RAFFLE_TICKET_CLAIMS (
  ID integer auto_increment,
  PROMO_ID integer not null,
  CUSTOMER_ID integer not null,
  TRANSACTION_DT date not null,
  CLAIM_DT datetime not null,
  PROCESSED_BY integer not null,
  NO_OF_TICKETS integer not null,
  constraint PROMO_RAFFLE_TICKET_CLAIMS$PK primary key (ID),
  constraint PROMO_RAFFLE_TICKET_CLAIMS$UK unique (PROMO_ID, CUSTOMER_ID, TRANSACTION_DT),
  constraint PROMO_RAFFLE_TICKET_CLAIMS$FK foreign key (CUSTOMER_ID) references CUSTOMER (ID),
  constraint PROMO_RAFFLE_TICKET_CLAIMS$FK2 foreign key (PROCESSED_BY) references USER (ID)
);

create table PROMO_RAFFLE_TICKET_CLAIM_TICKETS (
  CLAIM_ID integer not null,
  TICKET_ID integer not null,
  constraint PROMO_RAFFLE_TICKET_CLAIM_TICKETS$UK unique (CLAIM_ID, TICKET_ID),
  constraint PROMO_RAFFLE_TICKET_CLAIM_TICKETS$FK foreign key (CLAIM_ID) references PROMO_RAFFLE_TICKET_CLAIMS (ID),
  constraint PROMO_RAFFLE_TICKET_CLAIM_TICKETS$FK2 foreign key (TICKET_ID) references PROMO_RAFFLE_TICKETS (ID)
);

create table PROMO_RAFFLE_TICKET_CLAIM_SALES_INVOICES (
  CLAIM_ID integer not null,
  SALES_INVOICE_ID integer not null,
  constraint PROMO_RAFFLE_TICKET_CLAIM_SALES_INVOICES$UK unique (CLAIM_ID, SALES_INVOICE_ID),
  constraint PROMO_RAFFLE_TICKET_CLAIM_SALES_INVOICES$FK foreign key (CLAIM_ID) references PROMO_RAFFLE_TICKET_CLAIMS (ID),
  constraint PROMO_RAFFLE_TICKET_CLAIM_SALES_INVOICES$FK2 foreign key (SALES_INVOICE_ID) references SALES_INVOICE (ID)
);

create table PROMO_RAFFLE_TICKET_CLAIM_SALES_RETURNS (
  CLAIM_ID integer not null,
  SALES_RETURN_ID integer not null,
  constraint PROMO_RAFFLE_TICKET_CLAIM_SALES_RETURNS$UK unique (CLAIM_ID, SALES_RETURN_ID),
  constraint PROMO_RAFFLE_TICKET_CLAIM_SALES_RETURNS$FK foreign key (CLAIM_ID) references PROMO_RAFFLE_TICKET_CLAIMS (ID),
  constraint PROMO_RAFFLE_TICKET_CLAIM_SALES_RETURNS$FK2 foreign key (SALES_RETURN_ID) references SALES_RETURN (ID)
);
