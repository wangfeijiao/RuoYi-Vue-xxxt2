[artifact:TaskBreakdown]
status: READY
owner: tech-lead-architect
scope:
- 将“信息化项目管理”模块的架构方案拆解为可直接进入审批与实施排期的后端、前端、联调、测试任务
- 明确现有代码基线下的优先级、依赖关系和阶段边界，避免实现层越权扩张
inputs:
- [artifact:PRD] `docs/artifacts/project-management-prd.md`
- [artifact:UserStory] `docs/artifacts/project-management-user-story.md`
- [artifact:Prototype] `docs/artifacts/project-management-prototype.md`
- [artifact:SystemArch] `docs/artifacts/project-management-system-arch.md`
- [artifact:DBDesign] `docs/artifacts/project-management-db-design.md`
- [artifact:APIDoc] `docs/artifacts/project-management-api-doc.md`
- 当前前端页面：`ruoyi-ui/src/views/information/project/index.vue`
- 当前前端 API：`ruoyi-ui/src/api/information/project.js`
- 当前控制器：`ruoyi-admin/src/main/java/com/ruoyi/web/controller/information/InfoProjectController.java`
- 当前持久层：`ruoyi-system/src/main/resources/mapper/information/InfoProjectMapper.xml`
- 当前持久层：`ruoyi-system/src/main/resources/mapper/information/InfoWorkOrderMapper.xml`
handoff_to:
- engineering-manager
- senior-backend-engineer
- senior-frontend-engineer
- senior-qa-engineer
deliverables:
- 面向前端、后端、联调、测试的任务拆解与依赖顺序
- 必须实现范围与明确延后范围
- 可供 `engineering-manager` 审批的实施粒度
risks:
- 现有前端项目页面是单页 tabs 结构，若直接拆成多页面会扩大回归面；建议先在现有入口内完成结构化分层
- 验收主单沿用 `inf_work_order` 时，历史工单与新验收工单需靠 `domain_type='PROJECT' + request_type='ACCEPTANCE'` 严格区分
exit_criteria:
- 任务粒度清晰、依赖顺序明确、阶段边界稳定，审批后实现层可直接据此输出 `ImplementationPlan`

## Entry Check

- 当前任务目标明确：输出“信息化项目管理”专属 `[artifact:TaskBreakdown]`
- 必需 artifact 齐备：产品、架构、数据库、接口产物均已齐备
- 本角色权限匹配：`tech-lead-architect` 负责任务拆解，不直接编码实现
- 上游审批前置不存在：当前处于 Architecture 阶段

## 1. 实施范围结论

### 1.1 本阶段必须实现

- 项目主档增量字段与列表/详情扩展
- 模板主档与模板快照复用
- 项目空间目录实例
- 项目文档实例、合规检查、归档状态
- 项目协同记录时间线
- 项目验收申请主单与多条线审核明细
- 项目级、目录级、文档级授权关系与权限配置页
- 项目统计分析摘要
- 现有 `/information/project/*` 路由兼容

### 1.2 明确延后

- 独立 BPM 引擎或会签引擎
- 对象存储上传直传方案
- 在线预览、全文检索
- 更复杂的授权规则引擎与跨模块统一 ACL 中心
- 跨模块资源主数据写回

## 2. 后端任务拆解

### BE-01 项目主表增量迁移

目标：

- 为 `inf_project` 增加模板、空间、归档、验收聚合字段。

依赖：

- 无

完成标准：

- SQL 可重复执行
- 历史数据有默认值策略
- 不破坏现有 `InfoProject` CRUD

### BE-02 扩展项目领域模型与 Mapper

目标：

- 扩展 `InfoProject` 实体、Mapper 映射、列表查询条件和详情返回字段。

依赖：

- `BE-01`

完成标准：

- `list/get/add/edit` 支持新增字段
- 旧调用方无需改动即可继续工作

### BE-03 项目空间目录实例建模

目标：

- 新建 `inf_project_space_directory`
- 实现目录初始化、目录查询、自定义目录新增能力

依赖：

- `BE-01`

完成标准：

- 可根据模板快照初始化目录实例
- 目录支持标准/自定义类型
- 目录状态可查询

### BE-04 项目文档建模与服务

目标：

- 新建 `inf_project_document`
- 实现文档实例新增、查询、合规检查、归档动作

依赖：

- `BE-03`

完成标准：

- 文档与目录实例正确关联
- 文档状态可独立维护
- 可聚合项目资料完备度

### BE-05 项目协同记录服务

目标：

- 新建 `inf_project_collaboration_record`
- 实现按阶段、类型、关联域记录协同留痕

依赖：

- `BE-01`

完成标准：

- 启动/执行/结项三阶段记录可独立查询
- 协同记录支持外部域对象关联
- 项目 `last_collaboration_time` 可更新

### BE-06 验收主单复用与主单语义收敛

目标：

- 在 `inf_work_order` 上明确项目验收主单语义
- 规范 `domain_type='PROJECT'`、`request_type='ACCEPTANCE'`

依赖：

- `BE-01`

完成标准：

- 项目验收主单与其他 `PROJECT` 域工单类型可区分
- 主单状态流转与项目 `acceptance_status` 口径一致
- 不引入与现有代码冲突的新 `request_type`

### BE-07 多条线审核明细服务

目标：

- 新建 `inf_project_acceptance_review`
- 实现线路初始化、线路审核、线路状态汇总

依赖：

- `BE-06`

完成标准：

