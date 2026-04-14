[artifact:FrontendCode]
status: READY
owner: senior-frontend-engineer
scope:
- 信息化项目管理一期前端最小实现切片：项目权限配置入口与交互
inputs:
- [artifact:ImplementationPlan] `docs/artifacts/project-management-frontend-implementation-plan.md`
- [artifact:APIDoc] `docs/artifacts/project-management-api-doc.md`
- [artifact:Approval] `docs/artifacts/project-management-approval.md`
deliverables:
- 项目管理页新增“权限配置”页签
- 前端支持项目权限列表、矩阵摘要、新增、修改、删除
- 权限页签可复用项目台账当前选中项目作为上下文
risks:
- 权限页签目前是单文件内联实现，后续继续叠加功能时维护成本会升高
- 未提供独立项目检索器，使用前需要先在项目台账中选择项目
handoff_to:
- code-reviewer
- senior-qa-engineer
exit_criteria:
- 前端生产构建通过
changed_files:
- `ruoyi-ui/src/api/information/project.js`
- `ruoyi-ui/src/views/information/project/index.vue`
contracts_affected:
- `/information/project/{projectId}/permissions*`
- `/information/project/{projectId}/permission-matrix`
tests_run:
- `npm run build:prod`
known_issues:
- 项目扩展字段仅保留后端兼容，前端项目表单尚未完整展开所有新增字段
