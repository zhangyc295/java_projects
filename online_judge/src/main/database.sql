create
database if not exists java_oj_question charset utf8mb4;

use
java_oj_question;

drop table if exists oj_question;
create table oj_question
(
    id          int primary key auto_increment,
    title       varchar(20),
    level       varchar(10),
    description varchar(4096),
    initialCode varchar(4096),
    testCode    varchar(4096)
)CHARACTER SET = utf8mb4;


