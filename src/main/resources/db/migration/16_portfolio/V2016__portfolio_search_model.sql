
create or replace view obj_portfolio_search
as
select  obj_id,
        'portfolio ' || coalesce(name, ' ') || ' ' || coalesce(portfolio_nr, ' ') || ' ' as tokens,
        coalesce(name, ' ') || ' ' || coalesce(description, ' ') || ' '          as text
from    obj_portfolio_v;

create or replace function copy_portfolio_search()
returns trigger as
$body$
declare
  pf_tokens                   text;
  pf_text                     text;
  hh_tokens                   text;
  hh_text                     text;
begin
	select  coalesce(max(pf.tokens), ' '),
	        coalesce(max(pf.text), ' ')
	into    pf_tokens,
	        pf_text
	from    obj_portfolio_search pf
	where   pf.obj_id = new.obj_id;
	select  coalesce(max(hh.tokens), ' '),
	        coalesce(max(hh.text), ' ')
	into    hh_tokens,
	        hh_text
	from    obj_account_search hh
	where   hh.obj_id = new.account_id;
	hh_tokens = coalesce(hh_tokens, ' ');
	hh_text = coalesce(hh_text, ' ');
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
		'obj_portfolio:' || new.obj_id,
		--
		'obj_portfolio',
		new.obj_id,
		--
		pf_tokens || hh_tokens,
		pf_text || pf_tokens || hh_text || hh_tokens,
		pf_text || pf_tokens || hh_text || hh_tokens
	)
	on conflict (id)
	do update
	set			a_simple  = pf_tokens || hh_tokens,
					b_german  = pf_text || pf_tokens || hh_text || hh_tokens,
					b_english = pf_text || pf_tokens || hh_text || hh_tokens;
	return new;
end;
$body$
language plpgsql;

create constraint trigger obj_portfolio_search
after insert or update on obj_portfolio
deferrable initially deferred
for each row
execute procedure copy_portfolio_search();
