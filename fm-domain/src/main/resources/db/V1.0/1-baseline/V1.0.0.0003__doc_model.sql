
create table code_case_def (
	id																		varchar(40)					not null,
	--
	doc_type_id														varchar(40)					not null references code_aggregate_type(id),
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table code_case_stage_type (
	id																		varchar(40)										not null,
	--
	name																	varchar(100)									not null,
	--
	primary key (id)
);

create table code_case_stage (
	id																		varchar(40)										not null,
	--
	case_def_id														varchar(40)										not null references code_case_def(id),
	seq_nr																integer												not null,
	case_stage_type_id										varchar(40)										not null references code_case_stage_type(id),
	name																  varchar(100)									not null,
	description														text,
	due																		integer												not null,
	action																varchar(100),
	abstract_case_stage_id								varchar(40)										references code_case_stage(id),
	available_actions											varchar(1000),
	--
	primary key (id)
);

create sequence doc_id_seq minvalue 100000000;

create sequence doc_part_id_seq;

create table doc (
	id																		integer												not null,
	doc_type_id														varchar(40)										not null references code_aggregate_type(id),
	tenant_id															integer												not null, -- references obj_tenant(obj_id),
	account_id														integer,											-- references obj_account(obj_id) deferrable initially deferred,
	version																integer												not null default 0,
	--
	owner_id															integer 											not null, -- references obj_user(obj_id) not null,
	caption																varchar(200),
	--
	case_def_id														varchar(40)										not null references code_case_def(id) deferrable initially deferred,
	case_stage_id													varchar(40)										not null references code_case_stage(id) deferrable initially deferred,
	is_in_work														boolean,
	assignee_id														integer,											-- references obj(obj_id),
	--
	created_by_user_id										integer												not null, -- references obj_user(obj_id),
	created_at														timestamp with time zone			not null default now()::timestamp,
	modified_by_user_id										integer,	 										-- references obj_user(obj_id),
	modified_at														timestamp with time zone,
	--
	primary key (id)
);

create table doc_part_item (
	doc_id																integer												not null references doc(id) deferrable initially deferred,
	parent_part_id												integer												not null default 0, -- reference to parent part (optional)
	part_list_type_id											varchar(40)										not null references code_part_list_type(id),
	seq_nr																integer												not null,
	--
	item_id																varchar(40)										not null,
	--
	primary key (doc_id, parent_part_id, part_list_type_id, seq_nr)
);

create table doc_part_transition (
	id																		integer												not null,
	tenant_id															integer												not null, -- references obj_tenant(obj_id),
	doc_id																integer												not null references doc(id) deferrable initially deferred,
	parent_part_id												integer												not null default 0,
	part_list_type_id											varchar(40)										not null references code_part_list_type(id),
	seq_nr																integer												not null,
	--
	user_id																integer												not null, -- references obj_user(obj_id),
	timestamp															timestamp with time zone			not null default now()::timestamp,
	--
	old_case_stage_id											varchar(40)										references code_case_stage(id) deferrable initially deferred,
	new_case_stage_id											varchar(40)										references code_case_stage(id) deferrable initially deferred,	
	--
	changes																json,
	--
	primary key (id)
);

create index doc_part_transition$part
on     doc_part_transition(doc_id, parent_part_id, part_list_type_id, seq_nr);

create index doc_part_transition$activity
on     doc_part_transition(tenant_id, timestamp);
