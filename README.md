# ERP
在原基础上整合了MyBatis。

### 效果预览

1.登录界面。用户名admin，密码123456

2.因为pojp中Employee中有个属性是Department类，在写xml的时候使用<association>来处理多对一（多个员工，对应一个部门）
     
3.Date老是处理不好，查了感觉也没有很好的解决办法，估计还是得从前端下手

### 创建数据库
```MySQL
CREATE DATABASE `springboot`;

USE `springboot`;

/*Table structure for table `department` */

DROP TABLE IF EXISTS `department`;

CREATE TABLE `department` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `DepartmentName` varchar(255) NOT NULL COMMENT '部门名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

/*Data for the table `department` */

insert  into `department`(`id`,`DepartmentName`) values 
(1,'财政部'),
(2,'财政部'),
(3,'农业部'),
(4,'教育部'),
(5,'外交部');

/*Table structure for table `employee` */

DROP TABLE IF EXISTS `employee`;

CREATE TABLE `employee` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `lastName` varchar(255) NOT NULL COMMENT '员工姓名',
  `email` varchar(255) NOT NULL COMMENT '员工邮箱',
  `gender` int(2) NOT NULL COMMENT '员工性别',
  `departmentId` int(10) NOT NULL COMMENT '部门编号',
  `birth` date NOT NULL COMMENT '入职日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;

/*Data for the table `employee` */

insert  into `employee`(`id`,`lastName`,`email`,`gender`,`departmentId`,`birth`) values 
(1,'习近平','zhangsan@gmail.com',0,1,'1953-06-15'),
(2,'江泽民','lisi@qq.com',1,2,'1926-08-17'),
(3,'胡锦涛','wangwu@126.com',0,3,'1942-12-21'),
(4,'习仲勋','zhaoliu@163.com',1,4,'1913-10-15'),
(5,'江青','tianqi@foxmail.com',0,3,'1914-03-19'),
(6,'林彪','wangwei@gmail.com',1,3,'1907-12-05'),
(7,'毛泽东','zhangwei@gmail.com',1,2,'1893-12-26'),
(8,'李伟','liwei@gmail.com',1,3,'2020-02-18'),
(28,'fuckyou','123@qq.com',1,1,'1989-12-31');

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL COMMENT '密码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `user` */

insert  into `user`(`id`,`username`,`password`) values 
(1,'admin','123456');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
```
