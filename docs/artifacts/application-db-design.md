[artifact:DBDesign]
status: READY
owner: tech-lead-architect
scope:
- 设计应用管理扩展所需的数据模型、表结构、约束、索引与迁移策略
- 明确哪些字段保留在 `inf_application`，哪些拆成新表/新对象，以及如何兼容现有基础 CRUD
inputs:
- [artifact:PRD] `docs/artifacts/application-prd.md`
- [artifact:Prototype] `docs/artifacts/application-prototype.md`
- [artifact:SystemArch] `docs/artifacts/application-system-arch.md`
- 全局基线：`docs/artifacts/db-design.md`
- 当前 SQL 基线：`sql/information_management.sql`
- 当前领域模型：`ruoyi-system/src/main/java/com/ruoyi/system/domain/information/InfoApplication.java`
deliverables:
- `inf_application` 保留字段与新增字段建议
- 应用项目关联、依赖关系、异常事件、通知日志表设计
- 数据约束、索引与迁移策略
risks:
- API/数据库依赖在一期采用关系表快照，后续若独立资产化需要再迁移
- 主表摘要字段与关系表并存期间，需要服务层保证数据口径一致
handoff_to:
- engineering-manager
- senior-backend-engineer
exit_criteria:
- 表结构、字段口径、索引、约束和迁移步骤清晰，可直接指导后端设计

## Entry Check

- 当前任务目标明确：是，输出应用管理专属 `[artifact:DBDesign]`
- 必需输入齐备：是
- 本角色权限匹配：是
- 上游审批前置不存在：当前处于 Architecture 阶段

## 1. 数据建模结论

数据库设计采用“主表保留 + 新表外置”的方式：

1. `inf_application`
   继续作为应用主数据表。
2. `inf_application_project_rel`
   维护应用与项目的多对多关系。
3. `inf_application_dependency_rel`
   维护应用与资源/API/数据库/网络/项目的依赖关系。
4. `inf_application_alert_event`
   维护应用级异常事件。
5. `inf_application_notice_log`
   维护通知发送与处理留痕。

一期不新增责任矩阵关系表，固定角色先保留在 `inf_application`。

## 2. `inf_application` 字段边界

### 2.1 必须保留在 `inf_application` 的字段

以下字段继续作为主表字段保留：

- `application_id`
- `application_code`
- `application_name`
- `application_type`
- `classification_level`
- `application_status`
- `project_id`
- `owner_org`
- `owner_name`
- `vendor_name`
- `vendor_owner`
- `portal_owner`
- `resource_owner`
- `data_owner`
- `security_owner`
- `access_url`
- `tech_stack`
- `resource_summary`
- `network_summary`
- `relation_summary`
- `create_by`
- `create_time`
- `update_by`
- `update_time`
- `remark`

保留原因：

- 这些字段已经是现有 CRUD 的稳定主档结构
- 适合列表筛选和基础详情展示
- 是现有实体类和 Mapper 的主映射字段

### 2.2 建议新增到 `inf_application` 的字段

#### `construction_source`

- 类型：`varchar(32) not null`
- 默认值：`SELF_BUILT`
- 说明：建设来源

#### `owner_dept_id`

- 类型：`bigint null`
- 说明：归属部门 ID

#### `ops_owner`

- 类型：`varchar(64) null`
- 说明：应用运维负责人

#### `linkage_status`

- 类型：`varchar(32) not null`
- 默认值：`UNKNOWN`
- 说明：依赖联动汇总状态

#### `last_alert_time`

- 类型：`datetime null`
- 说明：最近异常时间

#### `deleted_flag`

- 类型：`char(1) not null`
- 默认值：`0`
- 说明：逻辑删除标记

### 2.3 主表状态口径

#### `application_status`

表示人工主状态，建议值：

- `BUILDING`
- `RUNNING`
- `ALERT`
- `DISABLED`

#### `linkage_status`

表示系统联动状态，建议值：

- `NORMAL`
- `WARNING`
- `CRITICAL`
- `UNKNOWN`

结论：

- `application_status` 不被联动状态直接覆盖
- `linkage_status` 用于列表异常标记和概览页告警视图

## 3. 新表：`inf_application_project_rel`

### 3.1 设计目的

