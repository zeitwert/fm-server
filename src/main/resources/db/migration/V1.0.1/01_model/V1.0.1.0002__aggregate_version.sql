
alter table obj add column version integer not null default 0;

alter table doc add column version integer not null default 0;
