[artifact:ImplementationPlan]
status: READY
owner: senior-backend-engineer
scope:
- 信息化项目管理一期后端最小实现切片：项目主表增量字段、项目权限关系表、权限查询/新增/修改/删除/矩阵接口、项目验收工单兼容收口
inputs:
- [artifact:PRD] `docs/artifacts/project-management-prd.md`
- [artifact:Prototype] `docs/artifacts/project-management-prototype.md`
- [artifact:SystemArch] `docs/artifacts/project-management-system-arch.md`
- [artifact:DBDesign] `docs/artifacts/project-management-db-design.md`
- [artifact:APIDoc] `docs/artifacts/project-management-api-doc.md`
- [artifact:TaskBreakdown] `docs/artifacts/project-management-task-breakdown.md`
- [artifact:Approval] `docs/artifacts/project-management-approval.md`
deliverables:
- 扩展 `inf_project` 领域模型、Mapper 与 SQL，支持模板版本、空间初始化、归档、资料完备度、最近协同时间等字段
- 新增 `inf_project_permission_rel` 的领域模型、Mapper、服务与控制器接口
- 兼容保留 `PROJECT + ACCEPTANCE` 工单语义，不引入破坏性 requestType 变更
handoff_to:
- code-reviewer
- security-engineer
- senior-qa-engineer
exit_criteria:
- 后端代码已实现并通过针对性单元测试
- 新增接口与字段符合已审批的 APIDoc/DBDesign
- 已输出对应的 [artifact:BackendCode]
goal:
- 在不破坏现有项目管理 CRUD 与验收工单链路的前提下，落地项目主数据扩展和项目权限配置能力
changed_areas:
- `sql/information_management.sql`
- `ruoyi-system/src/main/java/com/ruoyi/system/domain/information/*`
- `ruoyi-system/src/main/java/com/ruoyi/system/mapper/information/*`
- `ruoyi-system/src/main/resources/mapper/information/*`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/information/*`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/information/impl/*`
- `ruoyi-admin/src/main/java/com/ruoyi/web/controller/information/InfoProjectController.java`
- `ruoyi-system/src/test/java/com/ruoyi/system/service/information/impl/*`
steps:
- 为项目扩展字段和权限能力补齐失败测试，先锁定默认值、重复校验、矩阵聚合与工单兼容行为
- 扩展 `InfoProject`、`InfoProjectMapper` 与 MyBatis XML 映射，保证 list/get/add/edit 支持新增字段
- 新增 `InfoProjectPermissionRel` 的实体、Mapper、Mapper XML 与服务接口
- 在 `InfoProjectService` / `InfoProjectController` 中增加权限 CRUD 与矩阵查询接口
- 收口 `InfoWorkOrderServiceImpl` 对 PROJECT 域验收工单的创建与执行兼容逻辑
- 运行 Maven 单测验证，并根据结果修正实现
risks:
- 现有 `InfoProjectServiceImpl` 仅承担基础 CRUD，本次引入权限服务后需保持职责边界清晰
- SQL 基线文件与线上库可能不同步，需以增量脚本和可重复执行语句保证兼容
- 权限矩阵接口先返回按范围分组的聚合结构，暂不引入复杂继承计算引擎
- 项目详情聚合、目录/文档/协同/评审子表不在本轮实现，前端需要容忍权限接口先行落地
- 当前仓库存在应用管理模块未提交改动，本轮不得改动这些文件
validation:
- 运行 `mvn -pl ruoyi-system -Dtest=InfoProjectServiceImplTest,InfoWorkOrderServiceImplTest test`
- 如控制器/编译受影响，补充运行 `mvn -pl ruoyi-admin -am -DskipTests compile`
