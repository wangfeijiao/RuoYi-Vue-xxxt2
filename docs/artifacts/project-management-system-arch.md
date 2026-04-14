[artifact:SystemArch]
status: READY
owner: tech-lead-architect
scope:
- 设计“信息化项目管理”模块在当前 RuoYi-Vue 单体项目中的系统边界、模块职责、依赖关系与分阶段演进方案
- 明确现有项目台账、模板、PROJECT 域工单如何演进为“项目台账 + 项目空间 + 协同记录 + 验收审核 + 权限控制”的统一模块
inputs:
- [artifact:PRD] docs/artifacts/project-management-prd.md
- [artifact:UserStory] docs/artifacts/project-management-user-story.md
- [artifact:Prototype] docs/artifacts/project-management-prototype.md
- 当前领域模型：ruoyi-system/src/main/java/com/ruoyi/system/domain/information/InfoProject.java
- 当前模板模型：ruoyi-system/src/main/java/com/ruoyi/system/domain/information/InfoProjectTemplate.java
- 当前控制器：ruoyi-admin/src/main/java/com/ruoyi/web/controller/information/InfoProjectController.java
- 当前 Mapper：ruoyi-system/src/main/resources/mapper/information/InfoProjectMapper.xml
- 当前通用工单：ruoyi-system/src/main/resources/mapper/information/InfoWorkOrderMapper.xml
- 当前前端基线：ruoyi-ui/src/views/information/project/index.vue
handoff_to:
- engineering-manager
- senior-backend-engineer
- senior-frontend-engineer
deliverables:
- 信息化项目管理模块的系统分层与子域划分
- 复用现有项目台账/模板/工单能力的兼容扩展方案
- 项目空间目录、文档、协同记录、验收审核、权限模型的架构落点
risks:
- 项目空间文档承载能力一期仍以业务表与文件元数据为主，真实文件存储与在线预览能力后续再深化
- 多条线审核如果后续引入统一待办中心，接口聚合层可能需要二次抽象
exit_criteria:
- 模块边界、兼容策略、核心对象、交互链路与实现职责划分明确，可直接进入审批与实现拆解

## Entry Check

- 当前任务目标明确：输出“信息化项目管理”专属 `[artifact:SystemArch]`
- 必需输入齐备：PRD、UserStory、Prototype 和现有项目代码基线均已具备
- 本角色权限匹配：`tech-lead-architect` 负责架构设计，不直接进入业务实现
- 上游审批前置不存在：当前处于 Architecture 阶段，可直接输出

## 1. 架构结论

信息化项目管理模块应在当前单体工程内演进为 5 个协同子域，而不是继续将所有能力堆叠在现有 `InfoProjectController + index.vue` 中：

1. `project-ledger`
   负责项目主档、项目状态、项目负责人矩阵、计划信息、目标用户、资源需求摘要。
2. `project-template-space`
   负责模板管理、项目空间目录实例化、目录状态与文档归档视图。
3. `project-collaboration`
   负责项目执行阶段的跨模块协同记录，包括资源状态、网络变更、数据使用、结项归档摘要。
4. `project-acceptance`
   负责验收申请主单、五条线材料汇总、多条线审核意见与整改闭环。
5. `project-permission-stats`
   负责项目级/目录级/文档级授权与项目统计分析。

架构原则：

- 保留当前单体项目形态，不拆微服务。
- 保留 `inf_project`、`inf_project_template`、`inf_work_order` 作为主表基线。
- 一期优先解决“结构化建模与接口边界”，不引入复杂 BPM 或外部文件引擎。
- 对现有前后端接口保持兼容，新增 detail/space/review/permission 类接口做增量扩展。

## 2. 系统边界

### 2.1 域内职责

- 项目台账维护
- 标准模板与非标准模板维护
- 项目空间目录实例化与目录状态管理
- 项目文档元数据管理与合规检查留痕
- 执行阶段协同记录管理
- 项目验收申请与多条线审核
- 项目级与目录/文档级权限控制
- 项目治理统计分析

### 2.2 域外职责

- IT 资源主数据维护
- 网络资源主数据维护
- 数据资源主数据维护
- 统一门户/认证主数据维护
- 文件物理存储与在线预览引擎
- 短信/企业微信等真实通知通道

