/*
PostgreSQL

create database sample;
create user sample password 'password1!' superuser createdb;

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
*/

/*
MySQL

CREATE DATABASE sample;
CREATE USER 'sample'@'%' IDENTIFIED BY 'password1!';
GRANT ALL PRIVILEGES ON *.* TO 'sample'@'%' WITH GRANT OPTION;

DROP TABLE IF EXISTS todo;
DROP TABLE IF EXISTS member;

CREATE TABLE member (
    id INT PRIMARY KEY AUTO_INCREMENT,
    password VARCHAR(100),
    name VARCHAR(50),
    email VARCHAR(50) UNIQUE,
    role VARCHAR(50)
);

CREATE TABLE todo (
    id INT PRIMARY KEY AUTO_INCREMENT,
    member_id INT,
    title VARCHAR(255),
    description VARCHAR(255),
    completed BOOLEAN,
    CONSTRAINT fk_todo
        FOREIGN KEY(member_id)
            REFERENCES member(id)
            ON DELETE SET NULL
);
 */
