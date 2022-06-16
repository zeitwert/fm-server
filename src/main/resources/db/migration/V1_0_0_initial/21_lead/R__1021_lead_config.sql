
insert into code_aggregate_type(id, name)
values
('doc_lead', 'Lead');

insert into code_case_def(id, doc_type_id, name)
values
('lead', 'doc_lead', 'Lead Process');

insert into code_case_stage(case_def_id, seq_nr, id, case_stage_type_id, name, current_name, past_name, description, due, action, abstract_case_stage_id)
values
('lead', 10, 'lead.new',              'initial',      'Assign',					 		 'Assigning',							 'Assigned',           	  '', 3, null,          null),
('lead', 20, 'lead.contact',       		'intermediate', 'Contact',             'Contacting', 						 'Contacted', 						'', 3, null,          null),
('lead', 30, 'lead.qualify',     		  'intermediate', 'Qualify',             'Qualiryfing', 					 'Qualified', 						'', 3, null,          null),
('lead', 40, 'lead.done',             'abstract',     'Convert',             'Converting', 						 'Converted', 						'', 3, 'convertLead', null),
('lead', 41, 'lead.done_unqualify'	, 'terminal',     'Unqualify', 					 'Unqualifying' , 				 'Unqualified',           '', 3, null,          'lead.done'),
('lead', 42, 'lead.done_nurture',     'terminal',     'Convert Nurturing', 	 'Converting Nurturing', 	 'Converted Nurturing',   '', 3, null,          'lead.done'),
('lead', 43, 'lead.done_opportunity', 'terminal',     'Convert Opportunity', 'Converting Opportunity', 'Converted Opportunity', '', 3, null,          'lead.done'),
('lead', 44, 'lead.done_advice',      'terminal',     'Convert Advice', 		 'Converting Advice', 		 'Converted Advice',      '', 3, null,          'lead.done');

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