- 解决当前 `project_id` 只能表示单项目的问题
- 保留 `project_id` 作为主项目字段，实现对旧 CRUD 的兼容

### 3.2 字段建议

- `rel_id bigint` 主键
- `application_id bigint not null`
- `project_id bigint not null`
- `relation_type varchar(32) not null`
- `active_flag char(1) not null default '1'`
- `remark varchar(500) null`
- `create_by varchar(64)`
- `create_time datetime`
- `update_by varchar(64)`
- `update_time datetime`

### 3.3 字段说明

- `relation_type` 建议值：
  - `PRIMARY`
  - `RELATED`
  - `HISTORICAL`

### 3.4 约束建议

- 服务层保证同一应用只有一个激活中的 `PRIMARY`
- 同一应用下，同一项目关系不允许重复

### 3.5 索引建议

- `uk_app_project_rel (application_id, project_id)`
- `idx_app_project_application (application_id, active_flag)`
- `idx_app_project_project (project_id, active_flag)`

## 4. 新表：`inf_application_dependency_rel`

### 4.1 设计目的

- 统一管理应用对资源/API/数据库/网络/项目/其他对象的依赖关系

### 4.2 字段建议

- `dependency_id bigint` 主键
- `application_id bigint not null`
- `dependency_type varchar(32) not null`
- `target_id bigint null`
- `target_code varchar(64) null`
- `target_name varchar(128) not null`
- `target_source varchar(32) not null`
- `target_key varchar(128) null`
- `dependency_direction varchar(32) not null`
- `dependency_role varchar(32) not null`
- `importance_level varchar(16) not null`
- `status_link_enabled char(1) not null default '1'`
- `alert_link_enabled char(1) not null default '1'`
- `dependency_status varchar(32) not null default 'UNKNOWN'`
- `last_sync_time datetime null`
- `target_snapshot_json text null`
- `remark varchar(500) null`
- `create_by varchar(64)`
- `create_time datetime`
- `update_by varchar(64)`
- `update_time datetime`

### 4.3 类型建议

#### `dependency_type`

- `RESOURCE`
- `API`
- `DATABASE`
- `NETWORK`
- `PROJECT`
- `APPLICATION`
- `OTHER`

#### `target_source`

- `INTERNAL`
- `EXTERNAL`
- `MANUAL`

#### `dependency_direction`

- `UPSTREAM`
- `DOWNSTREAM`
- `BIDIRECTIONAL`

#### `dependency_role`

- `RUNTIME`
- `DATA`
- `ACCESS`
- `NETWORK`

### 4.4 约束建议

- `RESOURCE`、`NETWORK`、`PROJECT`、`APPLICATION` 类型依赖，`target_id` 必填
- `API`、`DATABASE`、`OTHER` 类型依赖允许 `target_id` 为空，但 `target_name` 必填
- 同一应用下，相同类型 + 相同目标不允许重复

### 4.5 索引建议

- `idx_app_dependency_application (application_id)`
- `idx_app_dependency_type_target (dependency_type, target_id)`
- `idx_app_dependency_status (dependency_status)`
- `idx_app_dependency_linkage (status_link_enabled, alert_link_enabled)`
- `uk_app_dependency_unique (application_id, dependency_type, target_id, target_key)`

### 4.6 一期为什么使用独立关系表

结论：

- 依赖关系必须使用独立关系表
- 不应继续仅靠 `resource_summary` / `network_summary` / `relation_summary` 文本维护

原因：

- 文本摘要无法支持列表过滤、状态联动、异常定位和统计分析
- 当前单体项目已有资源、网络、项目主数据，可直接引用
- 对于 API/数据库等暂未主数据化的对象，可用 `target_snapshot_json` 承载

## 5. 新表：`inf_application_alert_event`

### 5.1 设计目的

- 记录应用异常事件，而不是把异常历史塞入通知表或主表

### 5.2 字段建议

