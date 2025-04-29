CREATE database if NOT EXISTS `oj_platform`;

use oj_platform;

CREATE TABLE `oj_test` (
                           `test_id` bigint unsigned NOT NULL,
                           `title` text NOT NULL,
                           `content` text NOT NULL,
                           PRIMARY KEY (`test_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



INSERT INTO oj_test values(1,'test','test');
select * from oj_test;
update oj_test set title = 'test_update' where test_id = 1;
delete from oj_test;


