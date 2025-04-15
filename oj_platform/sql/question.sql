create table oj_question
(
    question_id   bigint            not null comment '题目id',
    title         varchar(20)       not null comment '标题',
    difficulty    tinyint           not null comment '1简单2中等3困难',
    time_limit    int               not null comment '时间限制',
    space_limit   int               not null comment '空间限制',
    content       varchar(1000)     not null comment '题目内容',
    question_case varchar(1000)     not null comment '测试用例',
    default_code  varchar(1000)     not null comment '默认代码块',
    main_func     varchar(1000)     not null comment 'main函数',

    created_by    bigint unsigned   not null comment '创建人',
    create_time   datetime          not null comment '创建时间',
    updated_by    bigint unsigned comment '修改人',
    update_time   datetime comment '修改时间',
    delete_flag   tinyint DEFAULT 0 not null comment '0未删除1回收站2已删除',

    primary key (question_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目列表';


INSERT INTO oj_question (
    question_id, title, difficulty, time_limit, space_limit,
    content, question_case, default_code, main_func,
    created_by, create_time, updated_by, update_time, delete_flag
) VALUES
-- 题目1: 两数之和
(1, '两数之和', 1, 1000, 256,
 '给定一个整数数组，找到两个数使它们的和等于目标值',
 '[{"input":"[2,7,11,15] 9","output":"[0,1]"}]',
 'def twoSum(nums, target):',
 'print(twoSum([2,7,11,15], 9))',
 1, NOW(), NULL, NULL, 0),

-- 题目2: 反转字符串
(2, '反转字符串', 1, 1000, 256,
 '将输入的字符串反转输出',
 '[{"input":"hello","output":"olleh"}]',
 'def reverseString(s):',
 'print(reverseString("hello"))',
 1, NOW(), NULL, NULL, 0),

-- 题目3: 斐波那契数列
(3, '斐波那契数列', 2, 2000, 512,
 '计算第n个斐波那契数',
 '[{"input":"5","output":"5"}]',
 'def fibonacci(n):',
 'print(fibonacci(5))',
 1, NOW(), 2, NOW(), 0),

-- 题目4: 判断素数
(4, '判断素数', 2, 1500, 256,
 '判断给定数字是否为素数',
 '[{"input":"7","output":"true"},{"input":"4","output":"false"}]',
 'def isPrime(num):',
 'print(isPrime(7))',
 2, NOW(), NULL, NULL, 0),

-- 题目5: 冒泡排序
(5, '冒泡排序', 2, 3000, 512,
 '实现冒泡排序算法',
 '[{"input":"[5,3,8,1]","output":"[1,3,5,8]"}]',
 'def bubbleSort(arr):',
 'print(bubbleSort([5,3,8,1]))',
 2, NOW(), NULL, NULL, 0),

-- 题目6: 二叉树深度
(6, '二叉树深度', 3, 2500, 512,
 '计算二叉树的最大深度',
 '[{"input":"[3,9,20,null,null,15,7]","output":"3"}]',
 'class TreeNode:\n    def __init__(self, val=0, left=None, right=None):\n        self.val = val\n        self.left = left\n        self.right = right\n\ndef maxDepth(root):',
 '# 测试代码需先构建树',
 3, NOW(), 3, NOW(), 0),

-- 题目7: 字符串转整数
(7, '字符串转整数', 3, 2000, 256,
 '实现atoi函数，将字符串转换为整数',
 '[{"input":"42","output":"42"},{"input":"   -42","output":"-42"}]',
 'def myAtoi(s):',
 'print(myAtoi("42"))',
 3, NOW(), NULL, NULL, 0),

-- 题目8: 删除链表节点
(8, '删除链表节点', 3, 2000, 512,
 '删除链表中的指定节点',
 '[{"input":"[4,5,1,9] 5","output":"[4,1,9]"}]',
 'class ListNode:\n    def __init__(self, x):\n        self.val = x\n        self.next = None\n\ndef deleteNode(node):',
 '# 测试代码需先构建链表',
 4, NOW(), NULL, NULL, 0),

-- 题目9: 爬楼梯
(9, '爬楼梯', 2, 1500, 256,
 '每次可以爬1或2个台阶，计算爬到n阶的方法数',
 '[{"input":"3","output":"3"}]',
 'def climbStairs(n):',
 'print(climbStairs(3))',
 4, NOW(), 4, NOW(), 0),

-- 题目10: 已删除的测试题目
(10, '旧题目', 1, 1000, 128,
 '已废弃的测试题目',
 '[{"input":"test","output":"test"}]',
 'def oldFunc():',
 'print(oldFunc())',
 1, '2023-01-01', 1, '2023-06-01', 1);