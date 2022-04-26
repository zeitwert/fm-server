
create table code_gender (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table code_salutation (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	gender_id															varchar(40)					not null references code_gender(id),
	--
	primary key (id)
);

create table code_title (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table code_contact_role (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table obj_contact (
	obj_id																integer							not null references obj(id) deferrable initially deferred,
	--
	intl_key															varchar(60),
	description														text,
	--
	account_id													integer							not null references obj_account(obj_id),
	--
	contact_role_id												varchar(40)					references code_contact_role(id),
	salutation_id													varchar(40)					references code_salutation(id),
	title_id															varchar(40)					references code_title(id),
	first_name														varchar(100),
	last_name															varchar(100),
	birth_date														date,
	phone																	varchar(60),
	mobile																varchar(60),
	email																	varchar(60),
	--
	primary key (obj_id)
);

alter table obj_account
add constraint obj_account$main_contact
foreign key (main_contact_id) references obj_contact(obj_id) deferrable initially deferred;

create or replace view obj_contact_v
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
				ct.*
from		obj_contact ct
join obj on id = ct.obj_id;


create table code_address_type (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table code_interaction_channel (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table obj_contact_part_address (
	id																		integer							not null,
	obj_id																integer							not null references obj_contact(obj_id) deferrable initially deferred,
	parent_part_id												integer,						-- reference to parent part (optional)
	part_list_type_id											varchar(40) 				not null default 'contact.addressList' references code_part_list_type(id),
	seq_nr																integer,
	--
	key																		varchar(60),
	address_type_id												varchar(40)					references code_address_type(id),
	name																	varchar(100)				not null,
	--
	street																varchar(100),
	zip																		varchar(60),
	city																	varchar(60),
	state																	varchar(60),
	country_id														varchar(40)					references code_country(id),
	--
	channel_id														varchar(40)					references code_interaction_channel(id),
	is_favorite														boolean,
	is_mail_address												boolean,
	--
	primary key (id)
);


create table code_anniversary_type (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table code_anniversary_notification (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table code_anniversary_template (
	id																		varchar(40)					not null,
	--
	name																	varchar(100)				not null,
	--
	primary key (id)
);

create table obj_contact_part_anniversary (
	id																		integer							not null,
	obj_id																integer							not null references obj_contact(obj_id) deferrable initially deferred,
	parent_part_id												integer,						-- reference to parent part (optional)
	part_list_type_id											varchar(40) default 'contact.anniversaryList' not null references code_part_list_type(id),
	seq_nr																integer,
	--
	key																		varchar(60),
	anniversary_type_id										varchar(40)					not null references code_anniversary_type(id),
	start_date														date								not null,
	--
	anniversary_notification_id						varchar(40)					not null references code_anniversary_notification,
	anniversary_template_id								varchar(40)					not null references code_anniversary_template,
	--
	primary key (id)
);
