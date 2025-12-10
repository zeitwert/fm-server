
insert into code_aggregate_type(id, name)
values
('obj_account', 'Account')
on conflict(id)
do nothing;

delete from code_account_type where id = 'dormant';

insert into code_account_type(id, name)
values
('prospect', 'Prospekt / Pilot'),
('client', 'Kunde')
on conflict(id)
do
update set name = excluded.name;

insert into code_client_segment(id, name)
values
('community', 'Gemeinde'),
('family', 'Family Office')
on conflict(id)
do
update set name = excluded.name;

delete from code_country where id <> 'ch';

insert into code_country(id, name)
values
('ch', 'Schweiz')
on conflict(id)
do
update set name = excluded.name;

insert into code_locale(id, name)
values
('en-US', 'English US'),
('en-UK', 'English UK'),
('de-CH', 'German CH'),
('de-DE', 'German DE'),
('fr-CH', 'French CH'),
('fr-FR', 'French FR'),
('es-ES', 'Spanish ES')
on conflict(id)
do
update set name = excluded.name;

delete from code_currency where id <> 'chf';

insert into code_currency(id, name)
values
('chf', 'CHF')
on conflict(id)
do nothing;
