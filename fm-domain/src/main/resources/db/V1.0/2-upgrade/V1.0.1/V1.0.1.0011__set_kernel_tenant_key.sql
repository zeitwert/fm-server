
-- Set key for kernel tenant (for existing databases that were created before the key column was added)
do $$
begin
    update obj_tenant
    set key = 'kernel'
    where tenant_type_id = 'kernel' 
      and name = 'Kernel'
      and key is null;
end $$;
