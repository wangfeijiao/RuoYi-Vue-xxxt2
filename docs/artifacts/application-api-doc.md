[artifact:APIDoc]
status: READY
owner: tech-lead-architect
scope:
- 定义应用管理模块在当前 RuoYi-Vue 单体项目内的接口契约、鉴权方式、错误码与兼容扩展路径
- 明确现有应用页面和接口如何从基础 CRUD 演进为详情、关系、状态、异常、统计接口
inputs:
- [artifact:PRD] `docs/artifacts/application-prd.md`
- [artifact:Prototype] `docs/artifacts/application-prototype.md`
- [artifact:SystemArch] `docs/artifacts/application-system-arch.md`
- [artifact:DBDesign] `docs/artifacts/application-db-design.md`
- 当前接口基线：`ruoyi-admin/src/main/java/com/ruoyi/web/controller/information/InfoApplicationController.java`
- 当前前端 API：`ruoyi-ui/src/api/information/application.js`
deliverables:
- 基础 CRUD 扩展契约
- 项目关联、依赖关系、运行概览、异常通知、统计分析接口清单
- 鉴权和错误码建议
risks:
- 当前 RuoYi 默认 `AjaxResult` 结构不区分业务错误码，一期需约定 `businessCode` 或统一错误文案
- 若外部通道暂未接入，一期通知接口只能闭环站内待办
handoff_to:
- engineering-manager
- senior-backend-engineer
- senior-frontend-engineer
exit_criteria:
- 路径、请求参数、响应字段、权限与错误处理口径明确，可直接支持实现设计

## Entry Check

- 当前任务目标明确：是，输出应用管理专属 `[artifact:APIDoc]`
- 必需输入齐备：是
- 本角色权限匹配：是
- 上游审批前置不存在：当前处于 Architecture 阶段

## 1. 通用约定

- Base URL：`/information/application`
- 鉴权方式：沿用 RuoYi 登录态 + `@PreAuthorize("@ss.hasPermi(...)")`
- 列表返回：`TableDataInfo`
- 明细与动作返回：`AjaxResult`
- 推荐在 `AjaxResult` 中补充 `businessCode`；若实现期不扩结构，则至少稳定 `msg` 文案

## 2. 权限建议

### 2.1 保留现有权限

- `information:application:list`
- `information:application:query`
- `information:application:add`
- `information:application:edit`
- `information:application:remove`

### 2.2 建议新增权限

- `information:application:relation`
- `information:application:alert`
- `information:application:stats`

## 3. 现有基础 CRUD 如何扩展

### 3.1 `GET /information/application/list`

用途：

- 列表页查询

新增查询参数建议：

- `applicationCode`
- `applicationName`
- `applicationType`
- `classificationLevel`
- `constructionSource`
- `applicationStatus`
- `linkageStatus`
- `ownerOrg`
- `ownerDeptId`
- `ownerName`
- `opsOwner`
- `projectId`
- `hasOpenAlert`
- `pageNum`
- `pageSize`

新增返回字段建议：

- `constructionSource`
- `ownerDeptId`
- `opsOwner`
- `linkageStatus`
- `lastAlertTime`
- `openAlertCount`

兼容原则：

- 旧调用方不传新参数时仍能正常工作
- 旧列表字段保持不删除

### 3.2 `GET /information/application/{applicationId}`

用途：

- 读取基础主档

新增返回字段建议：

- `constructionSource`
- `ownerDeptId`
- `opsOwner`
- `linkageStatus`
- `lastAlertTime`

边界：

- 该接口只返回主表字段，不返回完整项目关系列表和依赖详情

### 3.3 `POST /information/application`

用途：

- 新增应用主档

请求体建议：

