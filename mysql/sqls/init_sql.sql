/* 
유저 관련 테이블
user, tutor, tutee
user_mail, tutor_mail, tutee_mail
tutor_speciality
*/

CREATE DATABASE myapp IF NOT EXISTS myapp;

USE myapp; 

CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    contact VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    profile VARCHAR(1000),
    created_at DATETIME(6),
    modified_at DATETIME(6),
    dtype VARCHAR(31) NOT NULL
);

CREATE TABLE IF NOT EXISTS tutor (
    id BIGINT PRIMARY KEY,
    authentication_process ENUM('AUTHENTICATION_DENIED', 'AUTHENTICATION_ONGOING', 'AUTHENTICATION_SUCCESS') NOT NULL,
    authentication_address VARCHAR(255) NOT NULL,
    self_description VARCHAR(255),
    FOREIGN KEY (id) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS tutee (
    id BIGINT PRIMARY KEY,
    nick_name VARCHAR(255) UNIQUE,
    FOREIGN KEY (id) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS user_mail (
    id BIGINT PRIMARY KEY,
    created_at DATETIME(6),
    expiration_date_time DATETIME(6) NOT NULL,
    modified_at DATETIME(6),
    dtype VARCHAR(31) NOT NULL,
    code VARCHAR(255) NOT NULL,
    contact VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS tutor_mail (
    id BIGINT PRIMARY KEY REFERENCES user_mail(id),
    authentication_address VARCHAR(255) NOT NULL,
    self_description VARCHAR(1000),
    specialities VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS tutee_mail (
    id BIGINT PRIMARY KEY REFERENCES user_mail(id)
);

CREATE TABLE IF NOT EXISTS tutor_speciality (
    id BIGINT PRIMARY KEY,
    created_at DATETIME(6),
    modified_at DATETIME(6),
    tutor_id BIGINT NOT NULL,
    specialities ENUM('BASS','COMPOSITION','DRUM','GUITAR','KEYBOARD_INSTRUMENT','MUSIC_THEORY','PIANO','STRING_INSTRUMENT','VOCAL','WIND_INSTRUMENT') NOT NULL,
    FOREIGN KEY (tutor_id) REFERENCES tutor(id)
);

/* 
커뮤니티 관련 테이블
community
post, picture, bookmark, choose, thread
banned, report, thread_report, post_report
*/

CREATE TABLE IF NOT EXISTS community (
    id bigint NOT NULL AUTO_INCREMENT,
    created_at datetime(6),
    modified_at datetime(6),
    name varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS post (
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    community_id bigint NOT NULL REFERENCES community(id),
    created_at datetime(6),
    modified_at datetime(6),
    user_id bigint NOT NULL,
    view bigint NOT NULL,
    text varchar(3000) NOT NULL,
    title varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS picture (
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    created_at datetime(6),
    modified_at datetime(6),
    post_id bigint NOT NULL REFERENCES post(id),
    address varchar(255) NOT NULL
);


CREATE TABLE IF NOT EXISTS bookmark (
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    created_at datetime(6),
    modified_at datetime(6),
    post_id bigint NOT NULL REFERENCES post(id),
    user_id bigint NOT NULL
);

CREATE TABLE IF NOT EXISTS choose (
    created_at datetime(6),
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    modified_at datetime(6),
    post_id bigint NOT NULL REFERENCES post(id),
    user_id bigint NOT NULL
);

CREATE TABLE IF NOT EXISTS thread (
    created_at datetime(6) DEFAULT NULL,
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    modified_at datetime(6) DEFAULT NULL,
    parent_id bigint DEFAULT NULL REFERENCES thread(id),
    post_id bigint NOT NULL REFERENCES post(id),
    user_id bigint NOT NULL,
    text varchar(500) NOT NULL
);

CREATE TABLE IF NOT EXISTS banned (
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    created_at datetime(6),
    modified_at datetime(6),
    until_when datetime(6) NOT NULL,
    user_id bigint NOT NULL UNIQUE,
    reason varchar(255)
);

CREATE TABLE IF NOT EXISTS report (
    created_at datetime(6) DEFAULT NULL,
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    modified_at datetime(6) DEFAULT NULL,
    user_id bigint NOT NULL,
    dtype varchar(31) NOT NULL,
    reason varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS post_report (
    id bigint NOT NULL PRIMARY KEY REFERENCES report(id),
    post_id bigint NOT NULL UNIQUE REFERENCES post(id)
);

CREATE TABLE IF NOT EXISTS thread_report (
    id bigint NOT NULL PRIMARY KEY REFERENCES report(id),
    thread_id bigint NOT NULL UNIQUE REFERENCES thread(id)
);