- 一轮验收申请下可维护多条审核线
- 必审线路未完成时，主单不可汇总审批
- 审核留痕完整

### BE-08 权限关系表与权限服务

目标：

- 新建 `inf_project_permission_rel`
- 实现项目级、目录级、文档级授权查询、新增、修改、撤销

依赖：

- `BE-03`
- `BE-04`

完成标准：

- `scopeType=PROJECT/DIRECTORY/DOCUMENT` 授权可落库
- `targetType=USER/ROLE/TEAM` 可正确校验与查询
- 权限矩阵接口可为前端权限配置页一次性供数

### BE-09 详情聚合接口

目标：

- 提供项目详情聚合、项目空间摘要、协同摘要、验收摘要、权限摘要接口

依赖：

- `BE-02`
- `BE-04`
- `BE-05`
- `BE-07`
- `BE-08`

完成标准：

- 详情页不再依赖大 JSON 字段自行拼装
- 关键摘要字段一次请求可获取

### BE-10 统计接口与权限点菜单增量

目标：

- 提供项目总览、资料完备度、验收进度统计接口
- 增加 `information:project:detail/space/document/collaboration/review/permission/stats` 权限点

依赖：

- `BE-08`
- `BE-09`

完成标准：

- 管理员默认可授权
- 前端可通过 `v-hasPermi` 控制按钮和页签

## 3. 前端任务拆解

### FE-01 保留单页入口并做结构化重组

目标：

- 保留 `ruoyi-ui/src/views/information/project/index.vue` 作为入口
- 在现有 tabs 基础上拆出结构化详情视图或组件

依赖：

- `BE-09`

完成标准：

- 不更换模块入口路由
- 台账、模板、验收工单 tab 仍可访问
- 详情内容按页签结构化展示

### FE-02 扩展项目台账 tab

目标：

- 新增模板版本、项目空间初始化状态、归档状态、资料完备度等列与筛选项

依赖：

- `BE-02`

完成标准：

- 列表页可检索新增摘要字段
- 新建/编辑表单支持新增主档字段

### FE-03 项目空间页签

目标：

- 展示目录树、目录状态、文档列表
- 支持自定义目录新增与文档录入入口

依赖：

- `BE-03`
- `BE-04`

完成标准：

- 不再依赖手工编辑目录 JSON 查看空间结构
- 目录与文档状态可视化

### FE-04 项目协同页签

目标：

- 展示启动/执行/结项协同时间线
- 支持新增协同记录

依赖：

- `BE-05`

完成标准：

- 记录可按阶段和类型筛选
- 关联外部域对象摘要可见

### FE-05 验收管理页签与工单 tab 改造

目标：

- 在现有验收工单 tab 上增加审核明细视图
- 支持线路审核、汇总审批、执行结项动作展示

依赖：

- `BE-06`
- `BE-07`

完成标准：

- 主单与线路审核状态清晰区分
- 退回整改与重新提交可追踪

### FE-06 权限配置页签

目标：

- 新增项目级、目录级、文档级授权配置视图
- 支持授权查询、新增、修改、撤销和矩阵查看

依赖：

- `BE-08`
- `BE-10`

完成标准：

- 权限页能区分 `PROJECT/DIRECTORY/DOCUMENT` 三类授权范围
- 权限页能区分 `USER/ROLE/TEAM` 三类授权对象
- 观察员、评审专家等预设角色的默认只读/审核权限可直观看到

### FE-07 统计分析页签

目标：

- 提供项目总览、资料完备度、验收进度统计视图

依赖：

- `BE-10`

完成标准：

- 支持基本筛选切换
- 支持管理层快速查看治理摘要

## 4. 联调任务拆解

### INT-01 准备真实样本数据

目标：

- 准备项目、模板、目录、文档、协同、验收工单、审核明细、权限样本数据

依赖：

- `BE-08`

完成标准：

- 至少有一条完整项目链路可联调：
  项目创建 -> 空间初始化 -> 文档补录 -> 协同记录 -> 验收申请 -> 多条线审核 -> 汇总审批 -> 结项执行

### INT-02 接口兼容联调

目标：

- 验证现有 `project.js` 基础接口调用不受影响
- 验证新页签调用新增子资源接口

依赖：

- `BE-09`
- `FE-05`

完成标准：

- 旧入口和新页签均可正常工作
- 无跨 tab 状态串扰

### INT-03 权限联调

目标：

- 验证项目经理、过程资料管理员、各条线审核人、观察员的页面与动作权限

依赖：

- `BE-08`
- `BE-10`
- `FE-06`

完成标准：

- 不同角色的页签可见性正确
- 项目级授权、目录级覆盖授权、文档级覆盖授权能生效
- 无权用户不可通过直接请求越权操作

## 5. 测试任务拆解

### QA-01 主流程测试

- 项目创建与模板快照初始化
- 项目空间初始化
- 文档新增、合规检查、归档
- 协同记录新增
- 验收申请创建
- 多条线审核
- 汇总审批与结项执行

### QA-02 兼容回归

- 现有项目台账 CRUD 不回归
- 现有模板 CRUD 不回归
- 现有 PROJECT 域工单 list/add/approve/execute 不回归

### QA-03 权限测试

- 项目经理、成员、观察员、评审专家权限差异
- 项目级、目录级、文档级权限覆盖关系
- 无权用户直连接口校验

### QA-04 边界异常测试

- 项目编号重复
- 必传目录未完成不可提交验收
- 必审线路未完成不可汇总审批
- 已归档项目限制编辑
