### git基础配置
#### 配置用户名
##### git config --global user.name "你的用户名"
#### 配置邮箱
##### git config --global user.email "你的邮箱"
#### 编码设置
##### 避免git gui 中的中文乱码
##### git config --global gui.encoding utf-8
##### 避免git status 显示的中文文件名乱码
##### git config --global core.quotepath off
#### 其他
##### git config --global core.ignorecase false
### git ssh key pair配置
#### 1 在git bash命令行窗口中输入
##### ssh-keygen -t rsa -C "你的邮箱"
#### 2 然后一路回车，不输入任何东西，生成ssh key pair
#### 3 在用户目录下生成.ssh文件夹，找到公钥和私钥 id_rsa id_rsa.pub
#### 4 将公钥的内容复制
#### 5 进入github网站，将公钥添加进去
### git验证
#### 显示版本信息
##### git --version
### git常用命令
#### 创建本地仓库
##### git init
#### 添加到暂存区
##### git add
#### 提交到本地仓库
##### git commit -m "描述"
#### 检查工作区文件状态
##### git status
#### 查看提交committid
##### git log
#### 版本回退
##### git reset --hard committid
#### 查看分支
##### git branch
#### 创建并切换到dev分支
##### git checkout -b dev
#### 切换分支
##### git checkout 分支名
#### 拉取
##### git pull
#### 提交
##### git push -u origin master
#### 分之合并
##### git merge branch name
#### 本地仓库与远程仓库关联
#####  git remote add origin "远程仓库地址"
#### 第一次向远程仓库推送
##### git push -u -f origin master
#### 以后提交到远程
##### git push origin master
#### 创建分支
##### git checkout -b v1.0 origin/master
#### 将分支推送到远程
##### git push origin HEAD -u
#### 提交所有
##### git add  .
### .gitignore文件：告诉Git哪些文件不需要添加到版本管理中
#### 忽略规则
##### *.a  忽略所有.a结尾的文件
##### ！lib.a  lib.a文件除外
##### /TODO  仅仅忽略项目根目录下的 TODO 文件，不包括 subdir/TODO
##### build/  忽略 build/ 目录下的所有文件
##### doc/*.txt  忽略 doc/notes.txt 但不包括 doc/server/arch.txt
------------20181204------------
###远程分支合并dev分支
#### git checkout dev
#### git pull origin dev
#### git checkout master
#### git merge dev
#### git push origin master

### 数据库设计
#### 创建数据库
```
create database ilearnshopping;
```
#### 用户表
```
create table neuedu_user(
`id`             int(11)   not null auto_increment  comment '用户id',
`username`       varchar(50) not null  comment '用户名',
`password`       varchar(50) not null  comment '密码',
`email`          varchar(50) not null  comment '邮箱',
`phone`          varchar(11) not null  comment '联系方式',
`question`       varchar(100) not null comment '密保问题',
`answer`         varchar(100) not null comment '答案',
`role`           int(4)       not null default 0 comment '用户角色',
`create_time`    datetime     comment '创建时间',
`update_time`    datetime     comment '修改时间',
PRIMARY KEY (`id`),
UNIQUE KEY `user_name_index`(`username`) USING BTREE
)ENGINE=InnoDB DEFAULT CHARSET=UTF8;
```
#### 类别表
```
create table neuedu_category(
`id`           int(11)  not null   auto_increment  comment '类别id',
`parent_id`    int(11)  not null  default 0 comment '父类id',
`name`         varchar(50)  not null  comment '类别名称',
`status`       int(4)  default 1 comment '类别状态 1:正常 0:废弃',
`create_time`    datetime     comment '创建时间',
`update_time`    datetime     comment '修改时间',
PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

                   id    parent_id
电子产品    1       1        0
家电        2       2        1
手机        2       3        1
电脑        2       4        1
相机        2       5        1
华为手机    3       6        3
小米手机    3       7        3
p系列       4       8        6
mate系列    4       9        6

查询电子产品的商品----->递归

```
#### 商品表
```
create table neuedu_product(
`id`           int(11)  not null   auto_increment  comment '商品id',
`category_id`  int(11)  not null   comment '商品所属的类别id，值引用类别表的id',
`name`         varchar(100)  not null  comment '商品名称',
`detail`       text      comment '商品详情',
`subtitle`     varchar(200)  comment '商品副标题',
`main_image`   varchar(100)  comment '商品主图',
`sub_images`   varchar(200)  comment '商品子图',
`price`        decimal(20,2) not null  comment '商品的价格，总共20位，小数2位，整数18位',
`stock`        int(11)   comment '商品库存',
`status`       int(6)  default  1  comment '商品状态 1:在售 2:下架 3:删除',
`create_time`    datetime     comment '创建时间',
`update_time`    datetime     comment '修改时间',
PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8;
```

