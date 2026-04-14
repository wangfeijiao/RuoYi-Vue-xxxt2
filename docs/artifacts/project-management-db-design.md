[artifact:DBDesign]
status: READY
owner: tech-lead-architect
scope:
- 为“信息化项目管理”模块定义基于现有 SQL 基线的表结构演进、字段边界、索引与迁移策略
- 明确项目台账、项目空间目录、项目文档、项目协同记录、验收申请、多条线审核、项目级/目录级/文档级权限的数据建模方案
inputs:
- [artifact:PRD] `docs/artifacts/project-management-prd.md`
- [artifact:UserStory] `docs/artifacts/project-management-user-story.md`
- [artifact:Prototype] `docs/artifacts/project-management-prototype.md`
- [artifact:SystemArch] `docs/artifacts/project-management-system-arch.md`
- 当前 SQL 基线：`sql/information_management.sql`
- 当前领域模型：`ruoyi-system/src/main/java/com/ruoyi/system/domain/information/InfoProject.java`
- 当前领域模型：`ruoyi-system/src/main/java/com/ruoyi/system/domain/information/InfoProjectTemplate.java`
- 当前持久层：`ruoyi-system/src/main/resources/mapper/information/InfoProjectMapper.xml`
- 当前持久层：`ruoyi-system/src/main/resources/mapper/information/InfoWorkOrderMapper.xml`
handoff_to:
- engineering-manager
- senior-backend-engineer
- senior-frontend-engineer
deliverables:
- `inf_project`、`inf_project_template`、`inf_work_order` 的复用与增量字段方案
- 新增项目空间、文档、协同、审核明细、权限关系表设计
- 索引、唯一约束、迁移与兼容策略
risks:
- `directory_template_json`、`custom_directory_json` 与目录实例表并存期间，需要服务层保持快照与实例口径一致
- `inf_work_order` 复用为项目验收主单时，历史 `PROJECT` 域工单需要按 `request_type` 做语义隔离
exit_criteria:
- 表结构、字段口径、索引约束、迁移步骤与兼容映射清晰，可直接支撑实现层设计与审批

## Entry Check

- 当前任务目标明确：输出“信息化项目管理”专属 `[artifact:DBDesign]`
- 必需 artifact 齐备：PRD、UserStory、Prototype、SystemArch 已齐备
- 本角色权限匹配：`tech-lead-architect` 负责数据库设计，不直接实施迁移脚本
- 上游审批前置不存在：当前处于 Architecture 阶段

## 1. 数据建模结论

数据库采用“主表复用 + 子表外置 + 状态兼容映射”的演进策略：

1. `inf_project`
   继续作为项目主档表。
2. `inf_project_template`
   继续作为模板主表。
3. `inf_work_order`
   继续作为 PROJECT 域验收申请主单。
4. `inf_project_space_directory`
   新增，用于承载项目空间目录实例。
5. `inf_project_document`
   新增，用于承载项目文档元数据。
6. `inf_project_collaboration_record`
   新增，用于承载启动/执行/结项协同记录。
7. `inf_project_acceptance_review`
   新增，用于承载多条线审核明细。
8. `inf_project_permission_rel`
   新增，用于承载项目级、目录级、文档级授权关系。

## 2. `inf_project` 字段边界

### 2.1 继续复用的现有字段

- `project_id`
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
- `create_by`
- `create_time`
- `update_by`
- `update_time`
- `remark`

说明：

- `directory_template_json` 存放项目创建时冻结的标准目录模板快照。
- `custom_directory_json` 存放追加的非标准目录模板快照。
- 两者是模板快照，不替代项目空间目录实例。

### 2.2 建议新增字段

- `template_id bigint(20) null`
  当前项目使用的主模板 ID。
- `template_version_no varchar(32) null`
  冻结模板版本号。
- `project_objective varchar(500) null`
  项目目标与范围。
- `resource_requirement_summary text null`
  资源需求摘要。
- `business_owner varchar(64) null`
  业务负责人。
- `technical_owner varchar(64) null`
  技术负责人。
- `space_init_status varchar(32) not null default 'PENDING'`
  项目空间初始化状态。
