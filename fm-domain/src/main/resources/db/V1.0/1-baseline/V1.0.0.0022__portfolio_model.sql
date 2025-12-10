
create table obj_portfolio (
	obj_id																integer							not null references obj(id) deferrable initially deferred,
	tenant_id															integer							not null references obj_tenant(obj_id) deferrable initially deferred,
	account_id														integer							not null references obj_account(obj_id) deferrable initially deferred,
	--
	intl_key															varchar(60),
	name																	varchar(100),
	description														text,
	--
	portfolio_nr													varchar(200), -- Identifikation
	--
	primary key (obj_id)
);

create index obj_portfolio$account
on obj_portfolio(account_id);
