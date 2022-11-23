insert	into obj_part_item(
					obj_id,
					parent_part_id,
					part_list_type_id,
					seq_nr,
					item_id
				)
select	id,
				0,
				'user.tenantList',
				0,
				tenant_id
from		obj_user_v;

-- allow martin@comunas.ch access to clients
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
				2,
				tv.id
from		obj_user_v uv
left join obj_tenant_v tv on tv.caption = 'Gemeinde Unterägeri (Pilot)'
where		email = 'martin@comunas.ch';

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
				3,
				tv.id
from		obj_user_v uv
left join obj_tenant_v tv on tv.caption = 'Gemeinde Stäfa (Pilot)'
where		email = 'martin@comunas.ch';

-- allow all comunas to access demo
insert	into obj_part_item(
					obj_id,
					parent_part_id,
					part_list_type_id,
					seq_nr,
					item_id
				)
select	id,
				0,
				'user.tenantList',
				(select max(seq_nr)+1 from obj_part_item opi where opi.obj_id = uv.id and opi.part_list_type_id = 'user.tenantList'),
				(select t.id from obj_tenant_v t where t.caption = 'Demo')
from		obj_user_v uv
where		email like '%@comunas.ch';

-- close martin@customer
update	obj
set			closed_at = now()
where		id in (select obj_id from obj_user_v where email in ('martin.frey@unteraegeri.ch', 'martin.frey@staefa.ch'));
