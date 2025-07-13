create table user_contest_mapping
(
    user_contest_id bigint unsigned  not null comment '用户竞赛关系id',
    user_id         bigint unsigned  not null comment '用户id',
    contest_id      bigint unsigned  not null comment '竞赛id',
    score           int unsigned  comment '得分',
    contest_rank    int unsigned  comment '排名',

    created_by      bigint unsigned  not null comment '创建人',
    create_time     datetime not null comment '创建时间',
    updated_by      bigint unsigned comment '修改人',
    update_time     datetime comment '修改时间',

    primary key (user_contest_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户竞赛关系表';