
insert into code_aggregate_type(id, name)
values
('obj_document', 'Document')
on conflict(id)
do nothing;

insert into code_document_category(id, name)
values
('avatar', 'Avatar'),
('banner', 'Banner'),
('foto', 'Foto'),
('logo', 'Logo')
on conflict(id)
do nothing;

insert into code_document_kind(id, name)
values
('standalone', 'Standalone'),  -- f.ex. "Company Presentation"
('template',   'Template'),    -- f.ex. "Service Contract (Template)"
('instance',   'Instance')     -- f.ex. "Service Contract Thomas Meier"
on conflict(id)
do nothing;

insert into code_content_kind(id, name)
values
('document', 'Document'),
('foto', 'Foto'),
('video', 'Video')
on conflict(id)
do nothing;

insert into code_content_type(content_kind_id, id, name, extension, mime_type)
values
('document', 'pdf',  'PDF',             'pdf',  'application/pdf'),
('document', 'doc',  'Word',            'doc',  'application/msword'),
('document', 'xls',  'Excel',           'xls',  'application/vnd.ms-excel'),
('document', 'ppt',  'Powerpoint',      'ppt',  'application/vnd.ms-powerpoint'),
('document', 'docx', 'Word 2007',       'docx', 'application/vnd.openxmlformats-officedocument.wordprocessing'),
('document', 'xlsx', 'Excel 2007',      'xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'),
('document', 'pptx', 'Powerpoint 2007', 'pptx', 'application/vnd.openxmlformats-officedocument.presentationml.presentation'),
('foto',     'jpg',  'JPG Image',       'jpg',  'image/jpeg'),
('foto',     'gif',  'GIF Image',       'gif',  'image/gif'),
('foto',     'png',  'PNG Image',       'png',  'image/png'),
('foto',     'svg',  'SVG Image',       'svg',  'image/svg+xml'),
('video',    'mpeg', 'MPEG Video',      'mpeg', 'video/mpeg'),
('video',    'mp4',  'MP4 Video',       'mp4',  'video/mp4'),
('video',    'mov',  'MOV Video',       'mov',  'video/quicktime'),
('video',    'avi',  'AVI Video',       'avi',  'video/avi'),
('video',    'mp3',  'MP3 Audio',       'mp3',  'audio/mpeg')
on conflict(id)
do nothing;
