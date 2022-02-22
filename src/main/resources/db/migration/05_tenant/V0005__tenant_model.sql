
create table obj_tenant (
	obj_id																integer							not null references obj(id) deferrable initially deferred,
	--
	name																	varchar(100),
	description														text,
	extl_key															varchar(60)					not null,
	--
	primary key (obj_id),
	constraint extl_key_unique unique (extl_key)
);

create or replace view obj_tenant_v
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
				--
				t.*
from		obj_tenant t
join obj on obj.id = t.obj_id;

alter table obj
add constraint obj$tenant
foreign key (tenant_id) references obj_tenant(obj_id) deferrable initially deferred;

alter table doc
add constraint doc$tenant
foreign key (tenant_id) references obj_tenant(obj_id) deferrable initially deferred;