```json
{
  "applicationCode": "APP-001",
  "applicationName": "综合管理平台",
  "applicationType": "PORTAL",
  "classificationLevel": "LEVEL_3",
  "constructionSource": "SELF_BUILT",
  "applicationStatus": "BUILDING",
  "projectId": 1001,
  "ownerOrg": "信息中心",
  "ownerDeptId": 103,
  "ownerName": "张三",
  "opsOwner": "李四",
  "vendorName": "某实施厂商",
  "vendorOwner": "王五",
  "portalOwner": "赵六",
  "resourceOwner": "运维A",
  "dataOwner": "数据B",
  "securityOwner": "安全C",
  "accessUrl": "https://app.example.com",
  "techStack": "Vue + Spring Boot",
  "resourceSummary": "2台应用服务器",
  "networkSummary": "办公网访问",
  "relationSummary": "依赖统一认证和主数据接口"
}
```

校验建议：

- `applicationCode` 必填且唯一
- `applicationName` 必填
- `constructionSource` 必填
- 至少 `ownerName` 或 `opsOwner` 之一必填

### 3.4 `PUT /information/application`

用途：

- 修改应用主档和固定责任字段

边界：

- 不通过该接口提交完整项目关系
- 不通过该接口提交完整依赖关系
- 复杂对象通过子资源接口维护

### 3.5 `DELETE /information/application/{applicationIds}`

用途：

- 删除应用

约束建议：

- 存在关联项目、依赖关系或未关闭异常时，返回业务错误阻止删除
- 二期再切换为逻辑删除

## 4. 必须新增的详情与子资源接口

### 4.1 `GET /information/application/{applicationId}/detail`

权限：

- `information:application:query`

用途：

- 详情页头部聚合数据

返回结构建议：

```json
{
  "baseInfo": {},
  "responsibilityMatrix": {},
  "projectSummary": {
    "primaryProjectId": 1001,
    "projectCount": 2
  },
  "dependencySummary": {
    "resourceCount": 2,
    "apiCount": 3,
    "databaseCount": 1,
    "networkCount": 1
  },
  "statusOverview": {},
  "recentAlerts": []
}
```

### 4.2 项目关联接口

#### `GET /information/application/{applicationId}/projects`

用途：

- 查询应用关联项目列表

#### `POST /information/application/{applicationId}/projects`

权限：

- `information:application:relation`

请求体：

```json
{
  "projectId": 1001,
  "relationType": "PRIMARY",
  "remark": "主建设项目"
}
```

#### `DELETE /information/application/{applicationId}/projects/{relId}`

权限：

- `information:application:relation`

规则：

- 删除主项目关系前必须先切换主项目或清空主项目

### 4.3 依赖关系接口

#### `GET /information/application/{applicationId}/dependencies`

查询参数建议：

- `dependencyType`
- `dependencyStatus`
- `statusLinkEnabled`

#### `POST /information/application/{applicationId}/dependencies`

权限：

- `information:application:relation`

资源/网络/项目依赖请求示例：

```json
{
  "dependencyType": "RESOURCE",
  "targetId": 2001,
  "targetCode": "RES-01",
  "targetName": "应用服务器01",
  "targetSource": "INTERNAL",
  "dependencyDirection": "UPSTREAM",
  "dependencyRole": "RUNTIME",
  "importanceLevel": "HIGH",
  "statusLinkEnabled": "1",
  "alertLinkEnabled": "1",
  "remark": "核心运行资源"
}
```

API/数据库依赖请求示例：

```json
{
  "dependencyType": "API",
  "targetName": "统一认证API",
  "targetSource": "MANUAL",
  "targetKey": "GET:/oauth/userinfo",
  "dependencyDirection": "UPSTREAM",
  "dependencyRole": "ACCESS",
  "importanceLevel": "HIGH",
  "statusLinkEnabled": "0",
  "alertLinkEnabled": "1",
  "targetSnapshotJson": "{\"endpoint\":\"/oauth/userinfo\",\"owner\":\"认证组\"}"
}
```

#### `PUT /information/application/{applicationId}/dependencies/{dependencyId}`

权限：

- `information:application:relation`

#### `DELETE /information/application/{applicationId}/dependencies/{dependencyId}`

权限：

- `information:application:relation`

动作：

- 删除后触发应用联动状态重算

### 4.4 运行概览接口

#### `GET /information/application/{applicationId}/status-overview`

权限：

- `information:application:query`

返回字段建议：

