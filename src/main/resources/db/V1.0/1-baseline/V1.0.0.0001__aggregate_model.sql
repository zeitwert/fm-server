
-- Create base sequences

create sequence part_id_seq;

create sequence item_part_id_seq;

create table code_aggregate_type (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table code_part_list_type (
	id																		varchar(40) 				not null,
	--
	name																	varchar(100) 				not null,
	--
	primary key (id)
);
