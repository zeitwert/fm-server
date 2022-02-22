
insert into obj (id, tenant_id, obj_type_id, owner_id, caption)
values (nextval('obj_id_seq'), 2, 'obj_portfolio', 5, 'Gesamtbestand');

insert into obj_portfolio(obj_id, intl_key, name, account_id)
values ((select max(id) from obj), 'p1', 'Gesamtbestand', (select obj_id from obj_account where intl_key = '3043'));

insert into obj_part_item(obj_id, part_list_type_id, seq_nr, item_id)
values ((select obj_id from obj_portfolio where intl_key = 'p1'), 'portfolio.includeList', 1, (select obj_id from obj_account where intl_key = '3043'));

insert into obj (id, tenant_id, obj_type_id, owner_id, caption)
values (nextval('obj_id_seq'), 2, 'obj_portfolio', 5, 'Finanzvermögen');

insert into obj_portfolio(obj_id, intl_key, name, account_id)
values ((select max(id) from obj), 'p2', 'Finanzvermögen', (select obj_id from obj_account where intl_key = '3043'));

insert into obj_part_item(obj_id, part_list_type_id, seq_nr, item_id)
values ((select obj_id from obj_portfolio where intl_key = 'p2'), 'portfolio.includeList', 1, (select obj_id from obj_building where zip = '3043' and name = 'Turnhalle'));

insert into obj_part_item(obj_id, part_list_type_id, seq_nr, item_id)
values ((select obj_id from obj_portfolio where intl_key = 'p2'), 'portfolio.includeList', 2, (select obj_id from obj_building where zip = '3043' and name = 'Kulturelles Zentrum Reberhaus'));

insert into obj_part_item(obj_id, part_list_type_id, seq_nr, item_id)
values ((select obj_id from obj_portfolio where intl_key = 'p2'), 'portfolio.includeList', 3, (select obj_id from obj_building where zip = '3043' and name = 'Doppelkindergarten/Musikschule/Jugendtr.'));

insert into obj (id, tenant_id, obj_type_id, owner_id, caption)
values (nextval('obj_id_seq'), 2, 'obj_portfolio', 5, 'Verwaltungsvermögen');

insert into obj_portfolio(obj_id, intl_key, name, account_id)
values ((select max(id) from obj), 'p3', 'Verwaltungsvermögen', (select obj_id from obj_account where intl_key = '3043'));

insert into obj_part_item(obj_id, part_list_type_id, seq_nr, item_id)
values ((select obj_id from obj_portfolio where intl_key = 'p3'), 'portfolio.includeList', 1, (select obj_id from obj_account where intl_key = '3043'));

insert into obj_part_item(obj_id, part_list_type_id, seq_nr, item_id)
values ((select obj_id from obj_portfolio where intl_key = 'p3'), 'portfolio.excludeList', 1, (select obj_id from obj_portfolio where intl_key = 'p2'));
