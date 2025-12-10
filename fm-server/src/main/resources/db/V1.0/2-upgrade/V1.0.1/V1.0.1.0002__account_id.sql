
alter table obj_account
add column
	account_id															integer							references obj_account(obj_id) deferrable initially deferred;

commit;

update obj_account
set account_id = obj_id;

commit;

alter table obj_account
alter column account_id set not null;
