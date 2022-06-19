
-- make building_nr not null
alter table obj_building alter column building_nr set not null;

-- populate rating
update	obj_building_part_rating r
set			rating_date = (
					select	to_date(max(e.condition_year) || '0630', 'YYYYMMDD')
					from		obj_building_part_element_rating e
					where		e.obj_id = r.obj_id
					group by e.obj_id
				);

update	obj_building_part_element_rating e
set			seq_nr = 0,
				parent_part_id = (
					select  r.id
					from    obj_building_part_rating r
					where   r.obj_id = e.obj_id
				);
