
insert into migr_obj_user_v(tenant,name,email,password,role_list) values
('demo', 'Admin', 'admin@zeitwert.io', '{noop}demo', 'admin'),
('demo', 'Hannes Brunner', 'hannes@zeitwert.io', '{noop}demo', 'super_user'),
('demo', 'Martin Frey', 'martin@zeitwert.io', '{noop}demo', 'super_user'),
('demo', 'Xavier Frey', 'xavier@zeitwert.io', '{noop}demo', 'user'),
('demo', 'Morgan Frey', 'morgan@zeitwert.io', '{noop}demo', 'user'),
('demo', 'Robert Reader', 'reader@zeitwert.io', '{noop}demo', 'read_only'),
('demo', 'Simon Schatzmann', 'simon@zeitwert.io', '{noop}demo', 'user'),
('demo', 'Barbara Buchhalter', 'barbara@zeitwert.io', '{noop}demo', 'user'),
('demo', 'Verena Verwalterin', 'verena@zeitwert.io', '{noop}demo', 'super_user');

-- add tenant access
insert	into obj_part_item(
					obj_id,
					parent_part_id,
					part_list_type_id,
					seq_nr,
					item_id
				)
select	uv.id,
				0,
				'user.tenantList',
				0,
				uv.tenant_id
from		obj_user_v uv
where		not exists (select null from obj_part_item pi where pi.obj_id = uv.id and pi.part_list_type_id = 'user.tenantList');