信息化项目管理只消费上述域外对象，不回写其主数据。

## 3. 现有资产复用策略

### 3.1 继续复用 `inf_project`

`inf_project` 继续作为项目主档表，保留以下字段：

- `project_code`
- `project_name`
- `project_type`
- `project_status`
- `project_phase`
- `owner_dept_id`
- `project_manager`
- `owner_leader`
- `vendor_name`
- `vendor_owner`
- `portal_owner`
- `resource_owner`
- `data_owner`
- `security_owner`
- `target_users`
- `directory_template_json`
- `custom_directory_json`
- `acceptance_status`
- `plan_start_date`
- `plan_end_date`

复用原因：

- 当前控制器、Mapper、前端页面、SQL 已覆盖这些核心字段。
- 这些字段足以承载一期的“项目主档 + 负责人矩阵 + 模板快照摘要”。
- 可保障已有项目台账 CRUD 不被推翻。

### 3.2 继续复用 `inf_project_template`

`inf_project_template` 一期继续使用 `directory_json` 承载模板树定义，不强制拆成模板项子表。

原因：

- 现有代码和 SQL 已具备模板主表。
- 一期重点在“模板实例化到项目空间”，不是模板设计器深度能力。
- 保留 JSON 快照可降低迁移复杂度。

### 3.3 继续复用 `inf_work_order`

`inf_work_order` 继续作为 PROJECT 域验收申请主单，约定：

- `domain_type = PROJECT`
- `request_type = ACCEPTANCE`
- `subject_id = project_id`
- `subject_type = PROJECT`

原因：

- 当前控制器已具备 PROJECT 域 order/list、add、approve、execute 入口。
- 工单主表已包含申请、审批、执行的时间戳与意见字段。
- 一期只需补齐“多条线审核明细”，无需重新造主工单模型。

## 4. 新增核心对象

### 4.1 `ProjectRelatedObjectRel`

用于维护项目与外部对象的结构化关联，覆盖：

- `IT_RESOURCE`
- `NETWORK_RESOURCE`
- `DATA_RESOURCE`
- `PORTAL_RESOURCE`

职责：

- 支撑项目启动阶段的资源关联。
- 支撑项目详情中查看关联对象。
- 支撑结项时回收/检查结果追踪。

### 4.2 `ProjectSpaceDirectory`

用于承载项目空间目录实例。

职责：

- 根据模板实例化目录树。
- 记录目录层级、是否必传、合规状态、归档状态。
- 支撑目录级权限挂接。

### 4.3 `ProjectDocument`

用于承载项目文档元数据。

职责：

- 记录文档所属目录、上传人、版本号、状态。
- 记录合规检查与归档状态。
- 不承载实际文件二进制。

### 4.4 `ProjectCollaborationRecord`

用于记录执行与结项阶段协同事项，建议统一建模为：

- `RESOURCE_STATUS`
- `NETWORK_CHANGE`
- `DATA_USAGE`
- `CLOSURE_CHECK`
- `KNOWLEDGE_ARCHIVE`

职责：

- 汇总项目执行阶段过程留痕。
- 为验收申请提供引用材料。

### 4.5 `ProjectAcceptanceReview`

用于承载验收申请的多条线审核明细，审核线至少包含：

- `IT_MANAGEMENT`
- `DATA_MANAGEMENT`
- `SECURITY_MANAGEMENT`
- `PORTAL_MANAGEMENT`
- `PROCESS_DOCUMENT`
- 可选 `EXPERT_REVIEW`

职责：

- 一张验收申请对应多条线审核状态。
- 支撑退回整改与再次提交时保留历史痕迹。

### 4.6 `ProjectPermissionRel`

用于统一承载项目级、目录级、文档级授权。

职责：

- 区分授权目标类型：用户/角色/团队。
- 区分资源范围：项目/目录/文档。
- 区分权限动作：查看/编辑/下载/删除。

## 5. 分层与代码落点

### 5.1 Controller 边界

建议在 `ruoyi-admin/.../information` 下保留并扩展：

- `InfoProjectController`
  负责项目主档 CRUD、基础查询、详情聚合入口。
