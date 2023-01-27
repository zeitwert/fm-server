
create table code_tenant_type (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table obj_tenant (
	obj_id																integer							not null references obj(id) deferrable initially deferred,
	--
	tenant_type_id												varchar(40)					not null references code_tenant_type(id),
	name																	varchar(100),
	description														text,
	extl_key															varchar(60),
	logo_img_id														integer, -- references obj_document(obj_id) deferrable initially deferred
	--
	inflation_rate 												decimal,
	--
	primary key (obj_id),
	constraint extl_key_unique unique (extl_key)
);

alter table obj
add constraint obj$tenant
foreign key (tenant_id) references obj_tenant(obj_id) deferrable initially deferred;

alter table obj_part_transition
add constraint obj_part_transition$tenant
foreign key (tenant_id) references obj_tenant(obj_id) deferrable initially deferred;

alter table doc
add constraint doc$tenant
foreign key (tenant_id) references obj_tenant(obj_id) deferrable initially deferred;

alter table doc_part_transition
add constraint doc_part_transition$tenant
foreign key (tenant_id) references obj_tenant(obj_id) deferrable initially deferred;
