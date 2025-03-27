CREATE DATABASE sample;
CREATE USER 'sample'@'%' IDENTIFIED BY 'password1!';
GRANT ALL PRIVILEGES ON sample.* TO 'sample'@'%' WITH GRANT OPTION;

DROP TABLE IF EXISTS todo;
DROP TABLE IF EXISTS member;

CREATE TABLE member (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '회원 ID (자동 증가값)',
    email VARCHAR(50) UNIQUE COMMENT '회원 이메일 (유일값)',
    password VARCHAR(100) COMMENT '회원 비밀번호 (암호화된 해시값 저장)',
    name VARCHAR(50) COMMENT '회원 이름',
    role VARCHAR(50) COMMENT '회원 역할 (예: 관리자, 일반 사용자)'
) COMMENT='회원 정보 테이블';

CREATE TABLE todo (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '할 일 ID (자동 증가값)',
    member_id BIGINT COMMENT '회원 ID (할 일을 추가한 회원)',
    title VARCHAR(255) COMMENT '할 일 제목',
    description TEXT COMMENT '할 일 내용',
    completed BOOLEAN DEFAULT 0 NOT NULL COMMENT '할 일 완료 여부 (true: 완료, false: 미완료)',
    CONSTRAINT fk_todo_member
        FOREIGN KEY(member_id)
            REFERENCES member(id)
            ON DELETE SET NULL
) COMMENT='회원의 할 일 정보 테이블';
