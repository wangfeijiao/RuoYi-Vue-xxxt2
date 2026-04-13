[artifact:APIDoc]
status: READY
owner: tech-lead-architect
scope:
- 细化 IT 资源管理模块的接口清单、接口职责、状态约束、请求响应口径
- 基于当前仓库已存在控制器和前端 API 实现给出复用与优化建议
inputs:
- [artifact:PRD] docs/artifacts/it-resource-prd.md
- [artifact:Prototype] docs/artifacts/it-resource-prototype.md
- [artifact:SystemArch] docs/artifacts/it-resource-system-arch.md
- 当前实现：InfoResourceController、resource.js
handoff_to:
- engineering-manager
- senior-backend-engineer
- senior-frontend-engineer
deliverables:
- IT 资源管理模块接口清单
- 现状复用说明与接口优化建议
- 接口与状态机、权限、前端页面的映射说明
risks:
- 当前接口语义偏通用，资源域业务约束仍主要依赖调用方约定
- 当前工单新增/修改接口输入过于宽松，存在无效载荷和状态越权风险
exit_criteria:
- 后端接口清单、入参出参约束、权限码和优化方向明确

## 入口检查

- 当前任务目标是否明确：是，仅输出 IT 资源管理模块接口文档。
- 必需 artifact 是否齐备：是。
- 本角色是否有权限处理该任务：是。
- 是否存在上游审批或评审前置条件：否。

## 一、现状复用

### 1. 已存在接口

资源台账：

- `GET /information/resource/list`
- `GET /information/resource/{resourceId}`
- `POST /information/resource`
- `PUT /information/resource`
- `DELETE /information/resource/{resourceIds}`

资源工单：

- `GET /information/resource/order/list`
- `GET /information/resource/order/{workOrderId}`
- `POST /information/resource/order`
- `PUT /information/resource/order`
- `POST /information/resource/order/{workOrderId}/approve`
- `POST /information/resource/order/{workOrderId}/execute`
- `DELETE /information/resource/order/{workOrderIds}`

### 2. 当前可直接复用的点

- URL 路由已合理聚合在资源域下
- 资源台账与工单接口已分组
- 审批、执行动作已单独成接口，方向正确

## 二、建议优化

### 1. 保留现有 URL，不做大改

为了兼容当前前端，建议保留现有路径结构，仅优化：

- 入参校验
- 状态流转约束
- 返回错误信息
- 查询条件完整度

### 2. 资源域接口语义收口

- `POST /order` 只允许创建资源域工单
- `PUT /order` 不建议开放对流程中工单的任意编辑
- 审批与执行必须是唯一入口
- 资源核心字段变更应通过工单执行接口，不应通过资源编辑接口直接落账

## 三、接口清单

### 1. 资源台账接口

#### 1.1 查询资源列表

- 方法：`GET`
- 路径：`/information/resource/list`
- 权限：`information:resource:list`

请求参数建议：

- `resourceCode`
- `resourceName`
- `resourceType`
- `resourceStatus`
- `monitorStatus`
- `projectId`
- `ownerDeptId`
- `ownerName`
- `maintainerName`
- `ipAddress`
- `beginDeliveryTime`
- `endDeliveryTime`
- `pageNum`
- `pageSize`

当前现状：

- 已支持 `resourceCode`、`resourceName`、`resourceType`、`resourceStatus`、`projectId`、`ownerName`

建议优化：

- 补 `monitorStatus`、`maintainerName`、`ipAddress`、时间范围筛选

#### 1.2 查询资源详情

- 方法：`GET`
- 路径：`/information/resource/{resourceId}`
- 权限：`information:resource:query`

返回字段：

- 主档全部字段
- `projectName`

建议优化：

- 增加最近一次工单摘要

#### 1.3 新增资源

- 方法：`POST`
- 路径：`/information/resource`
- 权限：`information:resource:add`

请求体核心字段：

- `resourceCode`
- `resourceName`
- `resourceType`
- `resourceStatus`
- `monitorStatus`
- `projectId`
- `ownerDeptId`
- `ownerName`
- `maintainerName`
- `cpuCores`
- `memoryGb`
- `storageGb`
- `osName`
- `ipAddress`
- `softwareName`
- `softwareVersion`
- `licenseCount`
- `deliveryTime`
- `relationSummary`
- `performanceSummary`
- `remark`

建议约束：

- `resourceCode` 必填且唯一
- `resourceName`、`resourceType` 必填
- `resourceStatus` 默认 `IDLE`
- `monitorStatus` 默认 `UNKNOWN`

#### 1.4 更新资源

- 方法：`PUT`
- 路径：`/information/resource`
- 权限：`information:resource:edit`

建议约束：

- 允许修改非核心字段
- 核心字段变更应拦截并提示通过变更工单处理
- 禁止把 `RECYCLED` 资源直接编辑回 `IN_USE`

#### 1.5 删除资源

- 方法：`DELETE`
- 路径：`/information/resource/{resourceIds}`
- 权限：`information:resource:remove`

建议约束：

- 已被工单引用的资源禁止物理删除
- 优先改为逻辑删除或停用

### 2. 资源工单接口

#### 2.1 查询资源工单列表

