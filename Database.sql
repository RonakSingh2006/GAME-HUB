create database gamehub;
use gamehub;

create table userdata(
username varchar(10) primary Key not null,
password varchar(10) not null);

create table gamedata(
username varchar(10) primary Key not null,
snake_highscore int  default 0,
flappyBird_highscore int default 0,
trex_highscore int default 0,
brickBreaker_highscore int default 0,
snake_time INT DEFAULT 0,           
flappyBird_time INT DEFAULT 0,
trex_time INT DEFAULT 0,
brickBreaker_time INT DEFAULT 0,
tiktaktoe_time INT DEFAULT 0,
pong_time INT DEFAULT 0);