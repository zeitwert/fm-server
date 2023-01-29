
delete from code_part_list_type
where id = 'doc.areaSet'
   or id = 'obj.areaSet';

drop table if exists code_area;
