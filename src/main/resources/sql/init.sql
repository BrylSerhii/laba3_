create table if not exists users
(
    id         bigserial primary key,
    email      text unique,
    password   text,
    full_name  text,
    role       text,
    math_score int not null default 100,
    ukr_score  int not null default 100,
    eng_score  int not null default 100
);

create table if not exists faculty
(
    id          bigserial primary key,
    name        text  default 'unnamed faculty',
    math_weight float default 0.34,
    ukr_weight  float default 0.33,
    eng_weight  float default 0.33,
    capacity    int,
    draft_finished boolean default false

);

create table if not exists application
(
    id         bigserial primary key,
    user_id    bigint references users (id),
    faculty_id bigint references faculty (id),
    avg_score  float,
    priority   int
)