- `alert_id bigint` 主键
- `application_id bigint not null`
- `source_type varchar(32) not null`
- `source_object_type varchar(32) not null`
- `source_object_id bigint null`
- `event_code varchar(64) null`
- `event_title varchar(128) not null`
- `event_level varchar(16) not null`
- `event_status varchar(32) not null`
- `impact_summary varchar(500) null`
- `event_time datetime not null`
- `ack_time datetime null`
- `resolved_time datetime null`
- `handler_name varchar(64) null`
- `payload_json text null`
- `remark varchar(500) null`
- `create_by varchar(64)`
- `create_time datetime`
- `update_by varchar(64)`
- `update_time datetime`

### 5.3 状态建议

- `OPEN`
- `ACKED`
- `RESOLVED`
- `IGNORED`

### 5.4 索引建议

- `idx_app_alert_application (application_id, event_status)`
- `idx_app_alert_time (event_time)`
- `idx_app_alert_source (source_type, source_object_id)`
- `idx_app_alert_level (event_level)`

## 6. 新表：`inf_application_notice_log`

### 6.1 设计目的

- 记录通知发送对象、渠道、发送状态和处理留痕

### 6.2 字段建议

- `notice_id bigint` 主键
- `alert_id bigint not null`
- `application_id bigint not null`
- `receiver_name varchar(64) not null`
- `receiver_role varchar(32) not null`
- `channel_type varchar(32) not null`
- `send_status varchar(32) not null`
- `biz_status varchar(32) not null`
- `sent_time datetime null`
- `read_time datetime null`
- `processed_time datetime null`
- `content_summary varchar(500) null`
- `remark varchar(500) null`
- `create_by varchar(64)`
- `create_time datetime`
- `update_by varchar(64)`
- `update_time datetime`

### 6.3 状态建议

#### `send_status`

- `PENDING`
- `SENT`
- `FAILED`

#### `biz_status`

- `UNREAD`
- `READ`
- `PROCESSING`
- `DONE`

### 6.4 索引建议

- `idx_app_notice_alert (alert_id)`
- `idx_app_notice_application (application_id, biz_status)`
- `idx_app_notice_receiver (receiver_name, biz_status)`
- `idx_app_notice_sent_time (sent_time)`

## 7. 责任矩阵为什么一期不拆表

一期责任矩阵继续使用以下固定字段：

- `owner_name`
- `vendor_name`
- `vendor_owner`
- `portal_owner`
- `resource_owner`
- `data_owner`
- `security_owner`
- `ops_owner`

原因：

- 当前角色集合固定
- 旧页面和旧接口已经围绕这些字段实现
- 一期重点是关系治理、状态联动和通知闭环

二期触发拆表条件：

- 一个角色需要多人
- 需要升级链
- 需要通知顺序
- 需要按责任团队建模

## 8. 迁移策略

### 8.1 增量迁移顺序

1. 为 `inf_application` 新增字段，全部允许兼容旧数据
2. 新建 `inf_application_project_rel`
3. 新建 `inf_application_dependency_rel`
4. 新建 `inf_application_alert_event`
5. 新建 `inf_application_notice_log`
6. 将现有 `project_id` 回填到 `inf_application_project_rel`，关系类型写 `PRIMARY`

### 8.2 兼容策略

- 保留 `project_id`
- 保留固定责任字段
- 保留 `resource_summary`、`network_summary`、`relation_summary`
- 不修改现有主键、唯一键语义
- 新能力全部走增量表

### 8.3 删除策略

不建议继续直接物理删除已存在关系或异常记录的应用，建议：

- 一期先在服务层做删除阻断
- 二期正式切换到 `deleted_flag` 逻辑删除

## 9. 统计口径

一期统计分析基于实时聚合：

- 应用总量：`inf_application`
- 建设来源分布：`construction_source`
- 分类分级分布：`classification_level`
- 归属单位/部门分布：`owner_org`、`owner_dept_id`
- 异常量：`inf_application_alert_event` 中未关闭事件
- 责任团队风险：按 `ops_owner`、`resource_owner`、`data_owner`、`security_owner` 聚合

不新增统计宽表。

## 10. 数据库设计结论

数据库设计的核心不是“把所有治理能力都塞进 `inf_application`”，而是：

1. 主表保留主档、固定责任字段、主项目和摘要缓存。
2. 项目关联、依赖关系、异常事件、通知日志拆表。
3. 责任矩阵关系表延后到第二阶段。

这条路径最符合当前 RuoYi-Vue 单体项目的可落地性与兼容性。
