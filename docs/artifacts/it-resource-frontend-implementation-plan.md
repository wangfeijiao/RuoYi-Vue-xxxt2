[artifact:ImplementationPlan]
status: READY
owner: senior-frontend-engineer
scope:
- 对齐 IT 资源管理页面与最新资源域产品、架构和后端约束
inputs:
- [artifact:DesignSpec]
- [artifact:UICode]
- [artifact:InteractionSpec]
- [artifact:APIDoc] docs/artifacts/it-resource-api-doc.md
- [artifact:TaskBreakdown] docs/artifacts/it-resource-task-breakdown.md
- [artifact:Approval] docs/artifacts/it-resource-approval.md
handoff_to:
- code-reviewer
- senior-qa-engineer
goal:
- 让资源台账与资源工单页面的状态、类型、校验规则和后端保持一致
changed_areas:
- `ruoyi-ui/src/views/information/resource/index.vue`
steps:
- 收口资源状态、监控状态、工单类型、工单状态选项
- 增加资源台账筛选项和详情展示
- 调整资源工单表单校验，支持申请工单资源可空、变更回收必填
- 增加工单查询条件和执行状态默认值
- 执行前端生产构建验证
risks:
- 资源工单仍以 ID 手输为主，后续可进一步补充选择器提升可用性
validation:
- `npm run build:prod`
