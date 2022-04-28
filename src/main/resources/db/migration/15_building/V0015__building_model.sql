

create table code_building_type (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table code_building_sub_type (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	building_type_id											varchar(40)					not null references code_building_type(id),
	--
	primary key (id)
);

create table code_historic_preservation (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table code_building_maintenance_strategy (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table code_building_part (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	opt_restore_duration									decimal							not null,
	opt_restore_time_value								decimal							not null,
	max_restore_duration									decimal							not null,
	max_restore_time_value								decimal							not null,
	after_restore_time_value							decimal							not null,
	--
	restore_cost_perc											decimal							not null,
	new_build_cost_perc										decimal							not null,
	--
	linear_duration												decimal							not null,
	linear_time_value											decimal							not null,
	--
	c10																		decimal							not null,
	c9																		decimal							not null,
	c8																		decimal							not null,
	c7																		decimal							not null,
	c6																		decimal							not null,
	c5																		decimal							not null,
	c4																		decimal							not null,
	c3																		decimal							not null,
	c2																		decimal							not null,
	c1																		decimal							not null,
	c0																		decimal							not null,
	--
	primary key (id)
);

create table code_building_part_catalog (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	parts																	varchar(400),
	--
	primary key (id)
);

create table code_building_price_index (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table code_building_price_index_value (
	building_price_index_id								varchar(40)					not null references code_building_price_index(id),
	year																	integer							not null,
	--
	value																	decimal							not null,
	--
	primary key (building_price_index_id, year)
);

create table obj_building (
	obj_id																integer							not null references obj(id) deferrable initially deferred,
	--
	intl_key															varchar(60),
	name																	varchar(100),
	description														text,
	--
	account_id													integer							not null references obj_account(obj_id) deferrable initially deferred,
	--
	building_nr														varchar(200), -- Identifikation
	building_insurance_nr									varchar(200), -- Gebäudeversicherung Police Nr
	plot_nr																varchar(200), -- Parzellen Nr
	national_building_id									varchar(200), -- EGID (eidgenössischer Gebäudeidentifikator)
	historic_preservation_id							varchar(40)					references code_historic_preservation(id),
	--
	street																varchar(100),
	zip																		varchar(60),
	city																	varchar(60),
	country_id														varchar(40)					not null references code_country(id),
	currency_id														varchar(40)					not null references code_currency(id),
	--
	volume																decimal,
	area_gross														decimal, -- Grundfläche
	area_net															decimal, -- Hauptnutzfläche
	nr_of_floors_above_ground							integer,
	nr_of_floors_below_ground							integer,
	--
	building_type_id											varchar(40)					references code_building_type(id),
	building_sub_type_id									varchar(40)					references code_building_sub_type(id),
	building_year													integer,
	--
	insured_value													decimal,
	insured_value_year										integer,
	not_insured_value											decimal,
	not_insured_value_year								integer,
	third_party_value											decimal,
	third_party_value_year								integer,
	--
	building_part_catalog_id							varchar(40)					references code_building_part_catalog(id),
	building_maintenance_strategy_id			varchar(40)					references code_building_maintenance_strategy(id),
	--
	primary key (obj_id)
);

create or replace view obj_building_v
as
select	obj.tenant_id,
				obj.obj_type_id,
				obj.id,
				obj.owner_id,
				obj.caption,
				--
				obj.created_by_user_id,
				obj.created_at,
				obj.modified_by_user_id,
				obj.modified_at,
				obj.closed_by_user_id,
				obj.closed_at,
				--
				b.*
from		obj_building b
join obj on obj.id = b.obj_id;

create table code_building_element_description (
	id																		varchar(40)					not null,
	--
	category															varchar(100)				not null, -- material, condition, measure
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table obj_building_part_element (
	id																		integer							not null,
	obj_id																integer							not null references obj_building(obj_id) deferrable initially deferred,
	parent_part_id												integer,						-- reference to parent part (optional)
	part_list_type_id											varchar(40) default 'building.elementList' not null references code_part_list_type(id),
	seq_nr																integer,
	--
	building_part_id											varchar(40)					references code_building_part(id),
	--
	value_part														integer,
	condition															integer,
	condition_year												integer,
	strain																integer,
	strength															integer,
	--
	description														text,
	--
	primary key (id)
);
