
create sequence obj_id_seq minvalue 1;

create sequence obj_part_id_seq;

create table obj (
	tenant_id															integer												not null, -- references obj_tenant(obj_id),
	obj_type_id														varchar(40)										not null references code_aggregate_type(id),
	id																		integer												not null,
	owner_id															integer,											-- references obj_user(obj_id),
	--
	caption																varchar(200),
	--
	ref_obj_id														integer												references obj(id) deferrable initially deferred,
	--
	created_by_user_id										integer,											-- not null references obj_user(obj_id),
	created_at														timestamp with time zone			default now()::timestamp,
	modified_by_user_id										integer,											-- references obj_user(obj_id),
	modified_at														timestamp with time zone,
	--
	primary key (id)
);

create table obj_part_item (
	obj_id																integer												not null references obj(id) deferrable initially deferred,
	parent_part_id												integer default 0, 						-- reference to parent part (optional)
	part_list_type_id											varchar(40)										not null references code_part_list_type(id),
	seq_nr																integer												not null,
	--
	item_id																varchar(40)										not null,
	--
	primary key (obj_id, parent_part_id, part_list_type_id, seq_nr)
);

create table obj_part_transition (
	id																		integer												not null,
	obj_id																integer												not null references obj(id) deferrable initially deferred,
	parent_part_id												integer,
	part_list_type_id											varchar(40)										not null references code_part_list_type(id),
	seq_nr																integer												not null,
	--
	user_id																integer,											-- references obj_user(obj_id),
	modified_at														timestamp with time zone,
	--
	changes																json,
	--
	primary key (id)
);
