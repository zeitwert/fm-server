
drop view if exists activity_v;

create or replace view activity_v
as
select	* from doc_activity_v
union all
select	* from obj_activity_v
order by timestamp desc;
