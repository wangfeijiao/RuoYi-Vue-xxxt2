[artifact:ImplementationPlan]
status: READY
owner: senior-frontend-engineer
scope:
- 信息化项目管理一期前端最小实现切片：项目权限配置页签、权限接口调用、项目工单与项目选择联动
inputs:
- [artifact:PRD] `docs/artifacts/project-management-prd.md`
- [artifact:Prototype] `docs/artifacts/project-management-prototype.md`
- [artifact:APIDoc] `docs/artifacts/project-management-api-doc.md`
- [artifact:TaskBreakdown] `docs/artifacts/project-management-task-breakdown.md`
- [artifact:Approval] `docs/artifacts/project-management-approval.md`
deliverables:
- 扩展 `ruoyi-ui/src/api/information/project.js`，补齐项目权限查询/新增/修改/删除/矩阵接口
- 在 `ruoyi-ui/src/views/information/project/index.vue` 中新增“权限配置”页签与最小 CRUD 交互
- 保持现有项目台账、模板管理、验收工单三类页签可继续使用
handoff_to:
- code-reviewer
- senior-qa-engineer
exit_criteria:
- 前端构建通过
- 权限配置页签可以基于当前选中项目完成最小查询与 CRUD 操作
goal:
- 在不拆分现有项目管理入口页面的前提下，提供可用的项目权限配置入口
changed_areas:
- `ruoyi-ui/src/api/information/project.js`
- `ruoyi-ui/src/views/information/project/index.vue`
steps:
- 补齐项目权限相关 API 封装
- 在现有项目管理页中增加权限配置页签与权限矩阵摘要
- 复用当前选中项目作为权限配置上下文，避免新增独立路由
- 增加授权对话框，支持范围、对象、读写下载删除和继承标记录入
- 通过生产构建验证 Vue 模板与脚本语法
risks:
- 当前页签仍是单文件承载，后续空间/文档/统计等功能继续叠加时建议拆组件
- 权限页签目前依赖项目台账中先选中项目，尚未提供独立项目选择器
validation:
- 运行 `npm run build:prod`
