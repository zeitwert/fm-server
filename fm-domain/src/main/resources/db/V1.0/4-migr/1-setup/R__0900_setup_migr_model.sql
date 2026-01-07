
drop table if exists migr_key;

create table migr_key
(
  obj_id           integer     not null references obj(id) deferrable initially deferred,
  obj_type_id      varchar(60) not null,
  tenant_id        integer     not null references obj(id) deferrable initially deferred,
  key              varchar(60) not null,
  primary key (obj_id)
);


---- TENANT ----

drop trigger if exists migr_obj_tenant_v$ins on migr_obj_tenant_v;
drop function if exists insert_migr_obj_tenant_v;
drop view if exists migr_obj_tenant_v;

create or replace view migr_obj_tenant_v
as
select  t.obj_id as id,
        t.tenant_type_id,
        mk.key,
        t.name
from    obj_tenant t
join migr_key mk on mk.obj_id = t.obj_id;

create or replace function insert_migr_obj_tenant_v()
returns trigger
as
$func$
declare
	k_user_id int;
	tenant_id int;
begin
	select max(id) into tenant_id from obj_tenant_v where tenant_type_id = new.tenant_type_id and name = new.name;
	if tenant_id is not null then
		return null;
	end if;
	select nextval('obj_id_seq') into tenant_id;
	select id into k_user_id from obj_user_v where email = 'k@zeitwert.io';
	insert into migr_key(obj_id, obj_type_id, tenant_id, key)
	values (tenant_id, 'tenant', tenant_id, new.key);
	insert into obj_tenant(obj_id, tenant_type_id, key, name, tenant_id)
	values (tenant_id, new.tenant_type_id, new.key, new.name, tenant_id);
	insert into obj(id, tenant_id, obj_type_id, caption, owner_id, created_by_user_id)
	values (tenant_id, tenant_id, 'obj_tenant', new.name, k_user_id, k_user_id);
	return new;
end
$func$
language plpgsql;

create trigger migr_obj_tenant_v$ins
instead of insert on migr_obj_tenant_v
for each row execute procedure insert_migr_obj_tenant_v();


---- USER ----

drop trigger if exists migr_obj_user_v$ins on migr_obj_user_v;
drop function if exists insert_migr_obj_user_v;
drop view if exists migr_obj_user_v;

create or replace view migr_obj_user_v
as
select  u.obj_id as id,
        t.key as tenant,
        u.name,
        u.email,
        u.password,
        u.role_list
from    obj_user u
join migr_key t on t.obj_id = u.tenant_id;

create or replace function insert_migr_obj_user_v()
returns trigger
as
$func$
declare
	user_id int;
	new_id int;
	tenant_id int;
	k_user_id int;
begin
	select max(id) into user_id from obj_user_v where email = new.email;
	if user_id is not null then
		update	obj
		set			caption = new.name
		where		id = user_id;
		update	obj_user
		set			name = new.name,
						password = new.password,
						role_list = new.role_list
		where		obj_id = user_id;
		return new;
	end if;
	select nextval('obj_id_seq') into user_id;
	select id into k_user_id from obj_user_v where email = 'k@zeitwert.io';
	select obj_id into tenant_id from migr_key where obj_type_id = 'tenant' and key = new.tenant;
	insert into obj(id, tenant_id, obj_type_id, caption, owner_id, created_by_user_id)
	values (user_id, tenant_id, 'obj_user', new.name, k_user_id, k_user_id);
	insert into obj_user(obj_id, tenant_id, email, name, password, role_list)
	values (user_id, tenant_id, new.email, new.name, new.password, new.role_list);
	return new;
end
$func$
language plpgsql;

create trigger migr_obj_user_v$ins
instead of insert on migr_obj_user_v
for each row execute procedure insert_migr_obj_user_v();


---- ACCOUNT ----

drop trigger if exists migr_obj_account_v$ins on migr_obj_account_v;
drop function if exists insert_migr_obj_account_v;
drop view if exists migr_obj_account_v;

create or replace view migr_obj_account_v
as
select  a.id,
        t.key as tenant,
        u.email as owner,
        mk.key,
        a.name,
        a.account_type_id,
        a.client_segment_id as client_segment,
        a.reference_currency_id as reference_currency,
        a.description,
        null as main_contact
from    obj_account_v a
join migr_key mk on mk.obj_id = a.obj_id
join migr_key t on t.obj_id = a.tenant_id
join obj_user_v u on u.id = a.owner_id;

create or replace function insert_migr_obj_account_v()
returns trigger
as
$func$
declare
	account_id int;
	new_id int;
	tnt_id int;
	owner_id int;
