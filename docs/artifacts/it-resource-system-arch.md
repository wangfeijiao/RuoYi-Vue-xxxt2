[artifact:SystemArch]
status: READY
owner: tech-lead-architect
scope:
- 细化 IT 资源管理模块的系统边界、对象关系、状态流转约束、前后端分层与演进方案
- 明确当前仓库资源台账/工单实现的复用方式与后续优化方向
inputs:
- [artifact:PRD] docs/artifacts/it-resource-prd.md
- [artifact:UserStory] docs/artifacts/it-resource-user-story.md
- [artifact:Prototype] docs/artifacts/it-resource-prototype.md
- 当前仓库已有实现：inf_resource、inf_work_order、InfoResourceController、InfoWorkOrderServiceImpl、information/resource 前端页面
handoff_to:
- engineering-manager
- senior-backend-engineer
- senior-frontend-engineer
deliverables:
- IT 资源管理模块架构细化说明
- 现状复用方案与架构优化建议
- 可直接指导后续接口、数据、页面和状态机治理的实施约束
risks:
- 当前实现采用通用工单模型，扩展速度快，但资源申请、变更、回收的领域语义仍偏弱
- 当前资源模型为通用字段模型，后续资源类型差异扩大后会出现字段稀疏和校验不足问题
exit_criteria:
- 模块边界、对象关系、分层职责、状态流转约束、复用点和优化点明确
- 后续研发可基于该文档继续做局部重构和增量迭代

## 入口检查

- 当前任务目标是否明确：是，仅聚焦 IT 资源管理模块的架构侧细化。
- 必需 artifact 是否齐备：是，`it-resource-prd.md`、`it-resource-user-story.md`、`it-resource-prototype.md` 已存在。
- 本角色是否有权限处理该任务：是，`tech-lead-architect` 负责输出 `[artifact:SystemArch]`、`[artifact:DBDesign]`、`[artifact:APIDoc]`、`[artifact:TaskBreakdown]`。
- 是否存在上游审批或评审前置条件：否，当前属于 Architecture 阶段，可直接输出 `READY`。

## 一、模块边界

### 1. 模块职责

IT 资源管理模块负责以下能力：

- IT 资源台账管理
- 资源申请、变更、回收工单管理
- 资源状态与监控状态管理
- 资源与项目的关联管理
- 资源工单审批、执行与留痕
- 资源统计看板所需的聚合数据输出

### 2. 模块边界内

- `inf_resource` 资源主数据
- `inf_work_order` 中 `domain_type=RESOURCE` 的工单数据
- 资源台账查询、详情、增改删
- 资源申请、变更、回收工单的提交、审批、执行
- 资源状态联动

### 3. 模块边界外

- 自动发现/CMDB 同步
- 虚拟化平台或云平台自动交付
- 真实监控平台告警接入
- 采购、财务、折旧、预算
- 网络资源、数据资源、应用资源的跨域编排

## 二、现状复用

### 1. 可直接复用的后端实现

- 资源领域对象：`InfoResource`
- 资源 CRUD 控制器：`InfoResourceController`
- 资源服务：`IInfoResourceService`、`InfoResourceServiceImpl`
- 资源 MyBatis：`InfoResourceMapper`、`InfoResourceMapper.xml`
- 通用工单服务：`IInfoWorkOrderService`、`InfoWorkOrderServiceImpl`
- 资源工单接口：`/information/resource/order/*`

### 2. 可直接复用的前端实现

- API：`ruoyi-ui/src/api/information/resource.js`
- 页面：`ruoyi-ui/src/views/information/resource/index.vue`
- 交互骨架：台账 Tab + 工单 Tab + 新增/审批/执行弹窗

### 3. 当前实现的有效复用价值

- 已经具备“资源台账 + 通用工单”的基础骨架
- 已经有资源 CRUD、工单查询、审批、执行入口
- 已经有权限码和菜单结构
- 已经有资源状态回写机制，能支撑 MVP 闭环

## 三、建议优化

### 1. 架构优化方向

- 保留 `inf_resource + inf_work_order` 的总架构，不新增独立工作流引擎
- 在资源域内补齐状态机、字段校验和操作约束，避免直接编辑导致状态失控
- 将“台账直接编辑”和“通过工单变更”做清晰职责分离
- 将资源表单逐步升级为“通用字段 + 类型扩展字段”

### 2. 领域优化方向

- 将资源工单严格收敛为 `APPLY`、`CHANGE`、`RECYCLE`
- `DELIVER`、`USE` 不作为资源域对外主类型，避免和执行动作混淆
- 执行动作是工单阶段，不是工单类型
- 资源台账直接修改仅允许非核心字段

### 3. 技术优化方向

- 服务层新增领域校验器，不把状态判断散落在 Controller 和前端
- Mapper 层增加更完整筛选条件
- 删除改为逻辑停用或状态回收，不建议物理删除被工单引用的数据
- 将工单快照/载荷 JSON 结构标准化，便于后续前端做差异对比

## 四、对象关系

### 1. 核心对象

