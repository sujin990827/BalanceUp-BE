drop table if exists "user" cascade;

create table "user"
(
    id          int8        not null,
    create_at   timestamp,
    deleted_at  timestamp,
    modified_at timestamp,
    nickname    varchar(20) not null,
    password    varchar(50) not null,
    username    varchar(50) not null,
    primary key (id)
)