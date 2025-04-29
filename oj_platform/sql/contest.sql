create table oj_contest
(
    contest_id  bigint            not null comment '竞赛id',
    title       varchar(30)       not null comment '竞赛标题',
    start_time  datetime          not null comment '开始时间',
    end_time    datetime          not null comment '结束时间',
    status      tinyint default 0 not null comment '是否发布',

    created_by  bigint unsigned   not null comment '创建人',
    create_time datetime          not null comment '创建时间',
    updated_by  bigint unsigned comment '修改人',
    update_time datetime comment '修改时间',
    delete_flag tinyint DEFAULT 0 not null comment '0未删除1已删除',


    primary key (contest_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='竞赛表';

INSERT INTO oj_contest
(contest_id, title, start_time, end_time, status, created_by, create_time, delete_flag)
VALUES (1001, '春季算法赛', '2025-05-01 09:00:00', '2025-05-01 12:00:00', 0, 1, '2025-04-15 21:00:00', 0),
       (1002, '夏季编程挑战', '2025-06-10 14:00:00', '2025-06-10 17:00:00', 0, 1910940302738780161,
        '2025-04-15 21:01:00', 0),
       (1003, '秋季代码大赛', '2025-09-20 10:00:00', '2025-09-20 13:00:00', 0, 1910940302738780161,
        '2025-04-15 21:02:00', 0),
       (1004, '冬季编程杯', '2025-12-05 15:00:00', '2025-12-05 18:00:00', 0, 1, '2025-04-15 21:03:00', 0),
       (1005, '新年算法冲刺赛', '2026-01-01 10:00:00', '2026-01-01 13:00:00', 0, 1, '2025-04-15 21:04:00', 0),
       (1007, '2025算法赛', '2025-01-01 10:00:00', '2026-01-01 13:00:00', 1, 1, '2024-12-10 21:04:00', 0),
       (1008, '2024算法赛', '2024-01-01 10:00:00', '2025-01-01 13:00:00', 1, 1, '2023-12-31 09:00:00', 0);

create table contest_question_mapping
(
    contest_question_id bigint   not null comment '竞赛题目关系id',
    contest_id          bigint   not null comment '竞赛id',
    question_id         bigint   not null comment '题目id',
    question_order      int      not null comment '题目顺序',

    created_by          bigint unsigned   not null comment '创建人',
    create_time         datetime not null comment '创建时间',
    updated_by          bigint unsigned comment '修改人',
    update_time         datetime comment '修改时间',

    primary key (contest_question_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='竞赛题目关系表';



INSERT INTO contest_question_mapping
(contest_question_id, question_id, contest_id, question_order, created_by, create_time, updated_by, update_time)
VALUES (1, 101, 1001, 1, 1, '2025-04-15 21:00:00', 2, '2025-04-15 21:30:00'),
       (2, 102, 1001, 2, 1, '2025-04-15 21:01:00', 2, '2025-04-15 21:31:00'),
       (3, 103, 1001, 3, 1, '2025-04-15 21:02:00', 2, '2025-04-15 21:32:00'),
       (4, 104, 1002, 1, 1, '2025-04-15 21:03:00', 2, '2025-04-15 21:33:00'),
       (5, 105, 1002, 2, 1, '2025-04-15 21:04:00', 2, '2025-04-15 21:34:00');
