[artifact:APIDoc]
status: READY
owner: tech-lead-architect
scope:
- 定义“信息化项目管理”模块基于现有 `/information/project/*` 路由的接口契约、权限边界与扩展策略
- 明确项目台账、项目空间、项目文档、项目协同、验收申请、多条线审核、权限配置、统计分析的接口设计
inputs:
- [artifact:PRD] `docs/artifacts/project-management-prd.md`
- [artifact:UserStory] `docs/artifacts/project-management-user-story.md`
- [artifact:Prototype] `docs/artifacts/project-management-prototype.md`
- [artifact:SystemArch] `docs/artifacts/project-management-system-arch.md`
- [artifact:DBDesign] `docs/artifacts/project-management-db-design.md`
- 当前控制器：`ruoyi-admin/src/main/java/com/ruoyi/web/controller/information/InfoProjectController.java`
- 当前前端 API：`ruoyi-ui/src/api/information/project.js`
- 当前持久层：`ruoyi-system/src/main/resources/mapper/information/InfoWorkOrderMapper.xml`
handoff_to:
- engineering-manager
- senior-backend-engineer
- senior-frontend-engineer
deliverables:
- 保持兼容的现有接口边界
- 项目明细、项目空间、文档、协同、审核明细、权限配置、统计接口清单
- 权限建议、请求响应字段和错误码口径
risks:
- 现有前端 API 仅覆盖台账、模板、工单，新增明细接口后需要前端按页签拆分调用
- 现有 `approve`/`execute` 是主单动作，若实现期直接拿来承载多条线审核，会破坏主单与子单边界
exit_criteria:
- 路径、权限、请求参数、响应结构、兼容原则和错误语义明确，可直接指导实现层拆分接口

## Entry Check

- 当前任务目标明确：输出“信息化项目管理”专属 `[artifact:APIDoc]`
- 必需 artifact 齐备：PRD、UserStory、Prototype、SystemArch、DBDesign 已齐备
- 本角色权限匹配：`tech-lead-architect` 负责接口契约设计
- 上游审批前置不存在：当前处于 Architecture 阶段

## 1. 通用约定

- Base URL：`/information/project`
- 鉴权方式：沿用 RuoYi 登录态与 `@PreAuthorize("@ss.hasPermi(...)")`
- 列表响应：`TableDataInfo`
- 明细/动作响应：`AjaxResult`
- 兼容要求：保留现有 `/list`、`/{id}`、`/template/*`、`/order/*` 语义，新增能力通过子资源路由扩展

### 1.1 一期状态兼容约定

- `projectStatus` 在一期接口请求/响应中继续使用现有存储口径：`DRAFT`、`RUNNING`、`PENDING_ACCEPTANCE`、`COMPLETED`
- 原型与 PRD 中的业务阶段通过以下映射落地：
  - `INIT`、`STARTING` -> `projectStatus=DRAFT`
  - `EXECUTING` -> `projectStatus=RUNNING`
  - `ACCEPTANCE_REVIEW`、`RECTIFICATION` -> `projectStatus=PENDING_ACCEPTANCE`
  - `CLOSING`、`ARCHIVED` -> `projectStatus=COMPLETED`
- `projectPhase` 继续承担细分阶段表达，例如 `INIT`、`STARTING`、`EXECUTION`、`CLOSING`
- 模板 `status` 一期继续沿用 `0/1` 存储口径，接口补充 `statusLabel=ACTIVE/DISABLED`
- 验收主单 `requestType` 一期继续固定为 `ACCEPTANCE`

## 2. 权限建议

### 2.1 保留现有权限

- `information:project:list`
- `information:project:query`
- `information:project:add`
- `information:project:edit`
- `information:project:remove`
- `information:projectTemplate:list`
- `information:projectTemplate:query`
- `information:projectTemplate:add`
- `information:projectTemplate:edit`
- `information:projectTemplate:remove`
- `information:projectOrder:list`
- `information:projectOrder:add`
- `information:projectOrder:edit`
- `information:projectOrder:approve`
- `information:projectOrder:execute`

### 2.2 建议新增权限

- `information:project:detail`
- `information:project:space`
- `information:project:document`
- `information:project:collaboration`
- `information:project:review`
- `information:project:permission`
- `information:project:stats`

