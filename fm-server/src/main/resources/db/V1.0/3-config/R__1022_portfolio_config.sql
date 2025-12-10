
insert into code_aggregate_type(id, name)
values
('obj_portfolio', 'Portfolio')
on conflict(id)
do nothing;

insert into code_part_list_type(id, name)
values
('portfolio.includeList', 'Included Items (Account, Portfolio, Building)'),
('portfolio.excludeList', 'Excluded Items (Account, Portfolio, Building)'),
('portfolio.buildingList', 'Buildings')
on conflict(id)
do nothing;
