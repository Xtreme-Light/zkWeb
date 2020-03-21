# zookeeper图形化展示网站

提供zookeeper的图形化展示，支持节点详细信息查看，支持查看digest加密节点，集群信息查看，新建zk节点，删除节点。  
主要供给给运维以及开发人员，协助开发，以及核查ZK问题。

## 核心栈

**后台**  
[springboot][springboot]  
[zookeeper][zookeeper]  
[Orika][Orika]  bean转换jar 工具

**前台**  
[bootstrap][bootstrap]   
[bootstrap-table][bootstrap-table]    
[zTree][zTree] zTree 是一个依靠 jQuery 实现的多功能 “树插件”。  
[thymeleaf][thymeleaf] springboot 官方推荐使用的前段模版引擎  
[bootstrap-dialog][bootstrap-dialog] 一个好用的模态框
[clipboardJS][clipboardJS] 剪贴板 JS工具
[handlebars][handlebars] 前端使用的模版语言工具

[handlebars]:https://handlebarsjs.com/installation/#npm-or-yarn-recommended
[orika]:https://orika-mapper.github.io/orika-docs/
[zookeeper]:http://zookeeper.apache.org/
[clipboardjs]:https://clipboardjs.com/
[springboot]:https://spring.io/projects/spring-boot
[bootstrap-dialog]:https://github.com/nakupanda/bootstrap3-dialog
[bootstrap]:https://www.bootcss.com/ 
[bootstrap-table]:https://bootstrap-table.com/
[jQuery Growl]:http://ksylvest.github.io/jquery-growl/  
[zTree]:http://www.treejs.cn/v3/main.php#_zTreeInfo
[thymeleaf]:https://www.thymeleaf.org/