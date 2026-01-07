
-- Fix key columns for test data that was inserted before the key column was added
do $$
begin
    if exists (select 1 from information_schema.tables where table_name = 'migr_key') then
        -- First, ensure migr_key has entries for existing tenants (for test tenant)
        insert into migr_key(obj_id, obj_type_id, tenant_id, key)
        select t.obj_id, 'tenant', t.obj_id, 'test'
        from obj_tenant t
        where t.name = 'Test Tenant'
        and not exists (select 1 from migr_key mk where mk.obj_id = t.obj_id);
        
        -- Then update tenant key column
        update obj_tenant t
        set key = mk.key
        from migr_key mk
        where mk.obj_id = t.obj_id and mk.obj_type_id = 'tenant' and t.key is null;
        
        -- Ensure migr_key has entries for existing accounts (for test account)
        insert into migr_key(obj_id, obj_type_id, tenant_id, key)
        select a.obj_id, 'account', a.tenant_id, 'TA'
        from obj_account a
        where a.name = 'Testlingen'
        and not exists (select 1 from migr_key mk where mk.obj_id = a.obj_id);
        
        -- Then update account key column
        update obj_account a
        set key = mk.key
        from migr_key mk
        where mk.obj_id = a.obj_id and mk.obj_type_id = 'account' and a.key is null;
    end if;
end $$;

