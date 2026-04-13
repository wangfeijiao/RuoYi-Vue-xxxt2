[artifact:DBDesign]
status: READY
owner: tech-lead-architect
scope:
- 设计一期 MVP 的业务表、关联关系、状态字段、索引与初始化 SQL 范围
- 覆盖五大业务域台账、通用工单、数据版本与看板聚合所需数据基础
inputs:
- [artifact:PRD]
- [artifact:Prototype]
- [artifact:SystemArch]
- RuoYi 现有 `sys_*` 基础表
deliverables:
- 8 张业务主表和 1 张版本表的设计方案
- 通用工单模型与项目主关联模型
- 菜单权限初始化策略
risks:
- JSON 字段可快速承载动态参数，但后续做细粒度检索时可能需要拆表
- 文档目录和执行结果采用文本/JSON 存储，一期不适合大规模二次分析
handoff_to:
- engineering-manager
- senior-backend-engineer
exit_criteria:
- 表结构、主外键策略、状态口径和索引策略明确

## 1. 表清单

### 1.1 `inf_project`

存储信息化项目主档。

关键字段：

- `project_id` 主键
- `project_code` 项目编号，唯一索引
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
- `remark`
- 审计字段

索引：

- 唯一索引 `uk_project_code`
- 普通索引：`project_status`、`project_type`

### 1.2 `inf_project_template`

存储项目模板。

关键字段：

- `template_id`
- `template_name`
- `project_type`
- `phase_name`
- `version_no`
- `directory_json`
- `status`
- `remark`
- 审计字段

索引：

- `project_type`
- `status`

### 1.3 `inf_resource`

存储 IT 资源台账。

关键字段：

- `resource_id`
- `resource_code`，唯一
- `resource_name`
- `resource_type`
- `resource_status`
- `monitor_status`
- `project_id`
- `owner_dept_id`
- `owner_name`
- `maintainer_name`
- `cpu_cores`
- `memory_gb`
- `storage_gb`
- `os_name`
- `ip_address`
- `software_name`
- `software_version`
- `license_count`
- `delivery_time`
- `relation_summary`
- `performance_summary`
- `remark`
- 审计字段

索引：

- 唯一索引 `uk_resource_code`
- 普通索引：`resource_type`、`resource_status`、`project_id`

### 1.4 `inf_application`

存储应用台账。

关键字段：

- `application_id`
- `application_code`，唯一
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
- `remark`
- 审计字段

索引：

- 唯一索引 `uk_application_code`
- 普通索引：`application_status`、`classification_level`

### 1.5 `inf_data_asset`

存储数据资产目录。

关键字段：

- `asset_id`
- `asset_code`，唯一
- `asset_name`
- `business_domain`
- `asset_type`
- `asset_status`
- `security_level`
- `source_system`
- `data_format`
- `schema_desc`
- `dictionary_desc`
- `update_frequency`
- `data_volume`
- `storage_location`
- `share_mode`
- `share_condition`
- `owner_dept_name`
- `owner_name`
- `contact_phone`
- `tag_names`
- `lineage_desc`
- `quality_rule_desc`
- `remark`
- 审计字段

索引：

- 唯一索引 `uk_asset_code`
- 普通索引：`asset_status`、`security_level`、`business_domain`

### 1.6 `inf_data_asset_version`

存储数据资产版本快照。

关键字段：

- `version_id`
- `asset_id`
- `version_no`
- `change_type`
- `snapshot_json`
- `change_reason`
- `changed_by`
- `changed_time`

索引：

- `asset_id`
- `changed_time`

### 1.7 `inf_network_resource`

存储网络资源和安全策略台账。

关键字段：

- `network_id`
- `network_code`，唯一
- `network_name`
- `resource_type`
- `resource_status`
- `security_status`
- `project_id`
- `owner_name`
- `ip_segment`
- `ip_count`
- `vlan_no`
- `bandwidth_mbps`
- `device_name`
- `device_type`
- `rule_summary`
- `access_policy`
- `topology_doc`
- `allocation_doc`
- `monitor_summary`
- `remark`
- 审计字段

索引：

- 唯一索引 `uk_network_code`
- 普通索引：`resource_type`、`resource_status`、`project_id`

### 1.8 `inf_work_order`

通用工单表，统一承载审批流。

关键字段：

- `work_order_id`
- `work_order_no`，唯一
- `domain_type`
- `request_type`
- `order_status`
- `project_id`
- `subject_id`
- `subject_name`
- `subject_type`
- `applicant_dept_id`
- `applicant_name`
- `approver_name`
- `executor_name`
- `request_title`
- `request_reason`
- `current_snapshot_json`
- `request_payload_json`
- `approval_comment`
- `execution_result`
- `expect_finish_time`
- `submitted_time`
- `approved_time`
- `executed_time`
- `remark`
- 审计字段

`domain_type` 取值：

- `PROJECT`
- `RESOURCE`
- `DATA_ASSET`
- `NETWORK`

`request_type` 取值：

- `APPLY`
- `CHANGE`
- `RECYCLE`
- `DELIVER`
- `USE`
- `ACCEPTANCE`

`order_status` 取值：

- `DRAFT`
- `PENDING`
- `APPROVED`
- `REJECTED`
- `PENDING_EXECUTION`
- `COMPLETED`

索引：

- 唯一索引 `uk_work_order_no`
- 普通索引：`domain_type`、`request_type`、`order_status`、`project_id`、`subject_id`

## 2. 关系设计

- `inf_project` 1:N `inf_resource`
- `inf_project` 1:N `inf_application`
- `inf_project` 1:N `inf_network_resource`
- `inf_project` 1:N `inf_work_order`
- `inf_data_asset` 1:N `inf_data_asset_version`
- `inf_resource`/`inf_data_asset`/`inf_network_resource` 通过 `inf_work_order.subject_id` 形成弱关联

一期不使用数据库外键，原因：

- 保持与现有 RuoYi SQL 风格一致
- 便于导入存量数据
- 降低初始化 SQL 对已有环境的侵入风险

约束通过服务层与唯一索引保证。

## 3. 初始化与迁移策略

- 新增单独 SQL 文件，不覆盖现有 `ry_20260321.sql`
- 菜单和按钮权限通过增量 SQL 插入
- 将新菜单授权给管理员角色 `role_id = 1`
- 不新增字典表数据，前端一期以固定选项和后端状态枚举为主

## 4. 审计与追踪

所有业务表复用 RuoYi 审计字段：

- `create_by`
- `create_time`
- `update_by`
- `update_time`
- `remark`

通用工单、数据版本快照承担完整追踪职责。

## 5. 聚合看板口径

Dashboard 不新增表，基于以下统计实时聚合：

- 项目总数、待验收数
- IT 资源总数、在用数、待回收数
- 应用总数、异常数
- 数据资产总数、待审批工单数
- 网络资源总数、待执行工单数