begin
	select obj_id into tnt_id from migr_key where obj_type_id = 'tenant' and key = new.tenant;
	select max(a.id) into account_id from obj_account_v a where a.name = new.name and a.tenant_id = tnt_id;
	if account_id is not null then
		return null;
	end if;
	select nextval('obj_id_seq') into new_id;
	select id into owner_id from obj_user_v where email = new.owner;
	insert into migr_key(obj_id, obj_type_id, tenant_id, key)
	values (new_id, 'account', tnt_id, new.key);
	insert into obj(id, tenant_id, account_id, obj_type_id, owner_id, created_by_user_id, caption)
	values (new_id, tnt_id, new_id, 'obj_account', owner_id, owner_id, new.name);
	insert into obj_account(obj_id, tenant_id, account_id, key, name, description, account_type_id, client_segment_id, reference_currency_id)
	values (new_id, tnt_id, new_id, new.key, new.name, new.description, new.account_type_id, new.client_segment, new.reference_currency);
	return new;
end
$func$
language plpgsql;

create trigger migr_obj_account_v$ins
instead of insert on migr_obj_account_v
for each row execute procedure insert_migr_obj_account_v();


---- BUILDING ----

drop trigger if exists migr_obj_building_v$ins on migr_obj_building_v;
drop function if exists insert_migr_obj_building_v;
drop view if exists migr_obj_building_v;

create or replace view migr_obj_building_v
as
select	b.id,
				t.key as tenant,
				u.email as owner,
				a.key as account,
				b.name,
				--
				b.building_nr,
				--
				b.building_type_id,
				b.building_sub_type_id,
				b.building_year,
				--
				b.street,
				b.zip,
				b.city,
				b.country_id,
				--
				b.currency_id,
				--
				b.volume,
				b.area_gross,
				--
				b.insured_value,
				b.insured_value_year,
				b.not_insured_value,
				b.not_insured_value_year,
				b.third_party_value,
				b.third_party_value_year,
				--
				null building_part_catalog_id,
				null building_maintenance_strategy_id,
				--
				b.description
from		obj_building_v b
join migr_key t on t.obj_id = b.tenant_id
join migr_key a on a.obj_id = b.account_id
join obj_user_v u on u.id = b.owner_id;

create or replace function insert_migr_obj_building_v()
returns trigger
as
$func$
declare
	new_id int;
	tnt_id int;
	owner_id int;
	account_id int;
	cover_foto_id int;
begin
	select obj_id into tnt_id from migr_key where obj_type_id = 'tenant' and key = new.tenant;
	select id into owner_id from obj_user_v where email = new.owner;
	select obj_id into account_id from migr_key where obj_type_id = 'account' and key = new.account and tenant_id = tnt_id;
	select nextval('obj_id_seq') into cover_foto_id;
	insert into obj(id, tenant_id, obj_type_id, owner_id, created_by_user_id)
	values (cover_foto_id, tnt_id, 'obj_document', owner_id, owner_id);
	insert into obj_document(obj_id, tenant_id, document_kind_id, content_kind_id, name)
	values (cover_foto_id, tnt_id, 'standalone', 'foto', 'Coverfoto');
	select nextval('obj_id_seq') into new_id;
	insert into obj(id, tenant_id, account_id, obj_type_id, owner_id, created_by_user_id, caption)
	values (new_id, tnt_id, account_id, 'obj_building', owner_id, owner_id, new.name);
	insert into obj_building(
		obj_id,
		--
		tenant_id,
		account_id,
		--
		name,
		building_nr,
		--
		building_type_id,
		building_sub_type_id,
		building_year,
		--
		street,
		zip,
		city,
		country_id,
		--
		cover_foto_id,
		--
		currency_id,
		--
		volume,
		area_gross,
		--
		insured_value,
		insured_value_year,
		not_insured_value,
		not_insured_value_year,
		third_party_value,
		third_party_value_year,
		--
		description
	) values (
		new_id,
		--
		tnt_id,
		account_id,
		--
		new.name,
		new.building_nr,
		--
		new.building_type_id,
		new.building_sub_type_id,
		new.building_year,
		--
		new.street,
		new.zip,
		new.city,
		new.country_id,
		--
		cover_foto_id,
		--
		new.currency_id,
		--
		new.volume,
		new.area_gross,
		--
		new.insured_value,
		new.insured_value_year,
		new.not_insured_value,
		new.not_insured_value_year,
		new.third_party_value,
		new.third_party_value_year,
		--
		replace(replace(new.description, '\n', chr(10)), '<br>', chr(10))
	);
	insert into obj_building_part_rating(
		id,
		obj_id,
		part_list_type_id,
		seq_nr,
		--
		part_catalog_id,
		maintenance_strategy_id,
		--
		rating_status_id,
		rating_date
	) values (
		nextval('obj_part_id_seq'),
		new_id,
		'building.ratingList',
		0,
		--
		new.building_part_catalog_id,
		new.building_maintenance_strategy_id,
		--
		'done',
		current_date -- will be overwritten in building_data_03
	);
	return new;
end
$func$
language plpgsql;

create trigger migr_obj_building_v$ins
instead of insert on migr_obj_building_v
for each row execute procedure insert_migr_obj_building_v();
