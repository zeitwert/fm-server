
create or replace view obj_account_search
as
select  obj_id,
        'account ' || coalesce(name, ' ') || ' ' || coalesce(intl_key, ' ') || ' ' as tokens,
        coalesce(description, ' ') || ' '                                            as text
from    obj_account_v;

create or replace view obj_contact_search
as
select  obj_id,
        account_id,
        'contact ' || coalesce(first_name, ' ') || ' ' || coalesce(last_name, ' ') || ' ' || coalesce(email, ' ') || ' ' ||  ' ' as tokens,
        coalesce(translate(email, '@_-.', '    '), ' ') || ' ' as text
from    obj_contact_v;

create or replace function copy_contact_search()
returns trigger as
$body$
declare
  hh_tokens                   text;
  hh_text                     text;
  ct_tokens                   text;
  ct_text                     text;
begin
	select  coalesce(max(ct.tokens), ' '),
	        coalesce(max(ct.text), ' ')
	into    ct_tokens,
	        ct_text
	from    obj_contact_search ct
	where   ct.obj_id = new.obj_id;
	select  coalesce(max(hh.tokens), ' '),
	        coalesce(max(hh.text), ' ')
	into    hh_tokens,
	        hh_text
	from    obj_account_search hh
	where   hh.obj_id = (select o.account_id from obj_contact_v o where o.id = new.obj_id);
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
		'obj_contact:' || new.obj_id,
		--
		'obj_contact',
		new.obj_id,
		--
		ct_tokens || hh_tokens,
		ct_text || ct_tokens || hh_text || hh_tokens,
		ct_text || ct_tokens || hh_text || hh_tokens
	)
	on conflict (id)
	do update
	set			a_simple  = ct_tokens || hh_tokens,
					b_german  = ct_text || ct_tokens || hh_text || hh_tokens,
					b_english = ct_text || ct_tokens || hh_text || hh_tokens;
	return new;
end;
$body$
language plpgsql;

create constraint trigger obj_contact_search
after insert or update on obj_contact
deferrable initially deferred
for each row
execute procedure copy_contact_search();


create or replace function copy_account_search()
returns trigger as
$body$
declare
  hh_tokens                   text;
  hh_text                     text;
  ct_tokens                   text;
  ct_text                     text;
begin
	select  coalesce(max(hh.tokens), ' '),
	        coalesce(max(hh.text), ' ')
	into    hh_tokens,
	        hh_text
	from    obj_account_search hh
	where   hh.obj_id = new.obj_id;
	select  coalesce(string_agg(ct.tokens, ' '), ' '),
	        coalesce(string_agg(ct.text, ' '), ' ')
	into    ct_tokens,
	        ct_text
	from    obj_contact_search ct
	where   ct.account_id = new.obj_id
	group by ct.account_id;
	ct_tokens = coalesce(ct_tokens, ' ');
	ct_text = coalesce(ct_text, ' ');
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
		'obj_account:' || new.obj_id,
		--
		'obj_account',
		new.obj_id,
		--
		ct_tokens || hh_tokens,
		ct_text || ct_tokens || hh_text || hh_tokens,
		ct_text || ct_tokens || hh_text || hh_tokens
	)
	on conflict (id)
	do update
	set			a_simple  = ct_tokens || hh_tokens,
					b_german  = ct_text || ct_tokens || hh_text || hh_tokens,
					b_english = ct_text || ct_tokens || hh_text || hh_tokens;
	return new;
end;
$body$
language plpgsql;

create constraint trigger obj_account_search
after insert or update on obj_account
deferrable initially deferred
for each row
execute procedure copy_account_search();
