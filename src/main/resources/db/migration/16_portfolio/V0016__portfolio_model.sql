
create table obj_portfolio (
	obj_id																integer							not null references obj(id) deferrable initially deferred,
	--
	intl_key															varchar(60),
	name																	varchar(100),
	description														text,
	--
	account_id													integer							not null references obj_account(obj_id) deferrable initially deferred,
	--
	portfolio_nr													varchar(200), -- Identifikation
	--
	primary key (obj_id)
);

create or replace view obj_portfolio_v
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
				pf.*
from		obj_portfolio pf
join obj on obj.id = pf.obj_id;
