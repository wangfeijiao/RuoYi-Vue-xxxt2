[artifact:DBDesign]
status: READY
owner: tech-lead-architect
scope:
- 细化 IT 资源管理模块的数据模型、表结构建议、索引与约束
- 基于当前仓库已存在 `inf_resource`、`inf_work_order` 实现给出复用与优化方案
inputs:
- [artifact:PRD] docs/artifacts/it-resource-prd.md
- [artifact:Prototype] docs/artifacts/it-resource-prototype.md
- [artifact:SystemArch] docs/artifacts/it-resource-system-arch.md
- 当前 SQL：sql/information_management.sql
handoff_to:
- engineering-manager
- senior-backend-engineer
deliverables:
- IT 资源管理模块表结构细化建议
- 现状复用说明与增量改造建议
- 索引、唯一性、状态与删除策略说明
risks:
- 当前单表模式适合 MVP，但后续资源类型专属字段增多时会出现字段稀疏
- 当前通用工单表承担多域职责，资源域若要做更细粒度统计需要补字段或建立扩展表
exit_criteria:
- 表结构、索引、业务约束、状态字段口径和改造方向明确

## 入口检查

- 当前任务目标是否明确：是，仅聚焦 IT 资源管理模块的数据库设计。
- 必需 artifact 是否齐备：是，产品与原型输入齐备。
- 本角色是否有权限处理该任务：是。
- 是否存在上游审批或评审前置条件：否。

## 一、现状复用

### 1. 已存在资源主表 `inf_resource`

当前字段已覆盖资源域 MVP 主要需求：

- 主键与编码：`resource_id`、`resource_code`
- 基础信息：`resource_name`、`resource_type`
- 状态信息：`resource_status`、`monitor_status`
- 归属信息：`project_id`、`owner_dept_id`、`owner_name`、`maintainer_name`
- 配置信息：`cpu_cores`、`memory_gb`、`storage_gb`、`os_name`、`ip_address`
- 软件信息：`software_name`、`software_version`、`license_count`
- 摘要信息：`relation_summary`、`performance_summary`
- 审计字段：`create_by`、`create_time`、`update_by`、`update_time`、`remark`

### 2. 已存在通用工单表 `inf_work_order`

当前已满足资源工单的基本承载：

- 工单号：`work_order_no`
- 领域：`domain_type`
- 请求类型：`request_type`
- 状态：`order_status`
- 资源引用：`subject_id`、`subject_name`、`subject_type`
- 项目引用：`project_id`
- 人员：`applicant_name`、`approver_name`、`executor_name`
- 内容：`request_title`、`request_reason`、`current_snapshot_json`、`request_payload_json`
- 留痕：`approval_comment`、`execution_result`
- 时间：`submitted_time`、`approved_time`、`executed_time`

## 二、建议优化

### 1. 资源主表保留，不拆子表

对于当前仓库，应继续以 `inf_resource` 作为资源域唯一主表。原因：

- 当前后端实体、Mapper、前端表单已围绕该表实现
- 需求仍处于 MVP 阶段，拆多张类型子表会显著增加改造成本
- 现有字段已经足够承载服务器、虚拟机、存储、软件许可的第一阶段管理

### 2. 工单表继续复用，不单独新建资源工单表

继续复用 `inf_work_order`，但在资源域要明确：

- `domain_type` 固定为 `RESOURCE`
- `request_type` 仅允许 `APPLY`、`CHANGE`、`RECYCLE`

### 3. 需要增量增强的字段与约束

建议对现有表做以下增量改造：

- `inf_resource`
  - 新增 `deleted_flag`，用于逻辑删除
  - 新增 `resource_source`，标记 `MANUAL` / `REUSE_POOL`
  - 新增 `last_work_order_id`，便于列表快速查看最近工单
- `inf_work_order`
  - 新增 `biz_status_before`
  - 新增 `biz_status_after`
  - 新增 `request_payload_version`
  - 新增 `timeout_flag`

如果本轮不做 DDL 变更，也应先在服务层预留这些语义。

## 三、表结构建议

### 1. `inf_resource` 建议口径

#### 1.1 主键

- `resource_id bigint`

#### 1.2 业务唯一键

- `resource_code varchar(64)` 唯一

#### 1.3 核心字段

- `resource_name varchar(128) not null`
- `resource_type varchar(32) not null`
- `resource_status varchar(32) not null`
- `monitor_status varchar(32) not null`
- `project_id bigint null`
- `owner_dept_id bigint null`
- `owner_name varchar(64) null`
- `maintainer_name varchar(64) null`

#### 1.4 配置字段

