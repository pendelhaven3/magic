alter table SALES_RETURN add POST_DT date null;
alter table SALES_RETURN add POST_BY integer null;
alter table SALES_RETURN add constraint SALES_RETURN$FK2 foreign key (POST_BY) references USER (ID);
