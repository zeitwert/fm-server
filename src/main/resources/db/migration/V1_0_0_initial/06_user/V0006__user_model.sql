
create table code_user_role (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table obj_user (
	obj_id																integer							not null references obj(id) deferrable initially deferred,
	--
	name																	varchar(100),
	description														text,
	email																	varchar(100)				not null,
	password															varchar(100)				not null,
	role_list															varchar(400)				not null, -- comma-separated list of code_user_role
	--
	picture																varchar(400),
	--
	primary key (obj_id),
	constraint email_unique unique (email)
);

alter table obj
add constraint obj$owner
foreign key (owner_id) references obj_user(obj_id) deferrable initially deferred;

alter table obj
add constraint obj$created_by
foreign key (created_by_user_id) references obj_user(obj_id) deferrable initially deferred;

alter table obj
add constraint obj$modified_by
foreign key (created_by_user_id) references obj_user(obj_id) deferrable initially deferred;

alter table obj
add constraint obj$closed_by
foreign key (closed_by_user_id) references obj_user(obj_id) deferrable initially deferred;

alter table obj_part_transition
add constraint obj_part_transition$user
foreign key (user_id) references obj_user(obj_id) deferrable initially deferred;

alter table doc
add constraint doc$owner
foreign key (owner_id) references obj_user(obj_id) deferrable initially deferred;

alter table doc
add constraint doc$created_by
foreign key (created_by_user_id) references obj_user(obj_id) deferrable initially deferred;

alter table doc
add constraint doc$modified_by
foreign key (created_by_user_id) references obj_user(obj_id) deferrable initially deferred;

alter table doc_part_transition
add constraint doc_part_transition$user
foreign key (user_id) references obj_user(obj_id) deferrable initially deferred;

create or replace view obj_user_v
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
				u.*
from		obj_user u
join obj on obj.id = u.obj_id;
