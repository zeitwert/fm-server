
create sequence item_part_id_seq;

create or replace view item
as
select
	tenant_id,
	obj_type_id as item_type_id,
	id,
	owner_id,
	--
	caption,
	--
	created_by_user_id,
	created_at,
	modified_by_user_id,
	modified_at
from obj
union
select
	tenant_id,
	doc_type_id as item_type_id,
	id,
	owner_id,
	--
	caption,
	--
	created_by_user_id,
	created_at,
	modified_by_user_id,
	modified_at
from doc;
