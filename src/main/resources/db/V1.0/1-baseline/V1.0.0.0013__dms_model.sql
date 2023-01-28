
create table code_document_category (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table code_document_kind (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table code_content_kind (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table code_content_type (
	id																		varchar(40)					not null,
	--
	content_kind_id												varchar(40)					not null references code_content_kind(id) deferrable initially deferred,
	name																	varchar(100)				not null,
	extension															varchar(5)					not null,
	mime_type															varchar(128)				not null,
	--
	primary key (id)
);

create table obj_document (
	obj_id																integer							not null references obj(id) deferrable initially deferred,
	tenant_id															integer							not null references obj_tenant(obj_id) deferrable initially deferred,
	account_id														integer							references obj_account(obj_id) deferrable initially deferred,
	--
	document_kind_id											varchar(40)					not null references code_document_kind(id) deferrable initially deferred,
	content_kind_id												varchar(40)					not null references code_content_kind(id) deferrable initially deferred,
	name																	varchar(200)				not null,
	--
	document_category_id									varchar(40)					references code_document_category(id) deferrable initially deferred,
	template_document_id									integer							references obj_document(obj_id) deferrable initially deferred,
	--
	primary key (obj_id)
);

create table obj_document_part_content (
	obj_id																integer							not null references obj_document(obj_id) deferrable initially deferred,
	version_nr														integer							not null,
	--
	content_type_id												varchar(40)					not null references code_content_type(id) deferrable initially deferred,
	content																bytea								not null,
	--
	created_by_user_id										integer												not null references obj_user(obj_id),
	created_at														timestamp with time zone			default now()::timestamp,
	--
	primary key (obj_id, version_nr)
);

alter table obj_tenant
add constraint tenant$logo
foreign key (logo_img_id) references obj_document(obj_id) deferrable initially deferred;

alter table obj_user
add constraint user$avatar
foreign key (avatar_img_id) references obj_document(obj_id) deferrable initially deferred;

alter table obj_account
add constraint account$logo
foreign key (logo_img_id) references obj_document(obj_id) deferrable initially deferred;