## 3. 现有基础接口的兼容扩展

### 3.1 `GET /information/project/list`

用途：

- 项目台账列表查询

新增查询参数建议：

- `projectCode`
- `projectName`
- `projectType`
- `projectStatus`
- `projectPhase`
- `acceptanceStatus`
- `ownerDeptId`
- `projectManager`
- `archiveStatus`
- `spaceInitStatus`

新增返回字段建议：

- `templateVersionNo`
- `spaceInitStatus`
- `archiveStatus`
- `documentCompletionRate`
- `lastCollaborationTime`

### 3.2 `GET /information/project/{projectId}`

用途：

- 读取项目主档

边界：

- 只返回 `inf_project` 主表字段
- 不直接嵌入目录树、文档列表、协同时间线、审核明细

### 3.3 `POST /information/project`

用途：

- 新增项目主档

请求体建议新增字段：

```json
{
  "projectCode": "PRJ-2026-001",
  "projectName": "园区数智化改造",
  "projectType": "DIGITALIZATION",
  "projectStatus": "DRAFT",
  "projectPhase": "INIT",
  "ownerDeptId": 103,
  "projectManager": "张三",
  "templateId": 12,
  "templateVersionNo": "V1.0",
  "planStartDate": "2026-04-01 00:00:00",
  "planEndDate": "2026-09-30 00:00:00",
  "directoryTemplateJson": "[]",
  "customDirectoryJson": "[]"
}
```

校验建议：

- `projectCode` 必填且唯一
- `projectName` 必填
- `projectType` 必填
- `projectManager` 必填

### 3.4 `PUT /information/project`

用途：

- 修改项目主档

边界：

- 允许修改项目台账与模板快照字段
- 不通过该接口直接批量提交目录实例、文档实例、协同记录和审核明细

### 3.5 `GET /information/project/template/list`

用途：

- 模板列表查询

新增查询参数建议：

- `phaseName`
- `status`
- `projectType`

### 3.6 `POST /information/project/order`

用途：

- 新建项目验收申请主单

兼容扩展建议：

- 继续落到 `inf_work_order`
- `domainType` 固定 `PROJECT`
- `requestType` 固定 `ACCEPTANCE`
- `requestPayloadJson` 中存放材料快照和线路清单

### 3.7 `POST /information/project/order/{workOrderId}/approve`

用途：

- 主单汇总审批

边界：

- 只用于汇总审批，不直接代替各线路审核
- 调用前需确认所有必审线路都已完成

### 3.8 `POST /information/project/order/{workOrderId}/execute`

用途：

- 项目结项执行/归档确认

边界：

- 只在主单审批通过后可执行
- 用于更新项目 `projectStatus`、`acceptanceStatus`、`archiveStatus`

## 4. 新增聚合与子资源接口

### 4.1 项目详情聚合

#### `GET /information/project/{projectId}/detail`

权限：

- `information:project:detail`

用途：

- 给详情页签提供聚合数据

核心返回字段：

- `baseInfo`
- `templateSnapshot`
- `spaceSummary`
- `collaborationSummary`
- `acceptanceSummary`
- `permissionSummary`

### 4.2 项目空间目录接口

#### `GET /information/project/{projectId}/space/directories`

用途：

- 查询项目目录树

#### `POST /information/project/{projectId}/space/directories/custom`

用途：

- 新增自定义目录实例

#### `POST /information/project/{projectId}/space/init`

用途：

- 根据项目模板快照初始化目录实例

### 4.3 项目文档接口

#### `GET /information/project/{projectId}/documents`

用途：

- 查询项目文档列表

#### `POST /information/project/{projectId}/documents`

用途：

- 新增文档实例记录

#### `POST /information/project/documents/{documentId}/compliance`

用途：

- 过程资料管理员提交合规检查结果

#### `POST /information/project/documents/{documentId}/archive`

用途：

- 标记文档归档

### 4.4 项目协同接口

#### `GET /information/project/{projectId}/collaboration`

用途：

- 查询协同记录时间线

#### `POST /information/project/{projectId}/collaboration`

用途：

- 新增协同记录

请求体核心字段：

- `phaseCode`
- `recordType`
- `relatedDomainType`
- `relatedObjectId`
- `relatedObjectName`
- `recordSummary`
- `recordContentJson`

