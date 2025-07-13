-- 消息内容
create table oj_message_content
(
    content_id      bigint       not null comment '消息内容id',
    message_title   varchar(30)  not null comment '消息标题',
    message_content varchar(200) not null comment '消息内容',

    created_by      bigint unsigned   not null comment '创建人',
    create_time     datetime     not null comment '创建时间',
    updated_by      bigint unsigned comment '修改人',
    update_time     datetime comment '修改时间',

    primary key (content_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息内容表';

-- 消息对应
create table oj_message
(
    message_id  bigint   not null comment '消息id',
    content_id      bigint       not null comment '消息内容id',
    send_id     bigint   not null comment '发送人id（管理员）',
    receive_id  bigint   not null comment '接收人id（用户）',

    created_by  bigint unsigned   not null comment '创建人',
    create_time datetime not null comment '创建时间',
    updated_by  bigint unsigned comment '修改人',
    update_time datetime comment '修改时间',

    primary key (message_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息对应表';