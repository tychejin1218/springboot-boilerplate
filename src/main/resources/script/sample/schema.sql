/*
create database sample;

create user sample password 'password1!' superuser createdb;
*/

drop table if exists todo;

drop table if exists member;

create table member (
    id int generated always as identity,
    password varchar(100),
    name varchar(50),
    email varchar(50) UNIQUE,
    role varchar(50),
    primary key(id)
);

create table todo (
    id int generated always as identity,
    member_id int not null,
    title varchar(255),
    description varchar(255),
    completed boolean,
    primary key(id),
    constraint fk_todo
        foreign key(member_id)
            references member(id)
            on delete set null
);
