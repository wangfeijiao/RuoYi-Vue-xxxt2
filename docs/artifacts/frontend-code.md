[artifact:FrontendCode]
status: READY
owner: senior-frontend-engineer
scope:
- 交付信息化管理模块看板、项目、应用、资源、数据资产、网络安全前端页面
inputs:
- [artifact:DesignSpec]
- [artifact:UICode]
- [artifact:InteractionSpec]
- [artifact:APIDoc]
- [artifact:TaskBreakdown]
- [artifact:ImplementationPlan]
- [artifact:Approval]
deliverables:
- 新增信息化模块 API 封装文件
- 交付看板总览页与五大业务域管理页
- 项目、资源、数据资产、网络安全页面支持工单发起、审批、执行
- 数据资产页面支持版本记录查看
risks:
- 当前工单关联对象以 ID 手输为主，后续可补充选择器与下拉联动优化体验
handoff_to:
- code-reviewer
- senior-qa-engineer
exit_criteria:
- 前端生产构建通过，页面与后端接口可完成 CRUD 与工单闭环操作
changed_files:
- `ruoyi-ui/src/api/information/dashboard.js`
- `ruoyi-ui/src/api/information/project.js`
- `ruoyi-ui/src/api/information/application.js`
- `ruoyi-ui/src/api/information/resource.js`
- `ruoyi-ui/src/api/information/dataAsset.js`
- `ruoyi-ui/src/api/information/network.js`
- `ruoyi-ui/src/views/information/dashboard/index.vue`
- `ruoyi-ui/src/views/information/project/index.vue`
- `ruoyi-ui/src/views/information/application/index.vue`
- `ruoyi-ui/src/views/information/resource/index.vue`
- `ruoyi-ui/src/views/information/dataAsset/index.vue`
- `ruoyi-ui/src/views/information/network/index.vue`
contracts_affected:
- 对接 `/information/*` 新增接口
- 新增信息化模块菜单对应组件路径
tests_run:
- `npm run build:prod`
known_issues:
- None
