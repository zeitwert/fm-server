
create or replace view obj_building_search
as
select  obj_id,
        account_id,
        'building ' || coalesce(name, ' ') || ' ' || coalesce(building_nr, ' ') || ' ' || coalesce(building_insurance_nr, ' ') || ' ' || coalesce(plot_nr, ' ') || ' ' || coalesce(national_building_id, ' ') || ' ' || coalesce(street, ' ') || ' ' || coalesce(city, ' ') || ' ' || coalesce(zip, ' ') || ' ' as tokens,
        coalesce(description, ' ') || ' '                                                                                                        as text
from    obj_building_v;

create or replace function copy_building_search()
returns trigger as
$body$
declare
  hh_tokens                   text;
  hh_text                     text;
  bd_tokens                   text;
  bd_text                     text;
begin
	select  coalesce(max(bd.tokens), ' '),
	        coalesce(max(bd.text), ' ')
	into    bd_tokens,
	        bd_text
	from    obj_building_search bd
	where   bd.obj_id = new.obj_id;
	select  coalesce(max(hh.tokens), ' '),
	        coalesce(max(hh.text), ' ')
	into    hh_tokens,
	        hh_text
	from    obj_account_search hh
	where   hh.obj_id = (select o.account_id from obj_building_v o where o.id = new.obj_id);
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
		'obj_building:' || new.obj_id,
		--
		'obj_building',
		new.obj_id,
		--
		bd_tokens || hh_tokens,
		bd_text || bd_tokens || hh_text || hh_tokens,
		bd_text || bd_tokens || hh_text || hh_tokens
	)
	on conflict (id)
	do update
	set			a_simple  = bd_tokens || hh_tokens,
					b_german  = bd_text || bd_tokens || hh_text || hh_tokens,
					b_english = bd_text || bd_tokens || hh_text || hh_tokens;
	return new;
end;
$body$
language plpgsql;

create constraint trigger obj_building_search
after insert or update on obj_building
deferrable initially deferred
for each row
execute procedure copy_building_search();

