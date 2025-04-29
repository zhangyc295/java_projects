create table oj_admin
(

    admin_id       bigint unsigned not null comment '主键id',
    nick_name      varchar(20) not null comment '昵称',
    admin_account  varchar(20) not null comment '账号',
    admin_password varchar(60) not null comment '密码',

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


INSERT INTO oj_platform.oj_admin
(admin_id, admin_account, admin_password, created_by, create_time, updated_by, update_time, nick_name)
VALUES (1, 'admin', '$2a$10$glJrSepWy995k/4wZx86/.yuGLQ3Sboi01q7sPQRgJreR4r6RMYzm',
        0, '2025-04-08 21:33:53', NULL, NULL, '超级管理员1');
INSERT INTO oj_platform.oj_admin
(admin_id, admin_account, admin_password, created_by, create_time, updated_by, update_time, nick_name)
VALUES (1910940302738780161, 'admin2', '$2a$10$nHIQcgT5WQ6i3lvmv20GT.dx2svXSigQGWxgvRjVBClmJEuIEE.mO',
        100, '2025-04-12 14:17:26', NULL, NULL, '超级管理员2');