[artifact:SystemArch]
status: READY
owner: tech-lead-architect
scope:
- 为应用管理定义适配当前 RuoYi-Vue 单体项目的系统边界、模块职责、依赖关系与数据流
- 明确“基础应用台账 CRUD”如何演进为“应用台账 + 责任矩阵 + 项目关联 + 资源/API/数据库/网络依赖 + 状态联动 + 异常通知 + 统计分析”
inputs:
- [artifact:PRD] `docs/artifacts/application-prd.md`
- [artifact:UserStory] `docs/artifacts/application-user-story.md`
- [artifact:Prototype] `docs/artifacts/application-prototype.md`
- [artifact:PRD] `docs/artifacts/prd.md`
- [artifact:Prototype] `docs/artifacts/prototype.md`
- [artifact:APIDoc] `docs/artifacts/api-doc.md`
- [artifact:DBDesign] `docs/artifacts/db-design.md`
- 当前前端基线：`ruoyi-ui/src/views/information/application/index.vue`
- 当前后端基线：`ruoyi-admin/src/main/java/com/ruoyi/web/controller/information/InfoApplicationController.java`
- 当前领域模型：`ruoyi-system/src/main/java/com/ruoyi/system/domain/information/InfoApplication.java`
- 当前持久层：`ruoyi-system/src/main/resources/mapper/information/InfoApplicationMapper.xml`
deliverables:
- 应用管理模块边界、子模块职责与集成方式
- `inf_application` 复用策略与增量演进方案
- 状态联动、异常通知、统计分析的数据流与接口边界
risks:
- 当前仓库没有统一监控事件总线，一期状态联动需要依赖关系表 + 显式重算/定时重算实现
- API 和数据库依赖暂无独立主数据域，一期需采用“关系表 + 快照”的轻量模式
handoff_to:
- engineering-manager
- senior-backend-engineer
- senior-frontend-engineer
exit_criteria:
- 系统边界、模块职责、数据流、依赖关系、兼容路径和分阶段范围明确

## Entry Check

- 当前任务目标明确：是，输出应用管理专属 `[artifact:SystemArch]`
- 必需输入齐备：是，应用管理 PRD、用户故事、原型和现有实现基线均已存在
- 本角色权限匹配：是，`tech-lead-architect` 负责架构设计，不直接实现业务代码
- 上游审批前置不存在：当前处于 Architecture 阶段，可输出 `READY`

## 1. 架构结论

应用管理在当前单体项目内应演进为 5 个协同子模块，而不是继续把所有能力堆到 `InfoApplicationServiceImpl + index.vue`：

1. `application-ledger`
   负责应用主档、固定责任字段、主项目、列表筛选和摘要缓存。
2. `application-relation`
   负责项目关联、资源/API/数据库/网络依赖关系。
3. `application-status`
   负责人工主状态与依赖联动状态的融合计算。
4. `application-alert`
   负责异常事件、通知发送记录和处理留痕。
5. `application-statistics`
   负责应用治理统计和管理视图聚合。

结论：

- 保留当前单体工程，不拆微服务
- 保留现有基础 CRUD，不做推倒重来
- 新能力通过“主表增量字段 + 子资源表 + 新接口”渐进接入

## 2. 系统边界

### 2.1 域内职责

- 应用台账主档维护
- 固定责任矩阵维护
- 主项目与扩展项目关联
- 依赖对象维护
- 应用运行概览
- 异常通知与处理闭环
- 统计分析

### 2.2 域外职责

- `inf_project` 的项目主数据维护
- `inf_resource` 的资源主数据维护
- `inf_network_resource` 的网络主数据维护
- 外部监控平台、短信平台、企业微信平台
- 统一认证平台自身的账号与权限主数据

应用管理只消费这些域外对象或事件，不回写其主数据。

## 3. `inf_application` 的复用与扩展

### 3.1 继续保留在 `inf_application` 的字段

以下字段继续作为应用主档字段保留：

