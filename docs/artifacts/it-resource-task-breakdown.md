[artifact:TaskBreakdown]
status: READY
owner: tech-lead-architect
scope:
- 拆解 IT 资源管理模块的架构优化与迭代开发任务
- 结合当前仓库已实现能力，给出复用任务、优化任务、依赖和风险
inputs:
- [artifact:PRD] docs/artifacts/it-resource-prd.md
- [artifact:UserStory] docs/artifacts/it-resource-user-story.md
- [artifact:Prototype] docs/artifacts/it-resource-prototype.md
- [artifact:SystemArch] docs/artifacts/it-resource-system-arch.md
- [artifact:DBDesign] docs/artifacts/it-resource-db-design.md
- [artifact:APIDoc] docs/artifacts/it-resource-api-doc.md
- 当前仓库资源实现代码与 SQL
handoff_to:
- engineering-manager
- senior-backend-engineer
- senior-frontend-engineer
- senior-qa-engineer
deliverables:
- IT 资源管理模块可执行任务拆解
- 现状复用任务与优化任务清单
- 风险、依赖与实施顺序建议
risks:
- 若在一个迭代内同时做“字段扩展 + 状态机重构 + 前端重做”，回归面会明显扩大
- 当前前端页面是单页双 Tab 形态，若同时重构交互和后端契约，联调成本较高
exit_criteria:
- 任务拆解清晰到可排期、可分工、可验收

## 入口检查

- 当前任务目标是否明确：是，仅拆解 IT 资源管理模块任务。
- 必需 artifact 是否齐备：是。
- 本角色是否有权限处理该任务：是。
- 是否存在上游审批或评审前置条件：否。

## 一、实施原则

- 先复用现有骨架，再做约束增强
- 先稳住后端状态机和数据规则，再优化前端体验
- 先保证闭环正确，再做页面精细化和类型扩展

## 二、现状复用任务

### T1 复用资源主档模型

目标：

- 基于现有 `inf_resource`、`InfoResource`、`InfoResourceMapper` 继续演进

改动范围：

- `ruoyi-system` 资源实体、Mapper、Service

输出：

- 保留资源主表和 CRUD 主链路

依赖：

- 无

### T2 复用资源工单总线

目标：

- 基于现有 `inf_work_order` 与 `InfoWorkOrderServiceImpl` 承载资源申请、变更、回收

改动范围：

- 资源工单相关 Service、Controller、前端 API

输出：

- 保留 `/information/resource/order/*` 路径与基础流程

依赖：

- 无

### T3 复用资源前端双 Tab 页面

目标：

- 保留资源台账与工单双 Tab 主结构

改动范围：

- `ruoyi-ui/src/views/information/resource/index.vue`

输出：

- 延续当前页面路由和用户操作路径

依赖：

- 无

## 三、建议优化任务

### T4 后端状态机治理

目标：

- 将资源状态和工单状态流转规则固化在服务层

changed_areas:

- `InfoWorkOrderServiceImpl`
- `InfoResourceServiceImpl`
- 资源工单相关 DTO/校验逻辑

工作内容：

- 补齐 `PENDING_DELIVERY`、`CHANGING`、`PENDING_RECYCLE`
- 审批通过时同步资源中间状态
- 执行完成时按工单类型回写最终状态
- 禁止非法审批和重复执行

验收：

- 状态流转用例全部通过

### T5 资源主档校验治理

目标：

- 将唯一性和合法性校验前移到服务层

changed_areas:

- `InfoResourceServiceImpl`
- `InfoResourceMapper`

工作内容：

- 增加资源编码唯一性显式校验
- 增加运行中资源 IP 冲突校验
- 限制已回收资源的非法编辑
- 限制核心字段直接修改

验收：

- 新增/编辑接口能返回明确失败原因

### T6 删除策略改造

目标：

- 从物理删除切到逻辑停用或引用检查

changed_areas:

- SQL
- 资源 Service
- 资源 Controller

工作内容：

- 若本轮可改表，则新增 `deleted_flag`
- 若本轮不改表，则先在服务层禁止删除被工单引用资源

