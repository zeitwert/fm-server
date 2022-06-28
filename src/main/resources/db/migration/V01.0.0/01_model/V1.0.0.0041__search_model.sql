
-- -----------------------------------------------------------------------------
-- SEARCH TABLE
-- -----------------------------------------------------------------------------

create table item_search (
	id																		varchar(40)					not null,
	--
	item_type_id													varchar(40)					not null references code_aggregate_type(id),
	item_id																integer							not null,
	--
	a_simple															text,
	b_german															text,
	b_english															text,
	--
	search_key														tsvector
																				generated always as (
																					(
																						coalesce(setweight(to_tsvector('simple', a_simple), 'A'), ' ') || ' ' || 
																						coalesce(setweight(to_tsvector('german', b_german || ' '), 'B'), ' ') || ' ' ||
																						coalesce(setweight(to_tsvector('english', b_english || ' '), 'B'), ' ')
																					)::tsvector
																				) stored,
	--
	primary key (id)
);

create index item_search_key
on item_search
using gin(search_key);


create or replace function item_search_lower()
returns trigger language plpgsql as $$
begin
    new.a_simple := lower(new.a_simple);
    new.b_german := lower(new.b_german);
    new.b_english := lower(new.b_english);
    return new;
end $$;

create trigger item_search_lower
before insert or update on item_search
for each row
execute procedure item_search_lower();
