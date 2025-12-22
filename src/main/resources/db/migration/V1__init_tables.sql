-- V1__init_tables.sql
create schema if not exists recruiting;

set search_path to recruiting;

create table if not exists roles (
    id bigserial primary key,
    name varchar(30) not null unique
);

create table if not exists users (
    id bigserial primary key,
    full_name varchar(100) not null,
    login varchar(100) not null unique,
    password_hash varchar(255) not null,
    role_id bigint not null references roles(id) on delete restrict
);

create table if not exists candidate_statuses (
    id bigserial primary key,
    name varchar(50) not null unique
);

create table if not exists vacancies (
    id bigserial primary key,
    title varchar(255) not null,
    level varchar(100),
    status varchar(20) not null default 'DRAFT',
    constraint chk_vacancy_status
        check (status in ('DRAFT','OPEN','ON_HOLD','CLOSED'))
);

create table if not exists candidates (
    id bigserial primary key,
    access_token varchar(255) not null unique,
    token_expires_at timestamptz,
    full_name varchar(255) not null,
    contacts text,
    experience text,
    status_id bigint not null references candidate_statuses(id) on delete restrict,
    planned_vacancy_id bigint references vacancies(id) on delete set null
);

create index if not exists idx_candidates_status_id on candidates(status_id);

create table if not exists test_tasks (
    id bigserial primary key,
    title varchar(255) not null,
    description text,
    complexity_level varchar(100)
);

create table if not exists task_assignments (
    id bigserial primary key,
    candidate_id bigint not null references candidates(id) on delete cascade,
    task_id bigint not null references test_tasks(id) on delete restrict,
    assigned_by bigint not null references users(id) on delete restrict,
    status varchar(30) not null default 'ASSIGNED',
    solution_link text,
    solution_text text,
    constraint chk_task_assignment_status
        check (status in ('ASSIGNED','SUBMITTED','REVIEWED'))
);

create index if not exists idx_task_assignments_candidate_id on task_assignments(candidate_id);
create index if not exists idx_task_assignments_task_id on task_assignments(task_id);
create index if not exists idx_task_assignments_assigned_by on task_assignments(assigned_by);

create table if not exists reviews (
    id bigserial primary key,
    candidate_id bigint not null references candidates(id) on delete cascade,
    reviewer_id bigint not null references users(id) on delete restrict,
    recommended_vacancy_id bigint references vacancies(id) on delete set null,
    score int,
    decision varchar(30) not null,
    comment text,
    created_at timestamptz not null default now(),
    constraint chk_review_decision
        check (decision in ('RECOMMEND', 'NOT_RECOMMEND', 'INVITE', 'REJECT', 'PAUSE'))
);

create index if not exists idx_reviews_candidate_id on reviews(candidate_id);
create index if not exists idx_reviews_reviewer_id on reviews(reviewer_id);
create index if not exists idx_reviews_recommended_vacancy_id on reviews(recommended_vacancy_id);
