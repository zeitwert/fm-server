
-- Add key column to obj_tenant
alter table obj_tenant
add column key varchar(40);

create unique index obj_tenant_key_idx on obj_tenant(key) where key is not null;

-- Add key column to obj_account
alter table obj_account
add column key varchar(40);

create unique index obj_account_key_idx on obj_account(key) where key is not null;

