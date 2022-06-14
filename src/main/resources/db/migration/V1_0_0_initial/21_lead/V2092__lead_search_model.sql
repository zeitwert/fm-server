
create or replace view doc_lead_search
as
select  d.doc_id,
        'lead ' || coalesce(d.intl_key, ' ') || ' ' || coalesce(d.first_name, ' ') || ' ' || coalesce(d.last_name, ' ') || ' ' || coalesce(d.email, ' ') || ' ' || coalesce(d.street, ' ') || ' ' || coalesce(d.city, ' ') || ' ' || coalesce(d.zip, ' ') || ' ' as tokens,
        coalesce(d.subject, ' ') || ' ' || coalesce(d.description, ' ') || ' ' || coalesce(translate(d.email, '@_-.', '    '), ' ') || ' '                                                                     as text
from    doc_lead_v  d;

create or replace function copy_lead_search()
returns trigger as
$body$
declare
  ld_tokens                   text;
  ld_text                     text;
  hh_tokens                   text;
  hh_text                     text;
  ct_tokens                   text;
  ct_text                     text;
begin
	select  coalesce(max(ld.tokens), ' '),
	        coalesce(max(ld.text), ' ')
	into    ld_tokens,
	        ld_text
	from    doc_lead_search ld
	where   ld.doc_id = new.doc_id;
	select  coalesce(max(hh.tokens), ' '),
	        coalesce(max(hh.text), ' ')
	into    hh_tokens,
	        hh_text
	from    obj_account_search hh
	where   hh.obj_id = (select o.account_id from obj_contact_v o where o.id = new.contact_id);
	select  coalesce(max(ct.tokens), ' '),
	        coalesce(max(ct.text), ' ')
	into    ct_tokens,
	        ct_text
	from    obj_contact_search ct
	where   ct.obj_id = new.contact_id;
	insert into item_search(
		id,
		--
		item_type_id,
		item_id,
		--
		a_simple,
		b_german,
		b_english
	) values (
		'doc_lead:' || new.doc_id,
		--
		'doc_lead',
		new.doc_id,
		--
		ld_tokens || ct_tokens || hh_tokens,
		ld_text || ld_tokens || ct_text || ct_tokens || hh_text || hh_tokens,
		ld_text || ld_tokens || ct_text || ct_tokens || hh_text || hh_tokens
	)
	on conflict (id)
	do update
	set			a_simple  = ld_tokens || ct_tokens || hh_tokens,
					b_german  = ld_text || ld_tokens || ct_text || ct_tokens || hh_text || hh_tokens,
					b_english = ld_text || ld_tokens || ct_text || ct_tokens || hh_text || hh_tokens;
	return new;
end;
$body$
language plpgsql;

create constraint trigger doc_lead_search
after insert or update on doc_lead
deferrable initially deferred
for each row
execute procedure copy_lead_search();
