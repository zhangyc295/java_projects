create
database if not exists java_gobang charset utf8;

use
java_gobang;
drop table if exists user;
create table user
(
    userId     int primary key auto_increment,
    username   varchar(50) unique,
    password   varchar(50),
    score      int,
    total_game int,
    win_game   int
);

insert into user(userId, username, password, score, total_game, win_game)
values (null, 'zj', '123', 500, 0, 0);
insert into user(userId, username, password, score, total_game, win_game)
values (null, 'zyc', '123', 500, 0, 0);
