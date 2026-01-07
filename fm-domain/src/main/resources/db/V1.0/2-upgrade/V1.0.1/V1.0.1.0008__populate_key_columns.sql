
-- Populate key columns from migr_key table if it exists
do $$
begin
    if exists (select 1 from information_schema.tables where table_name = 'migr_key') then
        update obj_tenant t
        set key = mk.key
        from migr_key mk
        where mk.obj_id = t.obj_id and mk.obj_type_id = 'tenant' and t.key is null;
        
        update obj_account a
        set key = mk.key
        from migr_key mk
        where mk.obj_id = a.obj_id and mk.obj_type_id = 'account' and a.key is null;
    end if;
end $$;