- `cpu_cores int null`
- `memory_gb int null`
- `storage_gb int null`
- `os_name varchar(64) null`
- `ip_address varchar(64) null`
- `software_name varchar(128) null`
- `software_version varchar(64) null`
- `license_count int null`
- `delivery_time datetime null`

#### 1.5 摘要字段

- `relation_summary varchar(500) null`
- `performance_summary varchar(500) null`
- `remark varchar(500) null`

#### 1.6 推荐新增字段

- `deleted_flag char(1) default '0'`
- `resource_source varchar(32) default 'MANUAL'`
- `last_work_order_id bigint null`

### 2. `inf_work_order` 建议口径

#### 2.1 主键

- `work_order_id bigint`

#### 2.2 业务唯一键

- `work_order_no varchar(64)` 唯一

#### 2.3 资源域必填规则

当 `domain_type='RESOURCE'` 时：

- `request_type` 必填且限定值域
- `request_title` 必填
- `applicant_name` 必填
- `request_reason` 必填

当 `request_type in ('CHANGE','RECYCLE')` 时：

- `subject_id` 必填

当 `request_type='APPLY'` 时：

- `subject_id` 可为空
- `project_id` 应为必填

#### 2.4 推荐新增字段

- `biz_status_before varchar(32)`
- `biz_status_after varchar(32)`
- `timeout_flag char(1) default '0'`
- `request_payload_version varchar(16)`

## 四、索引建议

### 1. `inf_resource`

保留现有索引：

- `uk_resource_code`
- `idx_resource_type`
- `idx_resource_status`
- `idx_resource_project`

建议新增：

- `idx_resource_monitor_status (monitor_status)`
- `idx_resource_owner_name (owner_name)`
- `idx_resource_delivery_time (delivery_time)`

如果业务确认需要对运行中资源 IP 唯一做数据库辅助约束，可考虑：

- 普通索引 `idx_resource_ip_address (ip_address)`

IP 唯一性仍建议以服务层规则为主，因为不是所有资源类型都要求 IP。

### 2. `inf_work_order`

保留现有索引：

- `uk_work_order_no`
- `idx_work_order_domain`
- `idx_work_order_request`
- `idx_work_order_status`
- `idx_work_order_project`
- `idx_work_order_subject`

建议新增：

- `idx_work_order_applicant_name (applicant_name)`
- `idx_work_order_submitted_time (submitted_time)`
- `idx_work_order_expect_finish_time (expect_finish_time)`
- 组合索引 `idx_work_order_domain_status (domain_type, order_status)`

## 五、状态字段建议

### 1. `inf_resource.resource_status`

建议固定值：

- `IDLE`
- `PENDING_DELIVERY`
- `IN_USE`
- `CHANGING`
- `PENDING_RECYCLE`
- `RECYCLED`

### 2. `inf_resource.monitor_status`

建议固定值：

- `NORMAL`
- `WARNING`
- `CRITICAL`
- `UNKNOWN`

### 3. `inf_work_order.order_status`

建议固定值：

- `DRAFT`
- `PENDING`
- `REJECTED`
- `PENDING_EXECUTION`
- `COMPLETED`

当前表设计可以容纳这些值，但需要在服务层、前端下拉和测试用例中统一。

## 六、删除与历史策略

### 1. 资源删除策略

不建议继续保留“被工单引用资源可物理删除”的策略。建议改为：

- 未被工单引用且未投入使用的资源，可逻辑删除
- 已有工单历史的资源，不物理删除
- 已回收资源优先使用 `RECYCLED` 状态保留历史

### 2. 工单删除策略

资源工单原则上不建议物理删除。建议：

- `DRAFT` 状态允许删除
- `PENDING` 以后只允许作废，不允许删除

当前仓库已开放删除接口，这部分需要在服务层补限制。

## 七、SQL 迁移建议

### 1. 当前可直接复用的 SQL

- `sql/information_management.sql` 中 `inf_resource`
- `sql/information_management.sql` 中 `inf_work_order`
- 资源菜单与权限初始化

### 2. 下一轮推荐的增量 SQL

- 为 `inf_resource` 增加逻辑删除字段
- 为 `inf_work_order` 增加状态前后值与超时标记字段
- 为资源表和工单表增加辅助索引

## 八、数据库设计结论

数据库层不需要重构为多表复杂模型。正确路径是：

- 继续复用现有 `inf_resource` 和 `inf_work_order`
- 增加少量辅助字段和索引
- 通过服务层规则弥补弱外键和自由文本带来的数据一致性风险

这条路径与当前仓库实现兼容，落地成本最低。
