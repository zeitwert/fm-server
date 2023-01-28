
insert into migr_obj_tenant_v(key, tenant_type_id, name) values
('test', 'community', 'Test Tenant');

insert into migr_obj_user_v(tenant, name, email, password, role_list) values
('test', 'Tony Testeroni', 'tt@zeitwert.io', '{noop}test', 'user');

insert into migr_obj_user_v(tenant, name, email, password, role_list) values
('test', 'Chuck Checkeroni', 'cc@zeitwert.io', '{noop}test', 'user');

insert into migr_obj_account_v(tenant, owner, key, name, account_type_id, reference_currency) values
('test', 'tt@zeitwert.io', 'TA', 'Testlingen', 'client', 'chf');
