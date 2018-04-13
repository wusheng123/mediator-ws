tag V3.4.1
--修改价格进度问题

tag V3.4.0
--新增医院订单同步至CRM

tag V3.3.9
-日志打印医院name

tag V3.3.8
-订单状态是已预约/导出错误的可再导

tag V3.3.7
-删除报告接口

tag V3.3.6
-判断accomplishorder为空

tag V3.3.5
-散客单位拆分

tag V3.3.4
-套餐改造

tag V3.3.3
-查询新单位表获取散客别名

tag V3.3.2
-订单去mongo、退款接口改造

tag V3.3.1
-监控日志

tag V3.3.0
-体检报告模板内容迁出

tag V3.2.8
-套餐同步，去掉无hismealid不更新同步状态的逻辑
-报告同步，过滤无体检日期的报告

tag V3.2.7
crm导入 排序修改

tag V3.2.6
ext 修改

tag V3.2.5
报告同步操作修改为批量更新、删除

tag V3.2.4 本次更新 pom3.1.6
-医院任务列表增加按省份查询

tag V3.2.3 本次更新 pom3.1.6
-netty排包
-订单导出接口加执行时间日志

tag V3.2.2 本次更新 pom3.1.6
-订单重构
-为ops提供dubbo服务

tag V3.2.1 本次更新 pom3.1.6
-导出时检查订单状态，已预约的才导出

tag V3.2.0 本次更新 pom3.1.6
-修复订单消息队列异常退出问题
-对接dubbo化
-加扩展字段

tag V3.1.9 本次更新 pom3.1.5
-修复体检报告图片
-修复手动导出订单数量限制

tag V3.1.8 本次更新 pom3.1.5
-勤方打印加打印中状态
-修复大项科室排序字段为空
-增加手动收集大项科室信息，访问地址http://ip:port/mediator-ws/action/report/tpl/{syncExamreportId},{syncExamreportId}参数为tb_sync_exam_report.id

tag V3.1.7 本次更新 pom3.1.4
-报告手动导入

tag V3.1.6 本次更新 pom3.1.3
-勤方打印

tag V3.1.5 本次更新 pom3.1.2
-极速预约导出优化，去除mq
-套餐同步服务端加上性别

tag V3.1.4 本次更新 pom3.1.1
-修复删除状态的套餐同步出错

tag V3.1.3 本次更新 pom3.1.1
-服务端告警订单优化

tag V3.1.2 本次更新 pom3.1.1
-订单导出排序
-去掉数据库联众engine配置


tag V3.1.1 本次更新 pom3.1.0
-fix 市二单位映射空指针

tag V3.1.0 本次更新 pom3.1.0
-新单位同步，套餐同步
-task任务管理页面

tag V3.0.6 本次更新
-netty包冲突

tag V3.0.5 本次更新
-hotfix 体检报告重复

tag V3.0.4 本次更新
-hotfix 获取报告科室和大项service加上事务

tag V3.0.3 本次更新
-服务端从体检报告中获取大项和科室信息，为报告模板准备数据。
-新增获取tasks webservice接口，agent执行的任务可在数据库中配置。

tag V3.0.2 本次更新
-新增天方达engine，传batchid

tag V3.0.1 本次更新
-fix bug，不同步价格的单项，导出时设置价格为0

tag V3.0.0 本次更新
-接收报告时，使用redis缓存去重



