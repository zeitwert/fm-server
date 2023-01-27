
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
