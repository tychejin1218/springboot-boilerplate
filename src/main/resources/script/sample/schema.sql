CREATE DATABASE sample;
CREATE USER 'sample'@'%' IDENTIFIED BY 'password1!';
GRANT ALL PRIVILEGES ON *.* TO 'sample'@'%' WITH GRANT OPTION;

DROP TABLE IF EXISTS todo;
DROP TABLE IF EXISTS member;

CREATE TABLE member (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    password VARCHAR(100),
    name VARCHAR(50),
    email VARCHAR(50) UNIQUE,
    role VARCHAR(50)
);

CREATE TABLE todo (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT,
    title VARCHAR(255),
    description VARCHAR(255),
    completed BOOLEAN,
    CONSTRAINT fk_todo
        FOREIGN KEY(member_id)
            REFERENCES member(id)
            ON DELETE SET NULL
);
