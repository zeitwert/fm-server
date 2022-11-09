
alter table obj_account
add column logo_img_id integer references obj_document(obj_id) deferrable initially deferred;

alter table obj_account
add column banner_img_id integer references obj_document(obj_id) deferrable initially deferred;
