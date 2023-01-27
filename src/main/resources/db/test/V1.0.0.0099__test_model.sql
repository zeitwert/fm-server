
create table obj_test (
	obj_id																integer							not null references obj(id) deferrable initially deferred,
	tenant_id															integer							not null references obj_tenant(obj_id) deferrable initially deferred,
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
	doc_id																integer 											not null references doc(id) deferrable initially deferred,
	tenant_id															integer												not null references obj_tenant(obj_id) deferrable initially deferred,
	account_id														integer												references obj_account(obj_id) deferrable initially deferred,
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
	--
	primary key (doc_id)
);

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