- 标识字段：`application_id`、`application_code`、`application_name`
- 分类字段：`application_type`、`classification_level`
- 人工主状态：`application_status`
- 主项目字段：`project_id`
- 归属字段：`owner_org`
- 固定责任字段：`owner_name`、`vendor_name`、`vendor_owner`、`portal_owner`、`resource_owner`、`data_owner`、`security_owner`
- 访问与技术字段：`access_url`、`tech_stack`
- 摘要字段：`resource_summary`、`network_summary`、`relation_summary`
- 审计字段和 `remark`

保留原因：

- 已被现有实体、Mapper、Controller、前端表单和列表使用
- 适合作为“应用主数据 + 快速检索字段”
- 可以兼容旧页面和旧接口

### 3.2 建议新增到 `inf_application` 的字段

建议新增：

- `construction_source`
  建设来源，支持 `SELF_BUILT`、`GROUP_BUILT`、`MOT_BUILT`、`OTHER`
- `owner_dept_id`
  归属部门 ID
- `ops_owner`
  应用运维负责人
- `linkage_status`
  联动状态，支持 `NORMAL`、`WARNING`、`CRITICAL`、`UNKNOWN`
- `last_alert_time`
  最近异常时间
- `deleted_flag`
  逻辑删除标记

原因：

- 这些字段是列表筛选、统计分析、通知路由和治理视图的高频维度
- 继续放在主表最利于当前单体项目落地

### 3.3 不应继续塞到主表的内容

以下内容应拆出新表：

- 多项目关联
- 多条依赖关系
- 异常事件历史
- 通知发送记录

## 4. 新对象与模块边界

### 4.1 一期必须拆出的新对象

- `ApplicationProjectRel`
  对应应用与项目的多对多关系
- `ApplicationDependencyRel`
  对应资源/API/数据库/网络/项目依赖
- `ApplicationAlertEvent`
  对应应用级异常事件
- `ApplicationNoticeLog`
  对应通知发送与处理记录

### 4.2 二期再拆出的对象

- `ApplicationOwnerRel`
  用于多联系人、多升级链、责任团队建模

一期不拆 `ApplicationOwnerRel`，原因是当前责任角色集合固定，先用主表固定字段更稳。

## 5. 模块职责与代码落点

### 5.1 Controller 边界

建议在 `ruoyi-admin/src/main/java/com/ruoyi/web/controller/information` 下拆分：

- `InfoApplicationController`
  只负责主档 CRUD、基础详情
- `InfoApplicationRelationController`
  负责项目关联、依赖关系
- `InfoApplicationAlertController`
  负责异常事件、通知记录、处理动作
- `InfoApplicationStatsController`
  负责统计分析

### 5.2 Service 边界

建议在 `ruoyi-system/.../service/information` 拆分：

- `ApplicationLedgerService`
- `ApplicationRelationService`
- `ApplicationStatusService`
- `ApplicationAlertService`
- `ApplicationStatsQueryService`

说明：

- 不要求拆 Maven 模块
- 但必须避免继续把关系、状态、通知逻辑全部堆在 `InfoApplicationServiceImpl`

### 5.3 前端边界

建议在 `ruoyi-ui/src/views/information/application` 内演进为：

- `index.vue`
  列表页和基础操作入口
- `detail.vue` 或 `components/ApplicationDetailDrawer.vue`
  详情页骨架
- `components/ResponsibilityMatrix.vue`
- `components/ProjectRelationTab.vue`
- `components/DependencyTab.vue`
- `components/StatusOverviewTab.vue`
- `components/AlertLogTab.vue`
- `statistics.vue`

## 6. 关键数据流

### 6.1 主档写入流

前端表单 -> `InfoApplicationController` -> `ApplicationLedgerService` -> `inf_application`

职责：

- 校验编码唯一性
- 保存建设来源、归属部门、运维负责人
- 保持旧 CRUD 兼容

### 6.2 项目关联流

详情页项目页签 -> `InfoApplicationRelationController` -> `ApplicationRelationService` -> `inf_application_project_rel`

