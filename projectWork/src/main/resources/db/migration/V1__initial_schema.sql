create table favourite
(
    id        bigserial not null primary key,
    chat_id   bigserial,
    film_name varchar(250),
    film_year varchar(4),
    film_id   varchar(16)
);