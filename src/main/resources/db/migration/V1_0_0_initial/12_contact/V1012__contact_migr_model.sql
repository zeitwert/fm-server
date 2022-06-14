
create or replace view migr_obj_contact_v
as
select  c.id,
        t.key as tenant,
        u.email as owner,
        hh.key as account,
        c.intl_key as key,
        (select count(*) > 0 from obj_account hh where hh.obj_id = c.account_id and hh.main_contact_id = c.id) is_main,
        c.contact_role_id,
        c.salutation_id,
        c.title_id,
        c.first_name,
        c.last_name,
        to_char(c.birth_date, 'dd.mm.yyyy') birth_date,
        c.phone,
        c.mobile,
        c.email,
        c.description
from    obj_contact_v c
join migr_obj_tenant_v t on t.id = c.tenant_id
join migr_obj_user_v u on u.id = c.owner_id
join migr_obj_account_v hh on hh.id = c.account_id;

create or replace function insert_migr_obj_contact_v()
returns trigger
as
$func$
declare
	new_id int;
	new_part_id int;
	tenant_id int;
	owner_id int;
	account_id int;
begin
	select id into tenant_id from obj_tenant_v where extl_key = new.tenant;
	select id into owner_id from obj_user_v where email = new.owner;
	select id into account_id from obj_account_v where intl_key = new.account;
	insert into obj(id, tenant_id, obj_type_id, owner_id, created_by_user_id, caption)
	values (nextval('obj_id_seq'), tenant_id, 'obj_contact', owner_id, owner_id, new.first_name || ' ' || new.last_name)
	returning id
	into new_id;
	insert into obj_contact(
		obj_id,
		--
		intl_key,
		--
		account_id,
		--
		contact_role_id,
		salutation_id,
		title_id,
		first_name,
		last_name,
		birth_date,
		--
		phone,
		mobile,
		email,
		--
		description
	) values (
		new_id,
		--
		new.key,
		--
		account_id,
		--
		new.contact_role_id,
		new.salutation_id,
		new.title_id,
		new.first_name,
		new.last_name,
		to_date(new.birth_date, 'dd.mm.yyyy'),
		--
		new.phone,
		new.mobile,
		new.email,
		--
		new.description
	);
	if new.is_main then
		update obj_account
		set    main_contact_id = new_id
		where  obj_id = account_id;
	end if;
	return new;
end
$func$
language plpgsql;


create or replace view migr_obj_contact_part_address_v
as
select  id,
				obj_id,
				address_channel_id,
				name,
				street,
				zip,
				city,
				country_id
from    obj_contact_part_address;


create or replace function insert_migr_obj_contact_part_address_v()
returns trigger
as
$func$
declare
	new_id int;
begin
	select obj_id into new_id from obj_contact where intl_key = new.key;
	insert into obj_contact_part_address(
		id,
		obj_id,
		address_channel_id,
		--
		name,
		--
		street,
		zip,
		city,
		country_id
	) values (
		nextval('obj_part_id_seq'),
		new_id,
		new.address_channel_id,
		--
		new.name,
		--
		new.street,
		new.zip,
		new.city,
		new.country_id
	);
	return new;
end
$func$
language plpgsql;


create trigger migr_obj_contact_part_address_v$ins
instead of insert on migr_obj_contact_part_address_v
for each row execute procedure insert_migr_obj_contact_part_address_v();
