create table oj_admin
(

    admin_id       bigint unsigned not null comment '主键id',
    admin_account  varchar(20) not null comment '账号',
    admin_password varchar(20) not null comment '密码',

    created_by     bigint unsigned not null comment '创建人',
    create_time    datetime    not null comment '创建时间',
    updated_by     bigint unsigned comment '修改人',
    update_time    datetime comment '修改时间',

    primary key (admin_id),
    unique key (admin_account)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

INSERT INTO oj_admin (admin_id, admin_account, admin_password, created_by, create_time, updated_by, update_time)
VALUES (1, 'admin01', '123456', 0, CURRENT_TIMESTAMP,
        NULL, NULL);