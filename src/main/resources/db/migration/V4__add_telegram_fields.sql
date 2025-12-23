-- V4__add_telegram_fields.sql
set search_path to recruiting;

alter table users
    add column if not exists telegram_chat_id bigint;

alter table candidates
    add column if not exists telegram_chat_id bigint;