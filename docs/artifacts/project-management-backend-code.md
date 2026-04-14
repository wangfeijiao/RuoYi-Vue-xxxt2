[artifact:BackendCode]
status: READY
owner: senior-backend-engineer
scope:
- 信息化项目管理一期后端最小实现切片：项目扩展字段、项目权限关系与项目验收工单兼容逻辑
inputs:
- [artifact:ImplementationPlan] `docs/artifacts/project-management-backend-implementation-plan.md`
- [artifact:DBDesign] `docs/artifacts/project-management-db-design.md`
- [artifact:APIDoc] `docs/artifacts/project-management-api-doc.md`
- [artifact:Approval] `docs/artifacts/project-management-approval.md`
deliverables:
- 项目主表支持模板版本、空间状态、归档状态、资料完备度、最近协同时间等扩展字段
- 新增项目权限关系实体、Mapper、服务与控制器接口
- 项目验收工单强制收口为 `PROJECT + ACCEPTANCE` 兼容语义
risks:
- SQL 基线补的是最小可运行增量，目录/文档/协同/评审子表仍待后续阶段落地
- `information_management.sql` 使用 `IF NOT EXISTS` 增量语法，需与实际数据库版本核对
handoff_to:
- code-reviewer
- security-engineer
- senior-qa-engineer
exit_criteria:
- 后端单元测试通过
- `ruoyi-admin` 联编通过
changed_files:
- `ruoyi-system/src/main/java/com/ruoyi/system/domain/information/InfoProject.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/domain/information/InfoProjectPermissionRel.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/mapper/information/InfoProjectPermissionRelMapper.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/information/IInfoProjectPermissionService.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/information/impl/InfoProjectPermissionServiceImpl.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/information/impl/InfoProjectServiceImpl.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/information/impl/InfoWorkOrderServiceImpl.java`
- `ruoyi-system/src/main/resources/mapper/information/InfoProjectMapper.xml`
- `ruoyi-system/src/main/resources/mapper/information/InfoProjectPermissionRelMapper.xml`
- `ruoyi-admin/src/main/java/com/ruoyi/web/controller/information/InfoProjectController.java`
- `sql/information_management.sql`
- `ruoyi-system/src/test/java/com/ruoyi/system/service/information/impl/InfoProjectServiceImplTest.java`
- `ruoyi-system/src/test/java/com/ruoyi/system/service/information/impl/InfoProjectPermissionServiceImplTest.java`
- `ruoyi-system/src/test/java/com/ruoyi/system/service/information/impl/InfoWorkOrderServiceImplTest.java`
contracts_affected:
- `GET /information/project/{projectId}/permissions`
- `POST /information/project/{projectId}/permissions`
- `PUT /information/project/{projectId}/permissions/{permissionId}`
- `DELETE /information/project/{projectId}/permissions/{permissionId}`
- `GET /information/project/{projectId}/permission-matrix`
- `inf_project`
- `inf_project_permission_rel`
- `inf_work_order` 中 `PROJECT + ACCEPTANCE` 的服务层约束
tests_run:
- `mvn -pl ruoyi-system "-Dtest=InfoProjectServiceImplTest,InfoProjectPermissionServiceImplTest,InfoWorkOrderServiceImplTest" test`
- `mvn -pl ruoyi-admin -am -DskipTests compile`
known_issues:
- 项目空间、项目文档、项目协同、验收评审明细与统计接口尚未在本轮实现