### 4.5 验收审核明细接口

#### `GET /information/project/order/{workOrderId}/reviews`

权限：

- `information:project:review`

用途：

- 查询一轮验收申请的线路审核明细

#### `POST /information/project/order/{workOrderId}/reviews`

用途：

- 初始化或重建审核线路

#### `POST /information/project/order/{workOrderId}/reviews/{reviewId}/decision`

用途：

- 审核人提交单条线路审核结果

边界：

- 单条线路审核完成后，只更新审核明细表
- 主单是否流转到汇总审批由服务层根据线路状态统一判断

### 4.6 统计分析接口

#### `GET /information/project/statistics/overview`

用途：

- 返回项目总量、阶段分布、验收进度、资料完备度摘要

#### `GET /information/project/statistics/compliance`

用途：

- 返回目录/文档合规检查与归档统计

#### `GET /information/project/statistics/acceptance`

用途：

- 返回验收申请、线路审核、退回整改统计

### 4.7 权限配置接口

#### `GET /information/project/{projectId}/permissions`

权限：

- `information:project:permission`

用途：

- 查询项目级、目录级、文档级授权列表

查询参数：

- `scopeType`
- `scopeId`
- `targetType`
- `targetKey`

核心返回字段：

- `permissionId`
- `projectId`
- `scopeType`
- `scopeId`
- `targetType`
- `targetKey`
- `canView`
- `canEdit`
- `canDownload`
- `canDelete`
- `inheritFlag`
- `startTime`
- `endTime`

#### `POST /information/project/{projectId}/permissions`

权限：

- `information:project:permission`

用途：

- 新增授权关系

请求体：

```json
{
  "scopeType": "DIRECTORY",
  "scopeId": 101,
  "targetType": "ROLE",
  "targetKey": "project_observer",
  "canView": "1",
  "canEdit": "0",
  "canDownload": "1",
  "canDelete": "0",
  "inheritFlag": "0",
  "startTime": "2026-04-14 09:00:00",
  "endTime": null,
  "remark": "观察员只读授权"
}
```

约束：

- `scopeType` 仅允许 `PROJECT`、`DIRECTORY`、`DOCUMENT`
- `targetType` 仅允许 `USER`、`ROLE`、`TEAM`
- 同一 `(projectId, scopeType, scopeId, targetType, targetKey)` 不允许重复新增

#### `PUT /information/project/{projectId}/permissions/{permissionId}`

权限：

- `information:project:permission`

用途：

- 变更已有授权动作或有效期

允许更新字段：

- `canView`
- `canEdit`
- `canDownload`
- `canDelete`
- `inheritFlag`
- `startTime`
- `endTime`
- `remark`

#### `DELETE /information/project/{projectId}/permissions/{permissionId}`

权限：

- `information:project:permission`

用途：

- 撤销授权关系

#### `GET /information/project/{projectId}/permission-matrix`

权限：

- `information:project:permission`

用途：

- 给权限配置页一次性返回项目级、目录级、文档级权限矩阵视图

核心返回结构：

- `projectPermissions`
- `directoryPermissions`
- `documentPermissions`
- `defaultRoleSuggestions`

## 5. 错误码建议

建议在 `AjaxResult` 中统一补充 `businessCode`，至少约定以下业务语义：

- `PM_PROJECT_CODE_DUPLICATE`
- `PM_TEMPLATE_NOT_FOUND`
- `PM_SPACE_NOT_INITIALIZED`
- `PM_DIRECTORY_DUPLICATE`
- `PM_REQUIRED_DOCUMENT_MISSING`
- `PM_ACCEPTANCE_LINE_PENDING`
- `PM_REVIEW_NOT_ASSIGNED`
- `PM_ORDER_STATUS_INVALID`
- `PM_ARCHIVE_BLOCKED`
- `PM_PERMISSION_DUPLICATE`
- `PM_PERMISSION_SCOPE_INVALID`

## 6. 明确不在本接口设计中处理的内容

- 不在本阶段定义对象存储上传协议
- 不在本阶段定义在线预览接口
- 不在本阶段把多条线审核映射为完整流程引擎节点模型
- 不在本阶段开放跨模块主数据写接口
