[artifact:ImplementationPlan]
status: READY
owner: senior-frontend-engineer
scope:
- 前端实现总览、项目、应用、资源、数据、网络五个页面及其工单交互
inputs:
- [artifact:DesignSpec]
- [artifact:UICode]
- [artifact:InteractionSpec]
- [artifact:APIDoc]
- [artifact:TaskBreakdown]
- [artifact:Approval]
handoff_to:
- code-reviewer
- senior-qa-engineer
goal:
- 完成信息化管理页面、表单、审批与执行交互，并与后端接口联通
changed_areas:
- `ruoyi-ui/src/api/information`
- `ruoyi-ui/src/views/information`
steps:
- 新增业务 API 文件
- 新增六个页面
- 接入状态标签、审批弹窗、执行弹窗和分页查询
- 完成生产构建验证
risks:
- 页面数量较多，需控制重复代码
- 长文本和 JSON 信息展示需要保证可读性
validation:
- `npm run build:prod`
