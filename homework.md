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
配置mysql的驱动包
配置数据表
可以一键生成dao接口、实体类和映射文件
```

### 搭建ssm框架
#### 依赖的jar包
```
junit单元测试
mysql驱动包
mybatis jar包
mybatis-generator依赖包
mybatis和spring整合jar包
数据库连接池c3p0
spring-context：spring核心依赖
spring-web和spring-webmvc：spring web项目的依赖
spring-tx：事务管理的依赖
spring-jdbc：jdbc的依赖
jstl：jstl依赖的jar包
日志框架Logback的依赖
json解析的依赖
guava缓存的依赖包
mybatis-pagehelper 分页插件
joda-time 时间格式化
commons-fileupload和commons-io：图片上传
alipay 集成支付宝需要的依赖包
```

### 知识点
```
索引加在数值类型上：bigint对应java中的long类型(加索引时字段的类型只能是数值类型)
mybatis：半自动化框架
jdbc.username:加上jdbc前缀为防止默认加载系统的username
@RestController:往前端返回的数据是json格式的

@RequestParam(value = "username"，required = true) String username
value中的值与传入进来的参数保持一致（页面上直接输入的key值），当value值与形参值相同时,注解可以省略
required = true：代表参数必须要传递，可传可不传时设置成false
defaultValue = "zhangsan"：当username不传时默认为zhangsan
springmvc的对象绑定
在java中不确定类型用泛型表示
commons-lang jar包提供了常用的效验方法(如：StringUtils.isBlank()和StringUtils.isEmpty())
使用StringUtils.isBlank()方法认为"  "为空(引号中有空格认为为空)``````
使用StringUtils.isEmpty()方法(字符串为空或者长度为零)
数据库中当前时间用now()表示
```

#### 项目大体结构
```

对数据库中的密码通过MD5加密
    public static String getMD5Code(String strObj) {
    String resultString = null;
    try {
      resultString = new String(strObj);
      MessageDigest md = MessageDigest.getInstance("MD5");
      // md.digest() 该函数返回值为存放哈希值结果的byte数组
      resultString = byteToString(md.digest(strObj.getBytes()));
    } catch (NoSuchAlgorithmException ex) {
      ex.printStackTrace();
    }
    return resultString;
  }
调用getMD5Code方法对密码加密
MD5加密特点：不可逆的加密算法(通过MD5加密变成密文之后，不能通过密文获取到原密码)
注册密码变成密文的之后，登录密码也应进行加密，否则登录时输入明文密码会显示密码输入错误


定义响应状态码(枚举类型enum)：
    PARAM_EMPTY(2,"参数为空"),
    EXISTS_USERNAME(3,"用户名已经存在"),
    EXISTS_EMAIL(4,"邮箱已经存在"),
    NOT_EXISTS_USERNAME(5,"用户名不存在"),
    USER_NOT_LOGIN(6,"用户未登录"),
参数：status(状态)、msg(消息)
提供有参和无参的构造函数以及set和get方法


注册接口：
注册接口中的参数：String username、String password、String email、String phone、String question、String answer
多参数用数据绑定接收(对象形式)
Impl实现类实现Service中的注册方法，实现类处理注册的业务逻辑，控制层调用业务逻辑层把数据返回给视图层
步骤一：参数非空的校验
步骤二：判断用户名是否存在
步骤三：判断邮箱是否存在
在dao层中书写校验邮箱的方法(与数据库做交互)，映射类中写sql语句，实现类处理具体的业务逻辑
步骤四：注册
步骤五：返回结果


用户只有登录之后才能对购物车进行操作，购物车接口如何判断用户已经登录？
把用户登录的信息保存到session中
往session中保存数据：session.setAttribute(Const.CURRENTUSER,serverResponse.getData())
Const.CURRENTUSER 代表用户表中的字段名
serverResponse.getData() 代表字段中所对应的值
通过HttpSession获取session当中的用户信息


检查用户名或邮箱是否有效的接口
参数：String str、String type，str对应用户名或邮箱的值，type对应的是username或email
什么时候使用？注册接口在输入用户名或邮箱之后会反馈用户名或邮箱是否已存在，输入完成之后通过ajax异步加载调用接口返回数据，防止恶意调用接口
在service中定义检查用户名和密码的方法，在实现类中处理业务逻辑
步骤一：参数的非空校验
通过调用StringUtils.isBlank()判断
步骤二：判断用户名或邮箱是否存在
步骤三：返回结果


重构登录和注册接口
登录的步骤二检查用户名是否存在调用检查用户名或邮箱是否有效接口中的check_valid()


获取当前登录的用户的信息
登录之后用户信息保存到了session当中，直接操作session获取信息
如何判断对象的类型是否是某一类型：使用instanceof
```