#### 购物车表
```
create table neuedu_cart(
`id`           int(11)  not null   auto_increment  comment '购物车id',
`user_id`      int(11)  not null  comment '用户id',  
`product_id`   int(11)  not null  comment '商品id',
`quantity`     int(11)  not null  comment '购买数量',
`checked`      int(4)   default 1 comment '1:选中 0:未选中',
`create_time`    datetime     comment '创建时间',
`update_time`    datetime     comment '修改时间',
PRIMARY KEY (`id`),
key `user_id_index`(`user_id`) USING BTREE
)ENGINE=InnoDB DEFAULT CHARSET=UTF8;
```

#### 订单表
```
create table neuedu_order(
`id`            int(11)  not null  auto_increment comment '订单id，主键',
`order_no`      bigint(20)  not null comment '订单编号',  
`user_id`       int(11)  not null  comment '用户id',
`payment`       decimal(20,2) not null  comment '付款总金额，单位元，保留两位小数',
`payment_type`  int(4)   not null default 1 comment '支付方式 1:线上支付',
`status`        int(10)    not null  comment '订单状态 0-已取消 10-未付款 20-已付款 30-已发货 40-已完成 50-已关闭',
`shipping_id`   int(11)  not null comment '收货地址id',
`postage`       int(10)  not null default 0 comment '运费',
`payment_time`  datetime  default  null  comment '已付款时间',
`send_time`     datetime  default  null  comment '已发货时间',
`close_time`    datetime  default  null  comment '已关闭时间',
`end_time`      datetime  default  null  comment '已结束时间',
`create_time`   datetime  default  null  comment '已创建时间',
`update_time`   datetime  default  null  comment '更新时间',
PRIMARY KEY (`id`),
UNIQUE KEY `order_no_index`(`order_no`) USING BTREE
)ENGINE=InnoDB DEFAULT CHARSET=UTF8;
```

#### 订单明细表

```
create table neuedu_order_item(
`id`            int(11)  not null  auto_increment comment '订单明细id，主键',
`order_no`      bigint(20)  not null comment '订单编号',  
`user_id`       int(11)  not null  comment '用户id',
`product_id`    int(11)  not null  comment '商品id',
`product_name`  varchar(100)  not null  comment '商品名称',
`product_image` varchar(100)  comment '商品主图',
`current_unit_price`  decimal(20,2)  not null comment '下单时商品的价格，元为单位，保留两位小数',
`quantity`      int(10)  not null  comment '商品的购买数量',
`total_price`   decimal(20,2)  not null comment '商品的总价格，元为单位，保留两位小数',
`create_time`   datetime  default  null  comment '已创建时间',
`update_time`   datetime  default  null  comment '更新时间',
PRIMARY KEY (`id`),
 KEY `order_no_index`(`order_no`) USING BTREE,
 KEY `order_no_user_id_index`(`order_no`,`user_id`) USING BTREE
)ENGINE=InnoDB DEFAULT CHARSET=UTF8;
```

#### 支付表

```
create table neuedu_payinfo(
`id`            int(11)  not null  auto_increment comment '主键',
`user_id`       int(11)  not null  comment '用户id',
`order_no`      bigint(20)  not null comment '订单编号',  
`pay_platform`  int(4)  not null  default 1 comment '1:支付宝 2:微信',
`platform_status`  varchar(50)  comment '支付状态',
`platform_number`  varchar(100) comment '流水号',
`create_time`   datetime  default  null  comment '已创建时间',
`update_time`   datetime  default  null  comment '更新时间',
PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8;
```

#### 地址表

```
create table neuedu_shipping(
`id`            int(11)  not null  auto_increment comment '主键',
`user_id`       int(11)  not null,
`receiver_name` varchar(20)  default  null  comment '收货姓名',
`receiver_phone` varchar(20)  default  null  comment '收货固定电话',
`receiver_mobile` varchar(20)  default  null  comment '收货移动电话',
`receiver_province` varchar(20)  default  null  comment '省份',
`receiver_city` varchar(20)  default  null  comment '城市',
`receiver_district` varchar(20)  default  null  comment '区/县',
`receiver_address` varchar(200)  default  null  comment '详细地址',
`receiver_zip` varchar(6)  default  null  comment '邮编',
`create_time`   datetime   not null  comment '创建时间',
`update_time`   datetime   not null  comment '更新时间',
PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8;
```
### 项目架构---四层架构

```
视图层
控制层controller(接收视图层传递的数据，负责去调用业务逻辑层，处理业务逻辑，值通过控制层返回给视图层)
业务逻辑层service(处理业务逻辑，负责具体的业务逻辑，业务逻辑层去调用dao层)
    接口和实现类
dao层(主要是和数据库做交互，负责对数据库的操作，对数据的增删改查)

```

### mybatis-generator插件
```
可以一键生成dao接口、实体类和映射文件
```

### 搭建ssm框架
#### 依赖的jar包
```
junit单元测试
mysql驱动包
```

### 知识点
```
mybatis：半自动化框架
jdbc.username:加上jdbc前缀为防止默认加载系统的username
@RestController:往前端返回的数据是json格式的
```