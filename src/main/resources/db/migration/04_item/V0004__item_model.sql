
create sequence item_part_id_seq;

create or replace view item
as
select
	tenant_id,
	obj_type_id as item_type_id,
	id,
	owner_id,
	--
	caption,
	--
	created_by_user_id,
	created_at,
	modified_by_user_id,
	modified_at
from obj
union
select
	tenant_id,
	doc_type_id as item_type_id,
	id,
	owner_id,
	--
	caption,
	--
	created_by_user_id,
	created_at,
	modified_by_user_id,
	modified_at
from doc;

create table item_part_talking_point (
	id																		integer												not null,
	item_id																integer												not null, -- references item(id) deferrable initially deferred,
	parent_part_id												integer,
	part_list_type_id											varchar(40)										not null references code_part_list_type(id),
	seq_nr																integer												not null,
	--
	page_nr																integer,
	talking_points												text													not null,
	--
	primary key (id)
);

create table item_part_note (
	id																		integer												not null,
	item_id																integer												not null, -- references item(id) deferrable initially deferred,
	parent_part_id												integer,
	part_list_type_id											varchar(40)										not null references code_part_list_type(id),
	seq_nr																integer												not null,
	--
	subject																varchar(100),
	content																text,
	is_private														boolean,
	--
	created_by_user_id										integer,											-- not null references obj_user(obj_id),
	created_at														timestamp with time zone			default now()::timestamp,
	modified_by_user_id										integer,											-- references obj_user(obj_id),
	modified_at														timestamp with time zone,
	--
	primary key (id)
);
