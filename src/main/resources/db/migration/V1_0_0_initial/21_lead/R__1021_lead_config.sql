
insert into code_aggregate_type(id, name)
values
('doc_lead', 'Lead');

insert into code_case_def(id, doc_type_id, name)
values
('lead', 'doc_lead', 'Lead Process');

insert into code_case_stage(case_def_id, seq_nr, id, case_stage_type_id, name, description, due, action, abstract_case_stage_id)
values
('lead', 10, 'lead.new',              'initial',      'Assign',                '', 3, null,          null),
('lead', 20, 'lead.contact',       	  'intermediate', 'Contact',               '', 3, null,          null),
('lead', 30, 'lead.qualify',          'intermediate', 'Qualify',               '', 3, null,          null),
('lead', 40, 'lead.done',             'abstract',     'Convert', 	             '', 3, 'convertLead', null),
('lead', 41, 'lead.done_unqualify',   'terminal',     'Unqualified',           '', 3, null,          'lead.done'),
('lead', 42, 'lead.done_nurture',     'terminal',     'Converted Nurturing',   '', 3, null,          'lead.done'),
('lead', 43, 'lead.done_opportunity', 'terminal',     'Converted Opportunity', '', 3, null,          'lead.done'),
('lead', 44, 'lead.done_advice',      'terminal',     'Converted Advice',      '', 3, null,          'lead.done');

insert into code_lead_source(id, name)
values
('manual', 'Manual'),
('campaign', 'Campaign'),
('web', 'Website'),
('email', 'Email'),
('call', 'Phone call'),
('crm', 'CRM System'),
('cb', 'Core Banking'),
('event', 'Event'),
('referral', 'Referral'),
('meeting', 'Meeting');

insert into code_lead_rating(id, name)
values
('cold', 'Cold'),
('warm', 'Warm'),
('hot', 'Hot');
