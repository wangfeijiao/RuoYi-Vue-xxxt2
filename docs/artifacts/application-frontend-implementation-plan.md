[artifact:ImplementationPlan]
status: READY
owner: senior-frontend-engineer
scope:
- 应用管理第一阶段前端实现（主列表、统计卡片、详情页签、项目关联、依赖关系、状态概览、告警/通知闭环）
inputs:
- [artifact:TaskBreakdown] docs/artifacts/application-task-breakdown.md
- [artifact:DesignSpec] None
- [artifact:UICode] None
- [artifact:APIDoc] docs/artifacts/application-api-doc.md
- [artifact:Approval] docs/artifacts/application-approval.md
handoff_to:
- code-reviewer
- senior-qa-engineer
goal:
- 在沿用 RuoYi-Vue + Element UI 交互模式的前提下，交付应用管理一期可联调页面并兼容既有 CRUD 能力
changed_areas:
- `ruoyi-ui/src/views/information/application/index.vue`
- `ruoyi-ui/src/api/information/application.js`
- `docs/artifacts/application-frontend-code.md`
steps:
- 基于既有应用管理 CRUD 页面恢复列表、查询、增删改基础能力，并补充建设来源、联动状态、告警数量等一期字段
- 新增顶部统计卡片，接入 `/statistics/overview` 返回的总量、联动风险、告警应用等聚合数据
- 新增详情弹窗与页签，接入 detail/projects/dependencies/status-overview/alerts/notices 接口，形成“看详情-管关系-看概览-处理告警”的闭环
- 实现项目关系新增/删除、依赖关系新增/编辑/删除、告警确认/恢复等写操作，并与本地列表刷新联动
- 执行前端构建与本地页面 smoke 验证
risks:
- 当前阶段无独立 DesignSpec/UICode，上线样式遵循现有信息化管理模块通用视觉语言
- 网络资源暂无独立前端 API，网络类依赖先支持手工录入或按已有 ID 录入
- 一期详情页签不实现依赖拓扑图和外部通知渠道配置，仅呈现结构化记录
validation:
- `npm install`（如依赖未安装）
- `npm run build:prod`
- 联调校验：新增应用、维护项目关系、维护依赖、查看概览、确认/恢复告警