规则：

- `inf_application.project_id` 继续表示主项目
- 关系表维护全部项目关系
- 主项目变化时同步回写 `project_id`

### 6.3 依赖关系流

详情页依赖页签 -> `InfoApplicationRelationController` -> `ApplicationRelationService` -> `inf_application_dependency_rel`

规则：

- `RESOURCE`、`NETWORK`、`PROJECT` 类型依赖校验目标对象存在
- `API`、`DATABASE` 类型在一期允许手工快照录入
- 保存后触发状态重算

### 6.4 状态联动流

状态来源：

- 人工主状态：`inf_application.application_status`
- 依赖对象状态：`inf_resource.monitor_status`、`inf_network_resource.resource_status/security_status`
- 依赖关系开关：`status_link_enabled`、`alert_link_enabled`

处理链路：

1. 依赖新增/修改/删除后触发 `ApplicationStatusService.recalculate(applicationId)`
2. 服务层按依赖重要级别与对象状态计算 `linkage_status`
3. 将 `linkage_status`、`last_alert_time` 回写 `inf_application`

### 6.5 异常通知流

1. 异常状态产生后写入 `inf_application_alert_event`
2. `ApplicationAlertService` 根据责任字段路由接收人
3. 生成 `inf_application_notice_log`
4. 一期仅写站内待办/站内消息记录
5. 用户在详情页或通知中心确认、处理、关闭

通知优先路由：

- `ops_owner`
- `owner_name`
- 与异常类型对应的 `resource_owner` / `data_owner` / `security_owner`

### 6.6 统计分析流

`ApplicationStatsQueryService` 基于以下数据实时聚合：

- `inf_application`
- `inf_application_alert_event`
- `inf_application_project_rel`
- `inf_application_dependency_rel`

一期不建统计宽表。

## 7. 接口边界

### 7.1 保持兼容的现有接口

- `GET /information/application/list`
- `GET /information/application/{applicationId}`
- `POST /information/application`
- `PUT /information/application`
- `DELETE /information/application/{applicationIds}`

这些接口继续存在，但语义收口为“主档和固定责任字段”。

### 7.2 必须新增的接口边界

- `/information/application/{id}/detail`
- `/information/application/{id}/projects`
- `/information/application/{id}/dependencies`
- `/information/application/{id}/status-overview`
- `/information/application/{id}/alerts`
- `/information/application/{id}/notices`
- `/information/application/statistics/overview`

结论：

- 主档 CRUD 与复杂关系接口分离
- 不再使用一个大表单承担所有页签数据提交

## 8. 当前页面和接口如何扩展

### 8.1 现有页面扩展

当前 `index.vue` 继续作为列表入口，但需要新增：

- 建设来源筛选
- 归属单位/部门筛选
- 主项目筛选
- 联动状态和异常标识列
- “详情”“依赖”“通知”“统计”入口

### 8.2 现有接口扩展

- 列表接口增加查询维度和展示字段
- 主档详情接口保留基础字段读取
- 新详情聚合接口负责标签页数据
- 新子资源接口负责项目关联、依赖关系、异常和通知

## 9. 分阶段实施范围

### 9.1 第一阶段必须实现

- `inf_application` 增量字段
- 项目关系表
- 依赖关系表
- 异常事件与通知日志表
- 详情页和统计页
- 状态重算能力
- 站内通知处理闭环

### 9.2 第二阶段放后续

- 责任矩阵独立关系表
- 外部监控事件推送
- 短信/企业微信发送
- 依赖拓扑可视化
- 统计宽表

## 10. 架构结论

应用管理的正确演进路径不是“继续给 `inf_application` 堆字段”，而是：

1. `inf_application` 保持主档和高频筛选字段。
2. 项目关系、依赖关系、异常事件、通知记录拆出独立对象。
3. 基础 CRUD 保持兼容。
4. 新能力通过详情页与子资源接口接入。

该方案能够在当前 RuoYi-Vue 单体项目内直接落地，并支持后续阶段继续演进。
