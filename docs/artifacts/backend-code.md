[artifact:BackendCode]
status: READY
owner: senior-backend-engineer
scope:
- 交付 IT 资源、应用、信息化项目、数据资产、网络安全五大域后端实现与测试
inputs:
- [artifact:SystemArch]
- [artifact:DBDesign]
- [artifact:APIDoc]
- [artifact:TaskBreakdown]
- [artifact:ImplementationPlan]
- [artifact:Approval]
deliverables:
- 新增五大业务域台账表、工单表、数据资产版本表及菜单权限初始化 SQL
- 新增信息化模块领域对象、Mapper/XML、Service、Controller 与看板聚合接口
- 新增工单审批/执行状态流转逻辑与数据资产版本快照逻辑
- 新增 `InfoWorkOrderServiceImplTest` 与 `InfoDashboardServiceImplTest`
risks:
- `sql/information_management.sql` 中菜单初始化使用 `MAX(menu_id)+N`，重复执行时需人工确认避免重复菜单
handoff_to:
- code-reviewer
- security-engineer
- senior-qa-engineer
exit_criteria:
- 后端模块编译通过，核心单测通过，接口与数据库契约可供前端联调
changed_files:
- `sql/information_management.sql`
- `ruoyi-admin/src/main/java/com/ruoyi/web/controller/information`
- `ruoyi-system/src/main/java/com/ruoyi/system/domain/information`
- `ruoyi-system/src/main/java/com/ruoyi/system/mapper/information`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/information`
- `ruoyi-system/src/main/resources/mapper/information`
- `ruoyi-system/src/test/java/com/ruoyi/system/service/information/impl/InfoWorkOrderServiceImplTest.java`
- `ruoyi-system/src/test/java/com/ruoyi/system/service/information/impl/InfoDashboardServiceImplTest.java`
contracts_affected:
- 新增信息化管理业务表结构
- 新增 `/information/dashboard`
- 新增 `/information/project`
- 新增 `/information/resource`
- 新增 `/information/application`
- 新增 `/information/dataAsset`
- 新增 `/information/network`
tests_run:
- `mvn -pl ruoyi-admin -am test`
known_issues:
- None
