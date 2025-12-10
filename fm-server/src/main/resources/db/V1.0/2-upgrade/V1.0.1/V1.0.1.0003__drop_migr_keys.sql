
drop view if exists obj_tenant_v;
alter table obj_tenant
drop column extl_key cascade;

drop view if exists obj_account_v;
alter table obj_account
drop column intl_key;

drop view if exists obj_contact_v;
alter table obj_contact
drop column intl_key;

drop view if exists obj_building_v;
alter table obj_building
drop column intl_key;

drop view if exists obj_portfolio_v;
alter table obj_portfolio
drop column intl_key;

