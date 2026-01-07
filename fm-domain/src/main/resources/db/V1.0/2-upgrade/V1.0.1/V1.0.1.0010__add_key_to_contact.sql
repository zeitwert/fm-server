
-- Add key column to obj_contact
alter table obj_contact
add column key varchar(40);

create unique index obj_contact_key_idx on obj_contact(key) where key is not null;


