
drop trigger if exists item_search_lower on item_search;
drop function if exists item_search_lower;

drop trigger if exists obj_contact_search on obj_contact;
drop function if exists copy_contact_search;

drop trigger if exists obj_account_search on obj_account;
drop function if exists copy_account_search;

drop view if exists obj_contact_search;
drop view if exists obj_account_search;

drop trigger if exists obj_building_search on obj_building;
drop function if exists copy_building_search;
drop view if exists obj_building_search;

drop trigger if exists obj_portfolio_search on obj_portfolio;
drop function if exists copy_portfolio_search;
drop view if exists obj_portfolio_search;

drop trigger if exists doc_lead_search on doc_lead;
drop function if exists copy_lead_search;
drop view if exists obj_lead_search;
