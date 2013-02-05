## CloudPaster
是一个基于Playframework框架在线问答系统。

## 截图

![首页](http://cloudpaster.com/public/upload/pqdGOnxqTyZptnBTl.png "首页")

## 如何安装
1. 下载playframwork-1.2.4.zip
2. 检出本项目代码

   git clone git://github.com/nile/CloudPaster.git

3. 配置数据库
   打开conf/ebean.properties修改数据库配置
4. 初始化数据库
   mysql -uuser -ppassword dbname < default-create.sql
5. 启动
   play run
6. 访问 http://localhost:9000
