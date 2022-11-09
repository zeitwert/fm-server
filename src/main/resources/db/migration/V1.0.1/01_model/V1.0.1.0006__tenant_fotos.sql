
alter table obj_tenant
add column logo_img_id integer references obj_document(obj_id) deferrable initially deferred;

alter table obj_tenant
add column banner_img_id integer references obj_document(obj_id) deferrable initially deferred;
