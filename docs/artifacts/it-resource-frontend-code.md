[artifact:FrontendCode]
status: READY
owner: senior-frontend-engineer
scope:
- 交付 IT 资源管理页面对齐优化
inputs:
- [artifact:ImplementationPlan] docs/artifacts/it-resource-frontend-implementation-plan.md
- [artifact:Approval] docs/artifacts/it-resource-approval.md
deliverables:
- 资源页状态口径与后端统一
- 资源台账新增筛选项与资源详情弹窗
- 资源工单新增筛选项与动态校验规则
- 执行默认状态根据工单类型自动切换
risks:
- 资源与项目选择仍以直接输入 ID 为主，易用性仍有提升空间
handoff_to:
- code-reviewer
- senior-qa-engineer
exit_criteria:
- 页面行为与后端规则一致，前端构建通过
changed_files:
- `ruoyi-ui/src/views/information/resource/index.vue`
contracts_affected:
- `/information/resource/*`
- `/information/resource/order/*`
tests_run:
- `npm run build:prod`
known_issues:
- None