- 方法：`GET`
- 路径：`/information/resource/order/list`
- 权限：`information:resourceOrder:list`

请求参数建议：

- `workOrderNo`
- `requestType`
- `orderStatus`
- `projectId`
- `subjectId`
- `subjectName`
- `applicantName`
- `approverName`
- `executorName`
- `beginSubmittedTime`
- `endSubmittedTime`
- `beginExpectFinishTime`
- `endExpectFinishTime`
- `pageNum`
- `pageSize`

当前现状：

- 已支持 `requestType`、`orderStatus`、`projectId`、`subjectName`、`applicantName`

建议优化：

- 补充工单号、审批人、执行人、时间范围筛选

#### 2.2 查询资源工单详情

- 方法：`GET`
- 路径：`/information/resource/order/{workOrderId}`
- 权限：`information:resourceOrder:query`

返回字段：

- 工单全部字段
- `projectName`

建议优化：

- 增加资源当前状态
- 增加快照与申请载荷的结构化返回

#### 2.3 新增资源工单

- 方法：`POST`
- 路径：`/information/resource/order`
- 权限：`information:resourceOrder:add`

请求体建议字段：

- `requestType`
- `projectId`
- `subjectId`
- `subjectName`
- `subjectType`
- `applicantDeptId`
- `applicantName`
- `requestTitle`
- `requestReason`
- `currentSnapshotJson`
- `requestPayloadJson`
- `expectFinishTime`
- `remark`

业务规则：

- 后端强制写入 `domainType=RESOURCE`
- 自动生成 `workOrderNo`
- 默认状态 `PENDING`

类型约束：

- `APPLY`：`projectId` 必填，`subjectId` 可空
- `CHANGE`：`subjectId` 必填
- `RECYCLE`：`subjectId` 必填

#### 2.4 更新资源工单

- 方法：`PUT`
- 路径：`/information/resource/order`
- 权限：`information:resourceOrder:edit`

建议限制：

- 仅 `DRAFT` 或 `PENDING` 前允许编辑基础内容
- 已审批或已执行工单禁止编辑

当前现状：

- 接口已开放，但缺少状态限制

#### 2.5 审批资源工单

- 方法：`POST`
- 路径：`/information/resource/order/{workOrderId}/approve`
- 权限：`information:resourceOrder:approve`

请求体：

```json
{
  "approved": true,
  "approvalComment": "同意交付"
}
```

规则：

- 仅 `PENDING` 可审批
- `approved=false` 时必须填写意见
- 通过后工单进入 `PENDING_EXECUTION`
- 驳回后进入 `REJECTED`

当前现状：

- 已实现 `PENDING -> PENDING_EXECUTION/REJECTED`

建议优化：

- 审批通过时同步资源中间状态

#### 2.6 执行资源工单

- 方法：`POST`
- 路径：`/information/resource/order/{workOrderId}/execute`
- 权限：`information:resourceOrder:execute`

请求体：

```json
{
  "executorName": "zhangsan",
  "targetStatus": "IN_USE",
  "executionResult": "资源已交付"
}
```

规则：

- 仅 `PENDING_EXECUTION` 可执行
- `executionResult` 必填
- 执行完成后工单进入 `COMPLETED`
- 同步更新资源状态

当前现状：

- 已实现执行入口和状态回写

建议优化：

- `APPLY` 可支持“新建资源并绑定”或“绑定空闲资源”
- `CHANGE` 执行时强制要求变更结果载荷
- `RECYCLE` 执行时强制要求回收说明

#### 2.7 删除资源工单

- 方法：`DELETE`
- 路径：`/information/resource/order/{workOrderIds}`
- 权限：`information:resourceOrder:remove`

建议限制：

- 仅 `DRAFT` 可删除
- `PENDING` 后只允许作废，不允许删除

## 四、错误码与错误语义建议

当前仓库主要返回 `AjaxResult`。资源域建议统一错误语义：

- `资源编码已存在`
- `IP 地址与运行中资源冲突`
- `当前状态不支持该操作`
- `工单不存在或已变化`
- `仅待审批工单允许审批`
- `仅待执行工单允许执行`
- `已回收资源不允许发起变更`
- `目标资源不存在`

即使暂不引入独立错误码枚举，也要保持错误文案可判定。

## 五、前后端接口映射

### 1. 台账页

- 列表查询 -> `GET /list`
- 详情抽屉 -> `GET /{resourceId}`
- 新增 -> `POST /`
- 编辑 -> `PUT /`
- 删除 -> `DELETE /{resourceIds}`

### 2. 工单页

- 列表查询 -> `GET /order/list`
- 工单详情 -> `GET /order/{workOrderId}`
- 发起申请/变更/回收 -> `POST /order`
- 审批 -> `POST /order/{workOrderId}/approve`
- 执行 -> `POST /order/{workOrderId}/execute`

## 六、接口设计结论

接口层无需大规模推倒重来。推荐路径：

- 保留现有资源接口路径
- 强化查询维度与校验规则
- 收口工单类型语义
- 在审批与执行接口中补齐状态和载荷约束

这样既能兼容现有前端，又能把资源域接口变成可持续演进的稳定契约。
