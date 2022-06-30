
create sequence obj_id_seq minvalue 1;

create sequence obj_part_id_seq;

create table obj (
	id																		integer												not null,
	obj_type_id														varchar(40)										not null references code_aggregate_type(id),
	tenant_id															integer												not null, -- references obj_tenant(obj_id),
	account_id														integer,											-- references obj_account(obj_id) deferrable initially deferred,
	owner_id															integer												not null, -- references obj_user(obj_id),
	caption																varchar(200),
	--
	created_by_user_id										integer												not null, -- references obj_user(obj_id),
	created_at														timestamp with time zone			default now()::timestamp,
	modified_by_user_id										integer,											-- references obj_user(obj_id),
	modified_at														timestamp with time zone,
	closed_by_user_id											integer,											-- references obj_user(obj_id),
	closed_at															timestamp with time zone,
	--
	primary key (id),
	constraint account_id check(obj_type_id not in ('obj_contact', 'obj_building', 'obj_portfolio') or account_id is not null)
);

create table obj_part_item (
	obj_id																integer												not null references obj(id) deferrable initially deferred,
	parent_part_id												integer												not null default 0, -- reference to parent part (optional)
	part_list_type_id											varchar(40)										not null references code_part_list_type(id),
	seq_nr																integer												not null,
	--
	item_id																varchar(40)										not null,
	--
	primary key (obj_id, parent_part_id, part_list_type_id, seq_nr)
);

create table obj_part_transition (
	id																		integer												not null,
	tenant_id															integer												not null, -- references obj_tenant(obj_id),
	obj_id																integer												not null references obj(id) deferrable initially deferred,
	parent_part_id												integer												not null default 0,
	part_list_type_id											varchar(40)										not null references code_part_list_type(id),
	seq_nr																integer												not null,
	--
	user_id																integer												not null, -- references obj_user(obj_id),
	timestamp															timestamp with time zone			not null default now()::timestamp,
	--
	changes																json,
	--
	primary key (id)
);

create index obj_part_transition$part
on     obj_part_transition(obj_id, parent_part_id, part_list_type_id, seq_nr);

create index obj_part_transition$activity
on     obj_part_transition(tenant_id, timestamp);

create or replace view obj_activity_v
as
select	o.id,
				opt.seq_nr,
				opt.timestamp,
				opt.user_id,
				opt.tenant_id,
				opt.changes,
				o.owner_id,
				o.account_id,
				o.obj_type_id,
				o.caption
from		obj_part_transition opt
left join obj o
on			opt.obj_id = o.id
order by timestamp desc;
