
insert into obj(id, tenant_id, account_id, obj_type_id, owner_id, caption, created_by_user_id)
values (
	nextval('obj_id_seq'),
	(select id from obj_tenant_v where extl_key = 'demo'),
	(select obj_id from obj_account where intl_key = '3043'),
	'obj_portfolio',
	(select id from obj_user_v where email = 'martin@zeitwert.io'),
	'Gesamtbestand',
	(select id from obj_user_v where email = 'martin@zeitwert.io')
);

insert into obj_portfolio(obj_id, tenant_id, intl_key, name, account_id)
values (
	(select max(id) from obj),
	(select id from obj_tenant_v where extl_key = 'demo'),
	'p1',
	'Gesamtbestand',
	(select obj_id from obj_account where intl_key = '3043')
);

insert into obj_part_item(obj_id, part_list_type_id, seq_nr, item_id)
values ((select obj_id from obj_portfolio where intl_key = 'p1'), 'portfolio.includeList', 1, (select obj_id from obj_account where intl_key = '3043'));

insert into obj(id, tenant_id, account_id, obj_type_id, owner_id, caption, created_by_user_id)
values (
	nextval('obj_id_seq'),
	(select id from obj_tenant_v where extl_key = 'demo'),
	(select obj_id from obj_account where intl_key = '3043'),
	'obj_portfolio',
	(select id from obj_user_v where email = 'martin@zeitwert.io'),
	'Finanzvermögen',
	(select id from obj_user_v where email = 'martin@zeitwert.io')
);

insert into obj_portfolio(obj_id, tenant_id, intl_key, name, account_id)
values (
	(select max(id) from obj),
	(select id from obj_tenant_v where extl_key = 'demo'),
	'p2',
	'Finanzvermögen',
	(select obj_id from obj_account where intl_key = '3043')
);

insert into obj_part_item(obj_id, part_list_type_id, seq_nr, item_id)
values ((select obj_id from obj_portfolio where intl_key = 'p2'), 'portfolio.includeList', 1, (select obj_id from obj_building where zip = '3043' and name = 'Turnhalle'));

insert into obj_part_item(obj_id, part_list_type_id, seq_nr, item_id)
values ((select obj_id from obj_portfolio where intl_key = 'p2'), 'portfolio.includeList', 2, (select obj_id from obj_building where zip = '3043' and name = 'Kulturelles Zentrum Reberhaus'));

insert into obj_part_item(obj_id, part_list_type_id, seq_nr, item_id)
values ((select obj_id from obj_portfolio where intl_key = 'p2'), 'portfolio.includeList', 3, (select obj_id from obj_building where zip = '3043' and name = 'Doppelkindergarten/Musikschule/Jugendtr.'));

insert into obj(id, tenant_id, account_id, obj_type_id, owner_id, caption, created_by_user_id)
values (
	nextval('obj_id_seq'),
	(select id from obj_tenant_v where extl_key = 'demo'),
	(select obj_id from obj_account where intl_key = '3043'),
	'obj_portfolio',
	(select id from obj_user_v where email = 'martin@zeitwert.io'),
	'Verwaltungsvermögen',
	(select id from obj_user_v where email = 'martin@zeitwert.io')
);

insert into obj_portfolio(obj_id, tenant_id, intl_key, name, account_id)
values (
	(select max(id) from obj),
	(select id from obj_tenant_v where extl_key = 'demo'),
	'p3',
	'Verwaltungsvermögen',
	(select obj_id from obj_account where intl_key = '3043')
);

insert into obj_part_item(obj_id, part_list_type_id, seq_nr, item_id)
values ((select obj_id from obj_portfolio where intl_key = 'p3'), 'portfolio.includeList', 1, (select obj_id from obj_account where intl_key = '3043'));

insert into obj_part_item(obj_id, part_list_type_id, seq_nr, item_id)
values ((select obj_id from obj_portfolio where intl_key = 'p3'), 'portfolio.excludeList', 1, (select obj_id from obj_portfolio where intl_key = 'p2'));
