
delete from obj_part_item
where part_list_type_id = 'obj.areaSet';

delete from doc_part_item
where part_list_type_id = 'doc.areaSet';

delete from code_part_list_type
where id = 'doc.areaSet'
   or id = 'obj.areaSet';

drop table if exists code_area;
