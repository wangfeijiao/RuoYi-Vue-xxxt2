[artifact:ImplementationPlan]
status: READY
owner: senior-backend-engineer
scope:
- 应用管理第一阶段后端实现（主表扩展、依赖关系、通知记录、运行概览、基础统计）
inputs:
- [artifact:TaskBreakdown] docs/artifacts/application-task-breakdown.md
- [artifact:SystemArch] docs/artifacts/application-system-arch.md
- [artifact:DBDesign] docs/artifacts/application-db-design.md
- [artifact:APIDoc] docs/artifacts/application-api-doc.md
- [artifact:Approval] docs/artifacts/application-approval.md
handoff_to:
- code-reviewer
- security-engineer
- senior-qa-engineer
goal:
- 在不破坏既有 `information:application:*` CRUD 的前提下，交付应用管理一期后端能力并可被前端联调
changed_areas:
- `sql/` 应用管理一期增量脚本
- `ruoyi-system` 应用领域对象、Mapper、Service
- `ruoyi-admin` 应用控制器接口扩展
- `docs/artifacts/application-backend-code.md`
steps:
- 新增 MySQL 增量脚本：扩展 `inf_application` 字段，新增应用项目关系表、依赖关系表、告警事件表、通知记录表，并完成历史 `project_id` 回填
- 扩展 `InfoApplication` 主模型和 MyBatis 映射，补充筛选与返回字段，保证旧 CRUD 保持兼容
- 新增项目关系、依赖关系、告警/通知的数据模型与 Mapper SQL
- 在 `IInfoApplicationService` / `InfoApplicationServiceImpl` 实现参数校验、权限内写操作、关系维护、状态重算、通知闭环、统计聚合
- 在 `InfoApplicationController` 增加 detail/projects/dependencies/status-overview/alerts/notices/statistics 接口，写操作复用 `information:application:edit`
- 执行本地 SQL 应用与后端构建验证
risks:
- 现有前端若未同步新接口，短期仅可使用原 CRUD；新能力需前端逐步接入
- 依赖状态一期采用“关系表 + 联动重算”补偿机制，未接入真实事件总线，存在时效性窗口
- `project_id` 与关系表双写场景依赖服务层保证一致性，需后续联调回归重点关注
validation:
- `mysql` 执行增量脚本（本地 `ry-vue-xxxt2`）
- `mvn -pl ruoyi-admin -am test -DskipTests`
