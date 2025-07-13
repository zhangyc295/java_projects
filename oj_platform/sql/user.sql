create table oj_user
(
    user_id       bigint unsigned not null comment '主键id',

    nick_name     varchar(20)  comment '昵称',
    user_name     varchar(20)  comment '用户名',
    user_password varchar(60)  comment '密码',

    head_image    varchar(100) comment '头像',
    gender        tinyint comment '性别1男2女',
    telephone     char(11)    not null comment '手机号',
    code          char(6) comment '验证码',
    email         varchar(30) comment '邮箱',
    school        varchar(20) comment '学校',
    major         varchar(20) comment '专业',
    introduction  varchar(50) comment '个人介绍',
    status        tinyint     not null comment '账号状态 1正常2拉黑',

    created_by    bigint unsigned not null comment '创建人',
    create_time   datetime    not null comment '创建时间',
    updated_by    bigint unsigned comment '修改人',
    update_time   datetime comment '修改时间',

    primary key (user_id),
    unique key (user_name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 插入5条用户数据到oj_user表
INSERT INTO oj_user (user_id, nick_name, user_name, user_password,
                     head_image, gender, telephone, code, email,
                     school, major, introduction, status,
                     created_by, create_time)
VALUES (1, '系统测试员', 'admin', '123456',
        'admin_avatar.jpg', 1, '13800138000', NULL, 'admin@school.edu',
        '清华大学', '计算机科学与技术', '系统管理员账号', 1,
        1, '2023-01-01 00:00:00'),
       (2, '编程小王子', 'student1', '123456',
        'stu1_avatar.jpg', 1, '13912345678', '123456', 'student1@school.edu',
        '北京大学', '软件工程', '热爱算法竞赛', 1,
        1, '2023-02-15 09:30:00'),
       (3, '电路设计师', 'student2', '123456',
        'stu2_avatar.jpg', 2, '13598765432', '654321', 'student2@school.edu',
        '浙江大学', '电子信息工程', '喜欢硬件设计', 1,
        1, '2023-03-10 10:15:00'),
       (4, '数学教授', 'teacher1', '123456',
        'teacher_avatar.jpg', 1, '13687654321', NULL, 'math@school.edu',
        '复旦大学', '应用数学', '数学竞赛指导教师', 1,
        1, '2023-04-05 08:00:00'),
       (5, '语言达人', 'student3', '123456',
        'stu3_avatar.jpg', 2, '13712348765', '987654', 'student3@school.edu',
        '上海交通大学', '英语', '擅长翻译', 1,
        1, '2023-05-20 13:45:00'),
       (6, '张教授', 'zhang', '666666',
        NULL, 1, '13812345678', NULL, 'zhang@edu.cn',
        '', '控制工程', '博士生导师', 1,
        1, '2024-05-20 13:45:00' );