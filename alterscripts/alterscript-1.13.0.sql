insert into ADJUSTMENT_TYPE
(CODE, DESCRIPTION)
select ADJ_TYPE, ADJ_TYPE
from (
  select distinct ADJUSTMENT_TYPE ADJ_TYPE
  from PAYMENT_ADJUSTMENT
  where ADJUSTMENT_TYPE not in ('SR', 'BSR')
) a;

alter table PAYMENT_ADJUSTMENT change ADJUSTMENT_TYPE ADJUSTMENT_TYPE varchar(20) null;
alter table PAYMENT_ADJUSTMENT add ADJUSTMENT_TYPE_ID integer null;
alter table PAYMENT_ADJUSTMENT add constraint PAYMENT_ADJUSTMENT$FK2 foreign key (ADJUSTMENT_TYPE_ID) references ADJUSTMENT_TYPE (ID);

update PAYMENT_ADJUSTMENT a
set ADJUSTMENT_TYPE_ID = (
  select ID
  from ADJUSTMENT_TYPE b
  where b.CODE = a.ADJUSTMENT_TYPE
)
where ADJUSTMENT_TYPE_ID is null;

alter table PAYMENT_ADJUSTMENT add ADJUSTMENT_TYPE_ID integer not null;