
alter table obj_user
add column avatar_img_id integer references obj_document(obj_id) deferrable initially deferred;

-- dont do this now, too many dependencies
-- alter table obj_user
-- drop column picture;
