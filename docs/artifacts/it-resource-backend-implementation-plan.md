[artifact:ImplementationPlan]
status: READY
owner: senior-backend-engineer
scope:
- 补强 IT 资源管理模块后端状态机、唯一性校验、删除策略与工单校验
inputs:
- [artifact:SystemArch] docs/artifacts/it-resource-system-arch.md
- [artifact:DBDesign] docs/artifacts/it-resource-db-design.md
- [artifact:APIDoc] docs/artifacts/it-resource-api-doc.md
- [artifact:TaskBreakdown] docs/artifacts/it-resource-task-breakdown.md
- [artifact:Approval] docs/artifacts/it-resource-approval.md
handoff_to:
- code-reviewer
- senior-qa-engineer
goal:
- 在不推翻现有资源模块骨架的前提下，把资源域从 CRUD 型实现提升为受控状态流转实现
changed_areas:
- `ruoyi-system/src/main/java/com/ruoyi/system/service/information/impl`
- `ruoyi-system/src/main/java/com/ruoyi/system/mapper/information`
- `ruoyi-system/src/main/resources/mapper/information`
- `ruoyi-system/src/test/java/com/ruoyi/system/service/information/impl`
steps:
- 为资源台账补充资源编码唯一性与活动资源 IP 冲突校验
- 为资源删除补充工单引用保护
- 为资源工单补充类型约束、必填校验、快照生成与删除约束
- 为资源工单审批与执行补充中间状态回写和执行校验
- 补充资源域后端单元测试并执行 Maven 验证
risks:
- 当前仍未引入逻辑删除字段，删除策略以“引用保护 + 物理删除”过渡实现
validation:
- `mvn -pl ruoyi-admin -am test`
