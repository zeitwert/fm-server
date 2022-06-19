
create or replace view migr_obj_building_v
as
select	b.id,
				t.key as tenant,
				u.email as owner,
				hh.key as account,
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
join migr_obj_tenant_v t on t.id = b.tenant_id
join migr_obj_user_v u on u.id = b.owner_id
join migr_obj_account_v hh on hh.id = b.account_id;

create or replace function insert_migr_obj_building_v()
returns trigger
as
$func$
declare
	new_id int;
	tnt_id int;
	owner_id int;
	account_id int;
	hh_name varchar(200);
	cover_foto_id int;
begin
	select id into tnt_id from obj_tenant_v where extl_key = new.tenant;
	select id into owner_id from obj_user_v where email = new.owner;
	select a.id, a.name into account_id, hh_name from obj_account_v a where a.intl_key = new.account and a.tenant_id = tnt_id;
	select nextval('obj_id_seq') into cover_foto_id;
	insert into obj(id, tenant_id, obj_type_id, owner_id, created_by_user_id)
	values (cover_foto_id, tnt_id, 'obj_document', owner_id, owner_id);
	insert into obj_document(obj_id, document_kind_id, content_kind_id, name)
	values (cover_foto_id, 'standalone', 'foto', 'Coverfoto');
	insert into obj(id, tenant_id, obj_type_id, owner_id, created_by_user_id, caption)
	values (nextval('obj_id_seq'), tnt_id, 'obj_building', owner_id, owner_id, new.name || ' (' || hh_name || ')')
	returning id
	into new_id;
	insert into obj_building(
		obj_id,
		--
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
		building_part_catalog_id,
		building_maintenance_strategy_id,
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
