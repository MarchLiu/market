create table status
(
  id      serial primary key ,
  meta    jsonb     default '{}'::jsonb,
  content jsonb not null,
  save_at timestamp default now()
);
insert into status(content) values('{"latest-order-id":0, "bids":[], "asks":[], "status":"trading"}');