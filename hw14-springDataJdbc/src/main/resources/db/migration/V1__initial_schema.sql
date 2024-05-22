create table client
(
    id   bigserial not null primary key,
    name varchar(50)
);

create table address
(
    id        bigserial not null primary key,
    client_id bigserial,
    street    varchar(250)
);

create table phone
(
    id        bigserial not null primary key,
    client_id bigserial,
    number    varchar(50)
);

