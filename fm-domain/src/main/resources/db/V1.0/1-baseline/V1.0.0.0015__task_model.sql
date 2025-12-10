
create table code_task_priority (
	id																		varchar(40) not null,
	--
	name																	varchar(100) not null,
	--
	primary key (id)
);

create table doc_task (
	doc_id																integer												not null references doc(id) deferrable initially deferred,
	tenant_id															integer												not null references obj_tenant(obj_id) deferrable initially deferred,
	account_id														integer												references obj_account(obj_id) deferrable initially deferred,
	--
	related_obj_id												integer												references obj(id) deferrable initially deferred,
	related_doc_id												integer												references doc(id) deferrable initially deferred,
	--
	subject																varchar(100),
	content																text,
	is_private														boolean,
	--
	priority_id														varchar(40)										not null references code_task_priority (id),
	due_at																timestamp with time zone			not null,
	remind_at															timestamp with time zone,
	--
	primary key (doc_id)
);

create index doc_task$tenant
on doc_task(tenant_id);

create index doc_task$account
on doc_task(account_id);

create index doc_task$related_obj
on     doc_task(related_obj_id);

create index doc_task$related_doc
on     doc_task(related_doc_id);
