create table trade(id serial primary key ,
  meta jsonb default '{}'::jsonb,
  content jsonb not null,
  saved timestamp default now()
)