- `Resource`：资源当前主档
- `ResourceWorkOrder`：资源申请/变更/回收过程单据
- `ProjectRef`：资源归属项目的引用关系

### 2. 关系说明

- 一个项目可关联多个资源
- 一个资源可关联多个工单历史
- 一个工单在资源域最多关联一个资源主体
- 申请工单在交付前可无 `subjectId`，执行落账后绑定资源
- 变更/回收工单必须关联已存在资源

### 3. 推荐关系约束

- `inf_resource.project_id -> inf_project.project_id` 为弱引用
- `inf_work_order.subject_id -> inf_resource.resource_id` 在 `domain_type=RESOURCE` 时视为业务外键
- 不强制数据库外键，约束由服务层控制

## 五、状态流转约束

### 1. 资源状态

- `IDLE`：已登记、空闲可分配
- `PENDING_DELIVERY`：申请审批通过，待交付
- `IN_USE`：已交付、正在使用
- `CHANGING`：变更执行中
- `PENDING_RECYCLE`：回收审批通过，待回收
- `RECYCLED`：已回收

### 2. 工单状态

- `DRAFT`
- `PENDING`
- `REJECTED`
- `PENDING_EXECUTION`
- `COMPLETED`

### 3. 资源工单约束

- `APPLY`：`PENDING -> PENDING_EXECUTION -> COMPLETED`
- `CHANGE`：`PENDING -> PENDING_EXECUTION -> COMPLETED`
- `RECYCLE`：`PENDING -> PENDING_EXECUTION -> COMPLETED`
- 任意工单：`REJECTED` 后不可执行
- 任意工单：`COMPLETED` 后不可重复审批、不可重复执行

### 4. 资源状态联动约束

- 申请工单审批通过后，目标资源或待创建资源进入 `PENDING_DELIVERY`
- 申请工单执行完成后，资源进入 `IN_USE`
- 变更工单审批通过后，资源进入 `CHANGING`
- 变更工单执行完成后，资源恢复为 `IN_USE`
- 回收工单审批通过后，资源进入 `PENDING_RECYCLE`
- 回收工单执行完成后，资源进入 `RECYCLED` 或 `IDLE`

### 5. 当前实现与目标差异

当前 `InfoWorkOrderServiceImpl` 仅在执行完成后统一回写状态，默认状态主要是 `IN_USE` 或 `RECYCLED`，未覆盖：

- 审批通过后的中间资源状态
- 变更中的 `CHANGING`
- 回收待执行的 `PENDING_RECYCLE`
- 申请待交付的 `PENDING_DELIVERY`

这部分应在下一轮后端优化中补齐。

## 六、前后端分层

### 1. 后端分层职责

- Controller：入参接收、权限控制、分页包装、用户上下文注入
- Service：领域规则、状态流转、资源与工单联动、幂等和合法性校验
- Mapper：单表 CRUD 与列表查询
- SQL：表结构、索引、菜单权限初始化

### 2. 前端分层职责

- `api/information/resource.js`：资源接口封装
- `views/information/resource/index.vue`：资源台账/工单页面
- 资源弹窗：资源主档维护
- 工单弹窗：申请、审批、执行动作

### 3. 前后端协作原则

- 前端只做基础表单校验与按钮显隐
- 状态合法性必须以后端为准
- 前端不拼接状态流转逻辑
- JSON 快照和载荷由后端定义结构，前端只负责展示

## 七、建议的资源域内部组件拆分

在不拆新 Maven 模块的前提下，建议在 `ruoyi-system` 内继续细化为：

- `resource-service`
  职责：资源主档 CRUD、唯一性校验、核心字段修改约束
- `resource-order-service`
  职责：资源申请/变更/回收工单的创建、审批、执行
- `resource-state-policy`
  职责：资源状态机与工单状态机规则
- `resource-query-service`
  职责：资源列表、详情、统计聚合查询

当前仓库中这些职责主要集中在 `InfoResourceServiceImpl` 和 `InfoWorkOrderServiceImpl`，建议逐步拆出策略方法，但不必引入新工程结构。

## 八、风险与依赖

### 1. 主要风险

- 当前资源删除为物理删除，容易破坏工单追溯
- 当前资源唯一性仅数据库层面约束 `resource_code`，IP 冲突尚未治理
- 当前工单载荷结构是自由 JSON 文本，前后端缺少统一契约
- 当前资源类型为自由文本，难以保证统计口径一致

### 2. 依赖项

- 依赖项目模块提供项目下拉引用数据
- 依赖 RuoYi 权限体系提供菜单和按钮权限
- 依赖已有分页、日志、审计字段能力

### 3. 架构建议结论

IT 资源管理模块不需要推翻现有实现。合理路径是：

- 复用已有资源台账和通用工单骨架
- 在服务层补强状态机、校验器、删除策略和 JSON 契约
- 在前端补强详情、筛选、差异展示和按钮约束

这样可以在保持当前仓库结构稳定的前提下，把资源域从“可用原型”提升为“可控的业务模块”。
