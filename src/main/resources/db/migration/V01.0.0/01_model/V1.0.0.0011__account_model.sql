
create table code_account_type (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table code_client_segment (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table code_area (
	id																		varchar(40) 				not null,
	--
	name																	varchar(100) 				not null,
	--
	primary key (id)
);

create table code_country (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table code_locale (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table code_currency (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table obj_account (
	obj_id																integer							not null references obj(id) deferrable initially deferred,
	tenant_id															integer							not null references obj_tenant(obj_id) deferrable initially deferred,
	--
	intl_key															varchar(60),
	name																	varchar(100),
	description														text,
	--
	account_type_id												varchar(40)					not null references code_account_type(id),
	client_segment_id											varchar(40)					references code_client_segment(id),
	main_contact_id												integer,						-- references obj_contact(obj_id) deferrable initially deferred,
	reference_currency_id									varchar(40)					references code_currency(id),
	--
	primary key (obj_id)
);

alter table doc
add constraint doc$account
foreign key (account_id) references obj_account(obj_id) deferrable initially deferred;

alter table obj
add constraint obj$account
foreign key (account_id) references obj_account(obj_id) deferrable initially deferred;

create or replace view obj_account_v
as
select	obj.obj_type_id,
				a.obj_id as id,
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
				a.obj_id as account_id,
				--
				a.*
from		obj_account a
join obj on obj.id = a.obj_id;
