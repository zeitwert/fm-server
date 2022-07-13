
create or replace view obj_building_v
as
select	obj.obj_type_id,
				b.obj_id as id,
				obj.owner_id,
				obj.caption,
				--
				obj.created_by_user_id,
				obj.created_at,
				obj.modified_by_user_id,
				obj.modified_at,
				obj.closed_by_user_id,
				obj.closed_at,
				--
				(
					select	r.rating_status_id
					from		obj_building_part_rating r
					where		r.obj_id = obj.id
						and		r.seq_nr = (
										select	max(rr.seq_nr)
										from		obj_building_part_rating rr
										where		rr.obj_id = obj.id
											and		rr.rating_status_id in ('open', 'review', 'done')
									)
				) rating_status_id,
				(
					select	r.rating_date
					from		obj_building_part_rating r
					where		r.obj_id = obj.id
						and		r.seq_nr = (
										select	max(rr.seq_nr)
										from		obj_building_part_rating rr
										where		rr.obj_id = obj.id
											and		rr.rating_status_id in ('open', 'review', 'done')
									)
				) rating_date,
				(
					select	r.rating_user_id
					from		obj_building_part_rating r
					where		r.obj_id = obj.id
						and		r.seq_nr = (
										select	max(rr.seq_nr)
										from		obj_building_part_rating rr
										where		rr.obj_id = obj.id
											and		rr.rating_status_id in ('open', 'review', 'done')
									)
				) rating_user_id,
				--
				b.*,
				--
				(
					select	r.part_catalog_id
					from		obj_building_part_rating r
					where		r.obj_id = obj.id
						and		r.seq_nr = (
										select	max(rr.seq_nr)
										from		obj_building_part_rating rr
										where		rr.obj_id = obj.id
											and		rr.rating_status_id in ('open', 'review', 'done')
									)
				) part_catalog_id,
				(
					select	r.maintenance_strategy_id
					from		obj_building_part_rating r
					where		r.obj_id = obj.id
						and		r.seq_nr = (
										select	max(rr.seq_nr)
										from		obj_building_part_rating rr
										where		rr.obj_id = obj.id
											and		rr.rating_status_id in ('open', 'review', 'done')
									)
				) maintenance_strategy_id
from		obj_building b
join obj on obj.id = b.obj_id;