- `archive_status varchar(32) not null default 'UNARCHIVED'`
  项目归档状态。
- `current_acceptance_order_id bigint(20) null`
  当前生效验收主单 ID。
- `document_completion_rate decimal(5,2) not null default 0.00`
  资料完备度缓存值。
- `last_collaboration_time datetime null`
  最近协同时间。

### 2.3 索引建议

- 保留现有：
  - `uk_project_code (project_code)`
  - `idx_project_status (project_status)`
  - `idx_project_type (project_type)`
- 建议新增：
  - `idx_project_phase (project_phase)`
  - `idx_project_acceptance (acceptance_status)`
  - `idx_project_archive (archive_status)`
  - `idx_project_owner_dept (owner_dept_id)`
  - `idx_project_current_order (current_acceptance_order_id)`

## 3. `inf_project_template` 复用策略

本期继续复用现有字段：

- `template_id`
- `template_name`
- `project_type`
- `phase_name`
- `version_no`
- `directory_json`
- `status`

字段口径：

- `phase_name` 表示模板适用阶段，如 `INIT`、`EXECUTION`、`CLOSING`
- `directory_json` 存放目录树、必传标识、说明和排序
- `status` 存储继续沿用现有 `0/1` 口径，接口层和前端字典映射为 `ACTIVE/DISABLED`

索引建议：

- 保留现有：
  - `idx_template_type (project_type)`
  - `idx_template_status (status)`
- 建议新增：
  - `idx_template_phase (phase_name)`
  - `uk_template_version (template_name, project_type, version_no)`

## 4. `inf_work_order` 复用策略

### 4.1 作为验收申请主单

一期继续复用 `inf_work_order` 承载项目验收主单：

- `domain_type = PROJECT`
- `request_type = ACCEPTANCE`
- `subject_id = project_id`
- `subject_type = PROJECT`

### 4.2 现有字段承载方式

- `request_title`
  验收申请标题。
- `request_reason`
  提交说明或整改说明。
- `request_payload_json`
  材料快照、审核线路清单、提交摘要。
- `current_snapshot_json`
  提交时项目主档和项目空间摘要快照。
- `approver_name`
  汇总审批人，不再承载每条线审核人。
- `approval_comment`
  汇总审批意见。
- `executor_name`
  结项执行人。
- `execution_result`
  结项执行结果。

### 4.3 索引建议

- 保留现有：
  - `uk_work_order_no (work_order_no)`
  - `idx_work_order_domain (domain_type)`
  - `idx_work_order_request (request_type)`
  - `idx_work_order_status (order_status)`
  - `idx_work_order_project (project_id)`
- 建议新增：
  - `idx_work_order_project_request (project_id, request_type, order_status)`

## 5. 新表：`inf_project_space_directory`

设计目标：

- 保存项目空间目录实例
- 将模板快照转换为可操作目录树
- 支撑目录级必传、检查、归档状态

字段建议：

- `directory_id bigint(20)` 主键
- `project_id bigint(20) not null`
- `parent_id bigint(20) default 0`
- `template_id bigint(20) null`
- `directory_code varchar(64) not null`
- `directory_name varchar(128) not null`
- `directory_level int not null default 1`
- `directory_type varchar(32) not null`
- `full_path varchar(500) not null`
- `required_flag char(1) not null default '0'`
- `compliance_status varchar(32) not null default 'PENDING_CHECK'`
- `archive_status varchar(32) not null default 'UNARCHIVED'`
- `sort_order int not null default 0`
- `create_by varchar(64) default ''`
- `create_time datetime default null`
- `update_by varchar(64) default ''`
- `update_time datetime default null`
- `remark varchar(500) default null`

建议值：

- `directory_type`：`STANDARD`、`CUSTOM`
- `compliance_status`：`PENDING_CHECK`、`PASSED`、`REJECTED`
- `archive_status`：`UNARCHIVED`、`ARCHIVED`

约束与索引：

- `uk_project_directory_code (project_id, directory_code)`
- `idx_project_directory_parent (project_id, parent_id)`
- `idx_project_directory_required (project_id, required_flag)`
- `idx_project_directory_compliance (project_id, compliance_status)`
- `idx_project_directory_archive (project_id, archive_status)`

