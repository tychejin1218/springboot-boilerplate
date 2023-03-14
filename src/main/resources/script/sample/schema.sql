/*
create database sample;

create user sample password 'password1!' superuser createdb;
*/

drop table if exists sample.todo;

drop table if exists sample.member;

create table sample.member (
    id int generated always as identity,
    name varchar(100),
    email varchar(100),
    primary key(id)
);

create table sample.todo (
    id int generated always as identity,
    member_id int not null,
    title varchar(255),
    description varchar(255),
    completed boolean,
    primary key(id),
    constraint fk_todo
        foreign key(member_id)
            references sample.member(id)
            on delete set null
);
