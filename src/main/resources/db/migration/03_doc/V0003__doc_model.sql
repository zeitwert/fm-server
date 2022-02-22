
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
	current_name													varchar(100)									not null,
	past_name															varchar(100)									not null,
	description														text,
	due																		integer												not null,
	action																varchar(100),
	abstract_case_stage_id								varchar(40)										references code_case_stage(id),
	available_actions											varchar(1000),
	--
	primary key (id)
);

create sequence doc_id_seq minvalue 1000000;

create sequence doc_part_id_seq;

create table doc (
	tenant_id															integer,											-- not null references obj_tenant(obj_id),
	doc_type_id														varchar(40)										not null references code_aggregate_type(id),
	id																		integer												not null,
	owner_id															integer 											not null, -- references obj_user(obj_id) not null,
	--
	caption																varchar(200),
	--
	case_def_id														varchar(40)										not null references code_case_def(id) deferrable initially deferred,
	case_stage_id													varchar(40)										not null references code_case_stage(id) deferrable initially deferred,
	is_in_work														boolean												not null default true,
	assignee_id														integer,											-- references obj(obj_id),
	--
	account_id													integer,											-- references obj_account(obj_id) deferrable initially deferred,
	ref_obj_id														integer,											-- references obj(id) deferrable initially deferred,
	ref_doc_id														integer												references doc(id) deferrable initially deferred,
	--
	created_by_user_id										integer,											--not null references obj_user(obj_id),
	created_at														timestamp with time zone			default now()::timestamp,
	modified_by_user_id										integer,	 										-- references obj_user(obj_id),
	modified_at														timestamp with time zone,
	--
	primary key (id)
);

create index doc$is_in_work
on     doc(is_in_work);

create table doc_part_item (
	doc_id																integer												not null references doc(id) deferrable initially deferred,
	parent_part_id												integer default 0, 						-- reference to parent part (optional)
	part_list_type_id											varchar(40)										not null references code_part_list_type(id),
	seq_nr																integer												not null,
	--
	item_id																varchar(40)										not null,
	--
	primary key (doc_id, parent_part_id, part_list_type_id, seq_nr)
);

create table doc_part_transition (
	id																		integer												not null,
	doc_id																integer												not null references doc(id) deferrable initially deferred,
	parent_part_id												integer,
	part_list_type_id											varchar(40)										not null references code_part_list_type(id),
	seq_nr																integer												not null,
	--
	user_id																integer,											-- references obj_user(obj_id),
	modified_at														timestamp with time zone,
	--
	old_case_stage_id											varchar(40)										references code_case_stage(id) deferrable initially deferred,
	new_case_stage_id											varchar(40)										references code_case_stage(id) deferrable initially deferred,	
	--
	changes																json,
	--
	primary key (id)
);
