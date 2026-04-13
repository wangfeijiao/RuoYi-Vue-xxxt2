[artifact:ImplementationPlan]
status: READY
owner: senior-backend-engineer
scope:
- 后端实现信息化管理一期 MVP 的 SQL、实体、Mapper、Service、Controller 与测试
inputs:
- [artifact:SystemArch]
- [artifact:DBDesign]
- [artifact:APIDoc]
- [artifact:TaskBreakdown]
- [artifact:Approval]
handoff_to:
- code-reviewer
- security-engineer
- senior-qa-engineer
goal:
- 完成五大业务域后台接口、通用工单状态机和聚合看板接口
changed_areas:
- `sql`
- `ruoyi-admin/src/main/java/com/ruoyi/web/controller/information`
- `ruoyi-system/src/main/java/com/ruoyi/system/domain/information`
- `ruoyi-system/src/main/java/com/ruoyi/system/mapper/information`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/information`
- `ruoyi-system/src/main/resources/mapper/information`
- `ruoyi-system/src/test/java`
steps:
- 新增业务表与菜单初始化 SQL
- 新增领域对象、Mapper、XML
- 实现 CRUD、审批、执行、版本快照、看板聚合接口
- 补充后端测试并执行 Maven 验证
risks:
- 通用工单 JSON 字段依赖前后端约定
- 不同台账状态回写要保持一致
validation:
- `mvn -pl ruoyi-system -am test`
- 必要时补充 `mvn -pl ruoyi-admin -am test`
