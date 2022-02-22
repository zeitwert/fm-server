
create or replace view migr_obj_account_v
as
select  hh.id,
        t.key as tenant,
        u.email as owner,
        hh.intl_key as key,
        hh.name,
        hh.account_type_id,
				hh.client_segment_id as client_segment,
				hh.reference_currency_id as reference_currency,
        c.intl_key as main_contact,
        hh.description
from    obj_account_v hh
join migr_obj_tenant_v t on t.id = hh.tenant_id
join migr_obj_user_v u on u.id = hh.owner_id
left join obj_contact_v c on c.id = hh.main_contact_id;

create or replace function insert_migr_obj_account_v()
returns trigger
as
$func$
declare
	new_id int;
	tenant_id int;
	owner_id int;
begin
	select id into tenant_id from obj_tenant_v where extl_key = new.tenant;
	select id into owner_id from obj_user_v where email = new.owner;
	insert into obj(id, tenant_id, obj_type_id, owner_id, created_by_user_id, caption)
	values (nextval('obj_id_seq'), tenant_id, 'obj_account', owner_id, owner_id, new.name)
	returning id
	into new_id;
	insert into obj_account(obj_id, intl_key, name, description, account_type_id, client_segment_id, reference_currency_id)
	values (new_id, new.key, new.name, new.description, new.account_type_id, new.client_segment, new.reference_currency);
	return new;
end
$func$
language plpgsql;

create trigger migr_obj_account_v$ins
instead of insert on migr_obj_account_v
for each row execute procedure insert_migr_obj_account_v();