验收：

- 已有工单历史的资源无法被物理删除

### T7 工单载荷契约标准化

目标：

- 让 `request_payload_json` 和 `current_snapshot_json` 有统一结构

changed_areas:

- 工单 DTO
- 后端组装逻辑
- 前端弹窗

工作内容：

- 为 `APPLY`、`CHANGE`、`RECYCLE` 定义固定 JSON 结构
- 申请、变更、回收分别校验必填字段

验收：

- 前后端对同一类型工单使用一致载荷

### T8 资源列表与工单列表查询增强

目标：

- 补齐管理场景所需筛选条件

changed_areas:

- `InfoResourceMapper.xml`
- `InfoWorkOrderMapper.xml`
- 资源前端筛选区

工作内容：

- 资源增加 `monitorStatus`、`maintainerName`、`ipAddress`、时间范围
- 工单增加工单号、审批人、执行人、时间范围

验收：

- PRD 中定义的核心筛选项可用

### T9 资源详情与工单详情展示优化

目标：

- 补齐资源详情抽屉和工单详情视图

changed_areas:

- `resource/index.vue`

工作内容：

- 资源详情抽屉
- 最近工单展示
- 工单详情展示审批意见、执行结果、快照内容
- 变更前后差异展示预留位

验收：

- 资源和工单都具备可追溯详情

### T10 工单操作按钮约束

目标：

- 前端根据状态显隐按钮，但以后端约束为准

changed_areas:

- 资源前端页面
- 工单服务

工作内容：

- 仅 `PENDING` 显示审批
- 仅 `PENDING_EXECUTION` 显示执行
- `REJECTED/COMPLETED` 禁止继续动作

验收：

- 无法通过 UI 正常触发非法流程

### T11 统计看板补强

目标：

- 输出资源域管理所需基础统计

changed_areas:

- dashboard 聚合查询
- 资源页面顶部统计卡片或总览接入

工作内容：

- 资源总量
- 使用中
- 空闲
- 待审批工单数
- 待执行工单数
- 已回收数

验收：

- 看板统计与列表数据口径一致

### T12 测试任务

目标：

- 覆盖资源域主流程和约束

changed_areas:

- 后端测试
- 前端人工回归用例

工作内容：

- 资源新增/编辑/删除
- 申请/审批/执行
- 变更/审批/执行
- 回收/审批/执行
- 非法状态流转
- 资源编码重复
- IP 冲突

验收：

- 关键路径通过，无阻断缺陷

## 四、建议实施顺序

### 第一批：后端规则补强

- T4 后端状态机治理
- T5 资源主档校验治理
- T6 删除策略改造
- T7 工单载荷契约标准化

原因：

- 这是资源域稳定性的基础，不先做会导致前端和测试都建立在不稳契约上

### 第二批：查询与页面完善

- T8 查询增强
- T9 详情展示优化
- T10 操作按钮约束
- T11 统计看板补强

### 第三批：测试与回归

- T12 测试任务

## 五、角色分工建议

### `senior-backend-engineer`

- 负责 T4、T5、T6、T7、T8 后端部分、T11 后端部分

### `senior-frontend-engineer`

- 负责 T8 前端部分、T9、T10、T11 前端部分

### `senior-qa-engineer`

- 负责 T12，覆盖主流程、异常流和回归

## 六、风险与依赖

### 1. 风险

- 若继续允许资源直接编辑核心字段，工单设计会被绕过
- 若继续允许物理删除，历史审计会断裂
- 若不统一工单载荷结构，后续差异展示与审计分析难以落地

### 2. 依赖

- 项目模块提供项目引用数据
- 资源状态和工单状态字典需前后端统一
- QA 需要以状态机规则为依据编写测试

## 七、任务拆解结论

当前资源模块不缺主干，缺的是约束和治理。下一步研发不应再大面积铺新页面或新表，而应优先：

- 强化状态机
- 强化校验
- 强化删除与审计策略
- 强化查询和详情

这四项完成后，IT 资源管理模块才具备进入审批和持续迭代的基础。