## 6. 新表：`inf_project_document`

设计目标：

- 保存目录下文档实例
- 支撑版本、检查状态、归档状态和来源留痕

字段建议：

- `document_id bigint(20)` 主键
- `project_id bigint(20) not null`
- `directory_id bigint(20) not null`
- `document_name varchar(255) not null`
- `document_type varchar(32) not null`
- `storage_key varchar(255) null`
- `version_no varchar(32) not null default 'V1'`
- `source_type varchar(32) not null`
- `uploaded_by varchar(64) not null`
- `uploaded_time datetime not null`
- `file_size bigint(20) null`
- `checksum varchar(64) null`
- `compliance_status varchar(32) not null default 'PENDING_CHECK'`
- `archive_status varchar(32) not null default 'UNARCHIVED'`
- `check_comment varchar(500) null`
- `deleted_flag char(1) not null default '0'`
- `create_by varchar(64) default ''`
- `create_time datetime default null`
- `update_by varchar(64) default ''`
- `update_time datetime default null`
- `remark varchar(500) default null`

建议值：

- `source_type`：`STANDARD_TEMPLATE`、`CUSTOM_TEMPLATE`、`MANUAL_UPLOAD`

约束与索引：

- `idx_project_directory_document (project_id, directory_id)`
- `idx_document_compliance (project_id, compliance_status)`
- `idx_document_archive (project_id, archive_status)`

## 7. 新表：`inf_project_collaboration_record`

设计目标：

- 承载启动/执行/结项阶段的协同记录
- 对接资源状态、网络变更、数据使用、结项归档摘要

字段建议：

- `record_id bigint(20)` 主键
- `project_id bigint(20) not null`
- `phase_code varchar(32) not null`
- `record_type varchar(32) not null`
- `related_domain_type varchar(32) null`
- `related_object_id bigint(20) null`
- `related_object_name varchar(128) null`
- `record_summary varchar(255) not null`
- `record_content_json text null`
- `handler_name varchar(64) not null`
- `record_time datetime not null`
- `record_status varchar(32) not null default 'RECORDED'`
- `create_by varchar(64) default ''`
- `create_time datetime default null`
- `update_by varchar(64) default ''`
- `update_time datetime default null`
- `remark varchar(500) default null`

建议值：

- `phase_code`：`INIT`、`EXECUTION`、`CLOSING`
- `record_type`：`RESOURCE_STATUS`、`NETWORK_CHANGE`、`DATA_USAGE`、`MATERIAL_CHECK`、`ARCHIVE_EVENT`
- `related_domain_type`：`RESOURCE`、`NETWORK`、`DATA`、`PORTAL`、`PROJECT`

索引建议：

- `idx_project_collab_phase (project_id, phase_code)`
- `idx_project_collab_type (project_id, record_type)`
- `idx_project_collab_time (project_id, record_time desc)`
- `idx_project_collab_related (related_domain_type, related_object_id)`

## 8. 新表：`inf_project_acceptance_review`

设计目标：

- 承载一轮验收申请下的多条线审核明细
- 与 `inf_work_order` 主单形成一对多关系

字段建议：

- `review_id bigint(20)` 主键
- `work_order_id bigint(20) not null`
- `project_id bigint(20) not null`
- `review_line_code varchar(32) not null`
- `review_line_name varchar(64) not null`
- `reviewer_role varchar(64) not null`
- `reviewer_name varchar(64) null`
- `review_status varchar(32) not null default 'PENDING'`
- `required_flag char(1) not null default '1'`
- `review_comment varchar(500) null`
- `sequence_no int not null default 1`
- `reviewed_time datetime null`
- `due_time datetime null`
- `create_by varchar(64) default ''`
- `create_time datetime default null`
- `update_by varchar(64) default ''`
- `update_time datetime default null`
- `remark varchar(500) default null`

建议值：

- `review_line_code`：`IT_MANAGEMENT`、`DATA_MANAGEMENT`、`SECURITY_MANAGEMENT`、`PORTAL_MANAGEMENT`、`PROCESS_DOCUMENT`
- `review_status`：`PENDING`、`IN_REVIEW`、`APPROVED`、`REJECTED`、`SKIPPED`

