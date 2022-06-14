
create table obj_test (
	obj_id																integer							not null references obj(id) deferrable initially deferred,
	-- simple fields
	short_text														varchar(200),
	long_text															text,
	date																	date,
	int																		integer,
	is_done																boolean,
	json																	json,
	nr																		decimal(18,4),
	-- references
	country_id														varchar(40)					references code_country(id),
	ref_test_id														integer							references obj_test(obj_id),
	--
	primary key (obj_id)
);

create or replace view obj_test_v
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
				ot.*
from		obj_test ot
join obj on obj.id = ot.obj_id;

create table obj_test_part_node (
	id																		integer												not null,
	obj_id																integer												not null references obj(id) deferrable initially deferred,
	parent_part_id												integer,											-- optional reference to parent obj-part
	part_list_type_id											varchar(40)										not null references code_part_list_type(id),
	seq_nr																integer,
	-- simple fields
	short_text														varchar(200),
	long_text															text,
	date																	date,
	int																		integer,
	is_done																boolean,
	json																	json,
	nr																		decimal(18,4),
	-- references
	country_id														varchar(40)										references code_country(id),
	ref_obj_id														integer												references obj_test(obj_id),
	--
	primary key (id)
);


create table doc_test (
	doc_id																integer not null references doc(id) deferrable initially deferred,
	-- simple fields
	short_text														varchar(200),
	long_text															text,
	date																	date,
	int																		integer,
	is_done																boolean,
	json																	json,
	nr																		decimal(18,4),
	-- references
	country_id														varchar(40)					references code_country(id),
	--
	primary key (doc_id)
);

create or replace view doc_test_v
as
select	doc.tenant_id,
				doc.doc_type_id,
				doc.id,
				doc.owner_id,
				doc.assignee_id,
				doc.caption,
				--
				doc.case_def_id,
				doc.case_stage_id,
				doc.is_in_work,
				--
				doc.account_id,
				--
				doc.created_by_user_id,
				doc.created_at,
				doc.modified_by_user_id,
				doc.modified_at,
				--
				dt.*
from		doc_test dt
join doc on doc.id = dt.doc_id;

create table doc_test_part_node (
	id																		integer												not null,
	doc_id																integer												not null references doc(id) deferrable initially deferred,
	parent_part_id												integer,											-- optional reference to parent doc-part
	part_list_type_id											varchar(40)										not null references code_part_list_type(id),
	seq_nr																integer,
	-- simple fields
	short_text														varchar(200),
	long_text															text,
	date																	date,
	int																		integer,
	is_done																boolean,
	json																	json,
	nr																		decimal(18,4),
	-- references
	country_id														varchar(40)										references code_country(id),
	ref_obj_id														integer												references obj_test(obj_id),
	ref_doc_id														integer												references doc_test(doc_id),
	--
	primary key (id)
);
