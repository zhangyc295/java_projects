create table oj_answer
(

    submit_id        bigint unsigned not null comment '提交记录主键id',
    question_id      bigint unsigned not null comment '题目id',
    user_id          bigint unsigned not null comment '用户id',
    contest_id       bigint unsigned          comment '竞赛id',
    program_language tinyint  not null comment '0 java 1 cpp 2 python ...',
    submit_code      text     not null comment '提交代码',
    pass             tinyint  not null comment '0未通过 1通过',
    error_message    varchar(500) comment '执行结果',
    score            int      not null default 0 comment '题目得分',
    case_judge_result  varchar(1000) comment '测试用例结果',

    created_by       bigint unsigned not null comment '创建人',
    create_time      datetime not null comment '创建时间',
    updated_by       bigint unsigned comment '修改人',
    update_time      datetime comment '修改时间',

    primary key (submit_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题提交表';