约束与索引：

- `uk_work_order_review_line (work_order_id, review_line_code)`
- `idx_review_work_order (work_order_id, review_status)`
- `idx_review_project (project_id, review_status)`
- `idx_review_reviewer (reviewer_name, review_status)`

## 9. 新表：`inf_project_permission_rel`

设计目标：

- 承载一期范围内的项目级、目录级、文档级授权关系
- 支撑项目经理、成员、观察员、评审专家等角色差异化访问控制

字段建议：

- `permission_id bigint(20)` 主键
- `project_id bigint(20) not null`
- `scope_type varchar(32) not null`
- `scope_id bigint(20) null`
- `target_type varchar(32) not null`
- `target_key varchar(64) not null`
- `can_view char(1) not null default '0'`
- `can_edit char(1) not null default '0'`
- `can_download char(1) not null default '0'`
- `can_delete char(1) not null default '0'`
- `inherit_flag char(1) not null default '1'`
- `start_time datetime null`
- `end_time datetime null`
- `create_by varchar(64) default ''`
- `create_time datetime default null`
- `update_by varchar(64) default ''`
- `update_time datetime default null`
- `remark varchar(500) default null`

建议值：

- `scope_type`：`PROJECT`、`DIRECTORY`、`DOCUMENT`
- `target_type`：`USER`、`ROLE`、`TEAM`
- `inherit_flag`：`1` 表示继承项目级默认授权，`0` 表示当前范围独立授权

约束与索引：

- `uk_scope_target (project_id, scope_type, scope_id, target_type, target_key)`
- `idx_permission_project_scope (project_id, scope_type, scope_id)`
- `idx_permission_target (target_type, target_key)`

说明：

- 一期不再额外拆分独立 ACL 主表或规则表，细粒度授权统一落在 `inf_project_permission_rel`
- 项目级默认授权、目录级覆盖授权、文档级覆盖授权均由本表承载

## 10. 迁移策略

### 10.1 第一步：主表增量字段

- 为 `inf_project` 增加新增摘要字段
- 初始化历史数据：
  - `space_init_status = 'PENDING'`
  - `archive_status = 'UNARCHIVED'`
  - `document_completion_rate = 0.00`

### 10.2 第二步：新增子表

- 创建目录实例表、文档表、协同记录表、审核明细表、权限关系表
- 不影响现有 CRUD

### 10.3 第三步：历史数据兼容

- 现有 `directory_template_json` 继续作为项目模板快照
- 存量项目首次进入项目空间时，再按快照懒加载生成目录实例
- 存量 `PROJECT` 域工单按 `request_type` 补齐项目验收语义，不强制一次性迁移全部历史行

### 10.4 第四步：状态与字典兼容映射

- `inf_project.project_status` 一期存储继续沿用现有枚举：`DRAFT`、`RUNNING`、`PENDING_ACCEPTANCE`、`COMPLETED`
- 业务层状态与存储映射：
  - `INIT`、`STARTING` -> `project_status = DRAFT`，通过 `project_phase` 区分启动细分阶段
  - `EXECUTING` -> `project_status = RUNNING`
  - `ACCEPTANCE_REVIEW`、`RECTIFICATION` -> `project_status = PENDING_ACCEPTANCE`，通过 `acceptance_status` 和审核明细表表达差异
  - `CLOSING`、`ARCHIVED` -> `project_status = COMPLETED`，通过 `archive_status` 区分是否已归档
- `inf_project_template.status` 存储继续沿用现有 `0/1`，接口和前端字典映射为 `ACTIVE/DISABLED`
- `inf_work_order.request_type` 一期仅使用 `ACCEPTANCE`，整改与结项确认不新增新值，避免破坏现有服务逻辑
- 前端如需展示新的业务阶段名称，应由字典映射层完成，不直接修改存量数据库值

## 11. 明确不在本设计中处理的内容

- 不定义物理文件存储实现
- 不定义在线预览、文档转换、全文检索表结构
- 不在数据库层设计独立工作流引擎表
- 不在本阶段设计更复杂的授权规则引擎、冲突优先级策略和跨模块统一 ACL 中心
