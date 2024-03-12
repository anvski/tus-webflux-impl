create schema if not exists main;

create table if not exists main.file
(
    id             BIGSERIAL primary key,
    file_size      BIGINT default 0,
    mime_type      TEXT not null,
    content_offset BIGINT default 0,
    name           TEXT not null,
    uuid           UUID   default gen_random_uuid(),
    meta_data      TEXT
);

create table main.users
(
    id       BIGSERIAL primary key,
    username TEXT not null,
    email    TEXT not null,
    password TEXT not null
);

create table if not exists main.file_requests
(
    id           BIGSERIAL primary key,
    file_id      BIGINT references main.file (id),
    request_data JSONB,
    user_id      BIGINT references main.users (id)
);