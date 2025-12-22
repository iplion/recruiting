-- V2__init_data.sql
set search_path to recruiting;

insert into roles(name) values
    ('director'),
    ('teamlead'),
    ('pm')
on conflict (name) do nothing;

-- Статусы кандидата (как в классификаторах ВКР)
insert into candidate_statuses(name) values
    ('new'),
    ('test_sent'),
    ('test_received'),
    ('tech_review'),
    ('pm_review'),
    ('director_review'),
    ('invited'),
    ('rejected'),
    ('paused')
on conflict (name) do nothing;