- `applicationStatus`
- `linkageStatus`
- `lastAlertTime`
- `resourceStatusSummary`
- `networkStatusSummary`
- `openAlertCount`
- `latestAlert`

#### `POST /information/application/{applicationId}/status/recalculate`

权限：

- `information:application:relation`

用途：

- 手工触发联动状态重算

说明：

- 用于一期补偿机制和联调阶段排障

### 4.5 异常与通知接口

#### `GET /information/application/{applicationId}/alerts`

权限：

- `information:application:query`

查询参数：

- `eventStatus`
- `eventLevel`
- `beginTime`
- `endTime`

#### `GET /information/application/{applicationId}/notices`

权限：

- `information:application:query`

查询参数：

- `bizStatus`
- `receiverName`

#### `POST /information/application/alerts/{alertId}/ack`

权限：

- `information:application:alert`

请求体：

```json
{
  "handlerName": "李四",
  "remark": "已接手排查"
}
```

#### `POST /information/application/alerts/{alertId}/resolve`

权限：

- `information:application:alert`

请求体：

```json
{
  "handlerName": "李四",
  "remark": "资源恢复，已闭环"
}
```

### 4.6 统计分析接口

#### `GET /information/application/statistics/overview`

权限：

- `information:application:stats`

查询参数：

- `constructionSource`
- `classificationLevel`
- `ownerDeptId`
- `beginDate`
- `endDate`

返回字段建议：

- `applicationTotal`
- `openAlertTotal`
- `byConstructionSource`
- `byClassificationLevel`
- `byOwnerDept`
- `byApplicationStatus`
- `byLinkageStatus`
- `byResponsibleOwner`

## 5. 错误码建议

建议统一业务错误码：

- `APP_CODE_DUPLICATE`
  应用编码重复
- `APP_OWNER_REQUIRED`
  业务负责人或运维负责人缺失
- `APP_PRIMARY_PROJECT_REQUIRED`
  主项目关系缺失
- `APP_PROJECT_REL_DUPLICATE`
  项目关系重复
- `APP_DEPENDENCY_DUPLICATE`
  依赖关系重复
- `APP_DEPENDENCY_TARGET_MISSING`
  依赖目标不存在或已停用
- `APP_DELETE_BLOCKED`
  应用存在关系/异常记录，禁止删除
- `APP_ALERT_NOT_OPEN`
  异常已关闭，不能重复处理
- `APP_STATUS_RECALC_FAILED`
  联动状态重算失败

## 6. 现有页面和接口如何演进

### 6.1 现有 `index.vue`

继续保留，但需要扩展：

- 列表筛选项
- 列表展示字段
- 详情入口
- 统计入口

### 6.2 现有基础 API 文件

当前 `ruoyi-ui/src/api/information/application.js` 继续保留 `list/get/add/update/delete`，并增量增加：

- `getApplicationDetail`
- `listApplicationProjects`
- `addApplicationProject`
- `listApplicationDependencies`
- `addApplicationDependency`
- `getApplicationStatusOverview`
- `listApplicationAlerts`
- `ackApplicationAlert`
- `resolveApplicationAlert`
- `getApplicationStatisticsOverview`

### 6.3 兼容原则

- 基础 CRUD 不删除
- 复杂标签页能力通过新接口扩展
- 不让旧表单接口承载所有新能力

## 7. 分阶段接口范围

### 7.1 第一阶段必须实现

- 扩展基础 CRUD 字段
- 详情聚合接口
- 项目关联接口
- 依赖关系接口
- 状态概览接口
- 异常/通知接口
- 统计概览接口

### 7.2 第二阶段再实现

- 责任矩阵独立关系接口
- 外部监控事件推送接口
- 多渠道通知回执接口
- 拓扑图接口

## 8. 接口设计结论

应用管理接口设计遵循以下原则：

1. 基础 CRUD 兼容保留。
2. 主档、关系、状态、异常、统计分接口治理。
3. 权限和错误码在一期就收口。

这样可以确保当前单体项目渐进演进，而不是一次性推翻现有页面和接口。
