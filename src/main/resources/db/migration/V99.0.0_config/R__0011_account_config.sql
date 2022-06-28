
insert into code_aggregate_type(id, name)
values
('obj_account', 'Account')
on conflict(id)
do nothing;

insert into code_account_type(id, name)
values
('dormant', 'Dormant / Nurturing'),
('prospect', 'Prospect'),
('client', 'Client')
on conflict(id)
do nothing;

insert into code_client_segment(id, name)
values
('community', 'Community'),
('family', 'Family Office')
on conflict(id)
do nothing;

insert into code_country(id, name)
values
('ch', 'Switzerland'),
('de', 'Germany'),
('es', 'Spain')
on conflict(id)
do nothing;

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
do nothing;

insert into code_currency(id, name)
values
('chf', 'CHF'),
('eur', 'EUR'),
('usd', 'USD')
on conflict(id)
do nothing;
