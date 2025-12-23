set search_path to recruiting;

insert into users(full_name, login, password_hash, role_id)
select 'Директор', 'director', '$2a$10$EibXgydcMe/iEapM151z..rCow7JRi9yYpmYHWwuwrJCLJ/O/9xp.', r.id
from roles r where r.name = 'director'
on conflict (login) do nothing;

insert into users(full_name, login, password_hash, role_id)
select 'Тимлид', 'teamlead', '$2a$10$GAMuvbjD3Lb4PWDsI4BUpe6kSpALPPe00.DBNyInZvf8iru1KZHle', r.id
from roles r where r.name = 'teamlead'
on conflict (login) do nothing;

insert into users(full_name, login, password_hash, role_id)
select 'ПМ', 'pm', '$2a$10$iRoqUP.qa6TLRDklfy3.feDzuz25/ot2SltBnof2HtkYn8Zx8b/1W', r.id
from roles r where r.name = 'pm'
on conflict (login) do nothing;