- `InfoProjectTemplateController`
  可继续内聚在 `InfoProjectController`，但推荐后续拆分以降低控制器复杂度。
- `InfoProjectSpaceController`
  负责项目空间目录、文档上传元数据、合规检查、归档。
- `InfoProjectAcceptanceController`
  负责验收申请、多条线审核、整改重提。
- `InfoProjectPermissionController`
  负责授权读写。
- `InfoProjectStatsController`
  负责统计分析查询。

### 5.2 Service 边界

建议拆分服务职责：

- `ProjectLedgerService`
- `ProjectTemplateService`
- `ProjectSpaceService`
- `ProjectCollaborationService`
- `ProjectAcceptanceService`
- `ProjectPermissionService`
- `ProjectStatsQueryService`

说明：

- 不要求拆 Maven 模块。
- 但必须避免继续把模板、验收、权限、文档、协同全部堆进单一 `IInfoProjectService` 实现。

### 5.3 Frontend 边界

建议将 `ruoyi-ui/src/views/information/project/index.vue` 从单文件大页面演进为：

- `index.vue`
  负责列表页与总入口。
- `components/ProjectFormDialog.vue`
- `components/ProjectDetailDrawer.vue`
- `components/ProjectSpaceTab.vue`
- `components/ProjectCollaborationTab.vue`
- `components/ProjectAcceptanceTab.vue`
- `components/ProjectPermissionTab.vue`
- `template/index.vue`
- `statistics.vue`

## 6. 关键数据流

### 6.1 项目创建流

前端表单 -> `InfoProjectController` -> `ProjectLedgerService` -> `inf_project`

同步动作：

- 根据 `project_type` 匹配标准模板。
- 将模板快照写入 `directory_template_json`。
- 调用 `ProjectSpaceService` 实例化目录树。

### 6.2 模板实例化流

模板查询 -> 读取 `inf_project_template.directory_json` -> 生成 `inf_project_space_dir`

原则：

- 模板主数据保留 JSON。
- 项目空间必须落结构化目录实例，不能只把 JSON 放在项目主表中。

### 6.3 文档上传与合规检查流

项目空间上传 -> `ProjectDocument` 元数据写入 -> 资料管理员执行合规检查 -> 更新目录/文档状态

规则：

- 目录状态由其下文档状态汇总。
- 合规检查只改变合规结果，不替代验收审核。

### 6.4 项目协同记录流

执行阶段录入 -> `inf_project_collaboration_record`

用途：

- 展示项目执行轨迹。
- 为验收申请自动带出协同摘要。
- 为结项阶段回收/评估提供依据。

### 6.5 验收申请流

项目经理发起 -> `inf_work_order` 生成主单 -> 初始化 `inf_project_acceptance_review` 多条线记录 -> 各条线审核 -> 汇总结果 -> 更新 `inf_project.acceptance_status`

### 6.6 权限判定流

用户进入项目/目录/文档 -> 查询 `ProjectPermissionRel` -> 与系统角色权限叠加判断 -> 返回可见/可操作能力集

## 7. 兼容扩展策略

### 7.1 保持兼容的接口

继续保留：

- `GET /information/project/list`
- `GET /information/project/{projectId}`
- `POST /information/project`
- `PUT /information/project`
- `DELETE /information/project/{projectIds}`
- `GET /information/project/template/list`
- `POST /information/project/order`
- `POST /information/project/order/{workOrderId}/approve`
- `POST /information/project/order/{workOrderId}/execute`

### 7.2 增量扩展的接口方向

- 项目详情聚合
- 项目空间目录与文档
- 项目协同记录
- 验收审核明细
- 权限配置
- 统计分析

## 8. 分阶段建议

### Phase 1

- 项目台账字段补齐与兼容改造
- 模板实例化目录
- 文档元数据与合规检查
- 验收申请主单 + 多条线审核明细
- 权限基础模型

### Phase 2

- 文档版本对比与批量归档
- 统一待办聚合
- 模板版本化与发布回滚
- 细化 expert review 协同

### Phase 3

- 深化文件引擎与在线预览
- 深化跨模块事件联动
- 深化统计宽表/看板优化

