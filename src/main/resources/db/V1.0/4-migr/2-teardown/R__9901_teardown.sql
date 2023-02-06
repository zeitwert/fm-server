
-- execTrigger: 1

drop trigger if exists migr_obj_building_v$ins on migr_obj_building_v;
drop function if exists insert_migr_obj_building_v;
drop view if exists migr_obj_building_v;

drop trigger if exists migr_obj_account_v$ins on migr_obj_account_v;
drop function if exists insert_migr_obj_account_v;
drop view if exists migr_obj_account_v;

drop trigger if exists migr_obj_user_v$ins on migr_obj_user_v;
drop function if exists insert_migr_obj_user_v;
drop view if exists migr_obj_user_v;

drop trigger if exists migr_obj_tenant_v$ins on migr_obj_tenant_v;
drop function if exists insert_migr_obj_tenant_v;
drop view if exists migr_obj_tenant_v;

drop table if exists migr_key;
