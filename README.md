---
date: 2018-05-24 09:49
status: draft
title: 2018-05-24
---

功能介绍
=========
---------
**ticket** 是一套相对简单的工单系统；包含了流程设计器，表单设计器，权限管理、知识库等功能；
系统后端基于SpringMVC+Spring+Hibernate框架，前端页面采用JQuery+Bootstrap等主流技术；
流程引擎基于Snaker工作流；表单设计器基于雷劈网WEB表单设计器。
系统主要功能有：
 >1.系统管理
 >>系统管理包含有：基础信息管理、系统权限管理、版本管理、子系统管理。
 >
 >2.流程管理
 >>流程管理包含有：流程设计器、流程实例管理、流程页面模版管理等功能。
 >
 >3.表单管理
 >>表单管理包含有：表单设计器、表管理、表单帮助信息管理等。
 >
 >4.我的办公
 >>我的待办、我的已办;

使用说明
=======
-------
---数据库MySQL5.6以上 <br/>
---下载后把mysql目录下的脚本文件（“ticket2018513.sql”）导入到mysql数据库中；注：建库时，字符集编码为：utf8（utf8_general_ci）<br/>
---修改配置文件“jdbc.properties”，改成对应数据库的用户名和密码 <br/>
---“sysconfig.properties”系统配置文件；需要修改“root.dir”属性，设置为系统上传文件时用来存放的根目录，git的配置属性修改，设置你自己的git用于知识库的文档和图片上传 <br/>
---系统跑起来后，还需要修改配置信息表的信息，包括系统邮件、域账号同步、云之家接口等配置，不需要的可以关闭启用状态
----系统管理员用户名为：admin；密码为：123456 <br/>
----linux类系统需要修改mysql的配置文件，改为数据库表名不区分大小写（lower_case_table_names=1） <br />
环境要求
------------
1.jdk要求1.7及以上；<br />
2.tomcat6或tomcat7； <br />
3.eclipse版本4.4以上；<br />
4.浏览器要求：IE8及以上（最理想的是IE10及以上），火狐，chrome等。<br />

版本说明
----------
1.0 为第一稳定版本（支持表单单独使用）和知识库功能<br />
master 为开发版本 <br />
下一版本为：1.1.0（预计新增简单自定义报表功能）<br />

系统截图
=========
---------
 1.首页-显示左菜单栏，常看文档和文档搜索页面，右上角是一些小的系统设置，左上可以显示版本信息和当前一线值班人
 
![首页](https://github.com/shuaiqixiaoyumi/ticket/blob/master/image/09-52-02.jpg)

![](https://github.com/shuaiqixiaoyumi/ticket/blob/master/image/09-50-58.jpg)


![](https://github.com/shuaiqixiaoyumi/ticket/blob/master/image/09-54-48.jpg)

 <br />
 2.资源管理 - 设置对应的资源链接，分表单资源，流程资源和其他资源。表单资源和流程资源是在流程管理和表单管理里生成的，其他资源是对应的后台请求 ，选则该资源所需的操作<br />

![](https://github.com/shuaiqixiaoyumi/ticket/blob/master/image/09-56-35.jpg)
![](https://github.com/shuaiqixiaoyumi/ticket/blob/master/image/10-07-46.jpg)

<br />
 3.菜单管理 - 选择对应的资源设置，后面设置好权限，就会在左边菜单树出现了 <br />

![](https://github.com/shuaiqixiaoyumi/ticket/blob/master/image/11-05-14.jpg)

 <br />
 4.权限管理和角色管理- 创建角色，选择菜单、资源操作按钮、文档管理的文档，文档按钮权限
 
![](https://github.com/shuaiqixiaoyumi/ticket/blob/master/image/11-17-00.jpg)


![](https://github.com/shuaiqixiaoyumi/ticket/blob/master/image/11-21-29.jpg)

 <br />
 5.流程管理-设计过程：流程设计器设置流程-》设置流程菜单资源。流程实例管理，可以查看所有的流程进度，并进行修改删除跳转，一般只开放给管理员

![](https://github.com/shuaiqixiaoyumi/ticket/blob/master/image/11-23-22.jpg)
![](~/11-23-42.jpg)

 <br />
 6.表单管理- 设计过程：表管理设计数据库表单-》表单设计器画表格和字段插件，讲字段对应上-》表单资源添加。表单设计器用的是百度霹雳网的插件。

![](https://github.com/shuaiqixiaoyumi/ticket/blob/master/image/11-39-11.jpg)
![](https://github.com/shuaiqixiaoyumi/ticket/blob/master/image/11-38-51.jpg)
7、我的办公 - 用户的代办事项和已办事项

![](https://github.com/shuaiqixiaoyumi/ticket/blob/master/image/11-41-18.jpg)
8、文档管理- 首页新增的文档，都会在对应的文档书上挂载，得在角色列表的有对应文档权限才能显示，按钮也是如此

![](https://github.com/shuaiqixiaoyumi/ticket/blob/master/image/11-42-30.jpg)

-------

 [操作文档](https://github.com/shuaiqixiaoyumi/ticket/blob/master/image/ticket.pdf "操作文档")
 
-------

技术交流
========
--------
QQ群：544479131