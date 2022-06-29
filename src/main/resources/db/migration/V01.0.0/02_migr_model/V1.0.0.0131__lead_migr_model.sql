
create or replace view migr_doc_lead_v
as
select  l.id,
        t.key as tenant,
        u.email as owner,
        l.intl_key as key,
        l.case_stage_id,
        (select string_agg(item_id, ',') from doc_part_item where doc_id = l.id and part_list_type_id = 'doc.areaSet') as areas,
        hh.intl_key as account,
        c.intl_key as contact,
        --
        l.subject,
        l.description,
        l.lead_source_id,
        l.lead_rating_id,
        l.salutation_id,
        l.title_id,
        l.first_name,
        l.last_name,
        l.phone,
        l.mobile,
        l.email,
        l.street,
        l.zip,
        l.city,
        l.state,
        l.country_id
from    doc_lead_v l
join migr_obj_tenant_v t on t.id = l.tenant_id
join migr_obj_user_v u on u.id = l.owner_id
left join obj_account_v hh on hh.id = l.account_id
left join obj_contact_v c on c.id = l.contact_id;

create or replace function insert_migr_doc_lead_v()
returns trigger
as
$func$
declare
	new_id int;
	tenant_id int;
	owner_id int;
	account_id int;
	contact_id int;
	areas varchar[];
	area varchar;
	seq_nr int;
begin
	select id into tenant_id from obj_tenant_v where extl_key = new.tenant;
	select id into owner_id from obj_user_v where email = new.owner;
	select id into account_id from obj_account_v where intl_key = new.account;
	select id into contact_id from obj_contact_v where intl_key = new.contact;
	insert into doc(id, tenant_id, doc_type_id, case_def_id, case_stage_id, account_id, owner_id, created_by_user_id, caption)
	values (nextval('doc_id_seq'), tenant_id, 'doc_lead', 'lead', new.case_stage_id, account_id, owner_id, owner_id, new.subject || ' (' || new.first_name || ' ' || new.last_name || ')')
	returning id
	into new_id;
	insert into doc_lead(
		doc_id,
		--
		intl_key,
		subject,
		description,
		--
		tenant_id,
		account_id,
		contact_id,
		--
		lead_source_id,
		lead_rating_id,
		--
		salutation_id,
		title_id,
		first_name,
		last_name,
		--
		phone,
		mobile,
		email,
		--
		street,
		zip,
		city,
		state,
		country_id
	) values (
		new_id,
		--
		new.key,
		new.subject,
		new.description,
		--
		tenant_id,
		account_id,
		contact_id,
		--
		new.lead_source_id,
		new.lead_rating_id,
		--
		new.salutation_id,
		new.title_id,
		new.first_name,
		new.last_name,
		--
		new.phone,
		new.mobile,
		new.email,
		--
		new.street,
		new.zip,
		new.city,
		new.state,
		new.country_id
	);
	if new.areas is not null then
		areas = string_to_array(new.areas, ',');
		seq_nr = 0;
		foreach area in array areas
		loop
			seq_nr = seq_nr + 1;
			insert into doc_part_item(doc_id, part_list_type_id, seq_nr, item_id)
			values (new_id, 'doc.areaSet', seq_nr, area);
		end loop;
	end if;
	return new;
end
$func$
language plpgsql;

create trigger migr_doc_lead_v$ins
instead of insert on migr_doc_lead_v
for each row execute procedure insert_migr_doc_lead_v();
