
create table code_lead_source (
	id																		varchar(40) not null,
	--
	name																	varchar(100) not null,
	--
	primary key (id)
);

create table code_lead_rating (
	id																		varchar(40) not null,
	--
	name																	varchar(100) not null,
	--
	primary key (id)
);

create table doc_lead (
	doc_id																integer not null references doc(id) deferrable initially deferred,
	--
	intl_key															varchar(60),
	subject																varchar(100) not null,
	description														text,
	--
	contact_id														integer references obj_contact(obj_id) deferrable initially deferred,
	--
	lead_source_id												varchar(40) not null references code_lead_source(id),
	--
	salutation_id													varchar(40)					references code_salutation(id),
	title_id															varchar(40)					references code_title(id),
	first_name														varchar(100),
	last_name															varchar(100),
	phone																	varchar(60),
	mobile																varchar(60),
	email																	varchar(60),
	lead_rating_id												varchar(60),
	--
	street																varchar(100),
	zip																		varchar(60),
	city																	varchar(60),
	state																	varchar(60),
	country_id														varchar(40) references code_country(id),
	--
	primary key (doc_id)
);

create or replace view doc_lead_v
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
				dl.*
from		doc_lead dl
join doc on doc.id = dl.doc_id;
