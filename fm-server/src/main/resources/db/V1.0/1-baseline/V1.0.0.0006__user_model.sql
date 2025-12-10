
create table code_user_role (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table obj_user (
	obj_id																integer							not null references obj(id) deferrable initially deferred,
	tenant_id															integer							not null references obj_tenant(obj_id) deferrable initially deferred,
	--
	email																	varchar(100)				not null,
	name																	varchar(100)				not null,
	role_list															varchar(400)				not null, -- comma-separated list of code_user_role
	description														text,
	--
	password															varchar(100)				not null,
	need_password_change									boolean,
	--
	avatar_img_id													integer, -- references obj_document(obj_id) deferrable initially deferred
	--
	primary key (obj_id),
	constraint email_unique unique (email)
);

create index obj_user$email
on     obj_user(email);

create index obj_user$tenant
on     obj_user(tenant_id);

alter table obj
add constraint obj$owner
foreign key (owner_id) references obj_user(obj_id) deferrable initially deferred;

alter table obj
add constraint obj$created_by_user
foreign key (created_by_user_id) references obj_user(obj_id) deferrable initially deferred;

alter table obj
add constraint obj$modified_by_user
foreign key (created_by_user_id) references obj_user(obj_id) deferrable initially deferred;

alter table obj
add constraint obj$closed_by_user
foreign key (closed_by_user_id) references obj_user(obj_id) deferrable initially deferred;

alter table obj_part_transition
add constraint obj_part_transition$user
foreign key (user_id) references obj_user(obj_id) deferrable initially deferred;

alter table doc
add constraint doc$owner
foreign key (owner_id) references obj_user(obj_id) deferrable initially deferred;

alter table doc
add constraint doc$created_by_user
foreign key (created_by_user_id) references obj_user(obj_id) deferrable initially deferred;

alter table doc
add constraint doc$modified_by_user
foreign key (created_by_user_id) references obj_user(obj_id) deferrable initially deferred;

alter table doc_part_transition
add constraint doc_part_transition$user
foreign key (user_id) references obj_user(obj_id) deferrable initially deferred;
