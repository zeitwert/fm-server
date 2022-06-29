
create table code_note_type (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table obj_note (
	obj_id																integer							not null references obj(id) deferrable initially deferred,
	tenant_id															integer							not null references obj_tenant(obj_id) deferrable initially deferred,
	--
	note_type_id													varchar(40)					not null references code_note_type(id) deferrable initially deferred,
	subject																varchar(100),
	content																text,
	is_private														boolean,
	--
	related_to_id													integer							not null, --references item(id) deferrable initially deferred,
	--
	primary key (obj_id)
);

create or replace view obj_note_v
as
select	obj.obj_type_id,
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
				n.*
from		obj_note n
join obj on obj.id = n.obj_id;
