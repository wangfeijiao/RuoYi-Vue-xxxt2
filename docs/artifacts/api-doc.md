[artifact:APIDoc]
status: READY
owner: tech-lead-architect
scope:
- 定义一期 MVP 所需 REST 接口、权限点、响应风格与主要状态流转接口
- 保持与现有 RuoYi `AjaxResult`、`TableDataInfo` 风格一致
inputs:
- [artifact:PRD]
- [artifact:Prototype]
- [artifact:SystemArch]
- [artifact:DBDesign]
deliverables:
- 六组控制器接口清单
- 通用工单审批/执行 API 约定
- Dashboard 聚合返回约定
risks:
- 复杂动态表单参数以 JSON 文本传输，一期前后端约定需要保持一致
handoff_to:
- engineering-manager
- senior-backend-engineer
- senior-frontend-engineer
exit_criteria:
- 前后端可据此直接开发与联调

## 1. 通用约定

- Base URL 前缀：`/information`
- 列表接口：`GET /list`
- 详情接口：`GET /{id}`
- 新增接口：`POST`
- 修改接口：`PUT`
- 删除接口：`DELETE /{ids}`
- 列表返回：`TableDataInfo`
- 详情/动作返回：`AjaxResult`

## 2. Dashboard

### `GET /information/dashboard/overview`

权限：`information:dashboard:list`

返回字段：

- `projectTotal`
- `projectPendingAcceptance`
- `resourceTotal`
- `resourceInUse`
- `applicationTotal`
- `applicationAlertCount`
- `dataAssetTotal`
- `pendingDataOrders`
- `networkTotal`
- `pendingExecutionOrders`
- `todoOrders`
- `recentOrders`

## 3. 项目管理

### 项目

- `GET /information/project/list`
- `GET /information/project/{projectId}`
- `POST /information/project`
- `PUT /information/project`
- `DELETE /information/project/{projectIds}`

权限：

- `information:project:list`
- `information:project:query`
- `information:project:add`
- `information:project:edit`
- `information:project:remove`

### 模板

- `GET /information/project/template/list`
- `GET /information/project/template/{templateId}`
- `POST /information/project/template`
- `PUT /information/project/template`
- `DELETE /information/project/template/{templateIds}`

权限：

- `information:projectTemplate:list`
- `information:projectTemplate:query`
- `information:projectTemplate:add`
- `information:projectTemplate:edit`
- `information:projectTemplate:remove`

### 项目验收工单

- `GET /information/project/order/list`
  查询 `domainType=PROJECT`
- `POST /information/project/order`
  新建验收申请
- `PUT /information/project/order`
  编辑草稿或补充信息
- `POST /information/project/order/{workOrderId}/approve`
  审批通过/驳回
- `POST /information/project/order/{workOrderId}/execute`
  执行结项动作

权限：

- `information:projectOrder:list`
- `information:projectOrder:add`
- `information:projectOrder:edit`
- `information:projectOrder:approve`
- `information:projectOrder:execute`

## 4. IT 资源管理

### 资源台账

- `GET /information/resource/list`
- `GET /information/resource/{resourceId}`
- `POST /information/resource`
- `PUT /information/resource`
- `DELETE /information/resource/{resourceIds}`

权限：

- `information:resource:list`
- `information:resource:query`
- `information:resource:add`
- `information:resource:edit`
- `information:resource:remove`

### 资源工单

- `GET /information/resource/order/list`
- `GET /information/resource/order/{workOrderId}`
- `POST /information/resource/order`
- `PUT /information/resource/order`
- `POST /information/resource/order/{workOrderId}/approve`
- `POST /information/resource/order/{workOrderId}/execute`
- `DELETE /information/resource/order/{workOrderIds}`

权限：

- `information:resourceOrder:list`
- `information:resourceOrder:query`
- `information:resourceOrder:add`
- `information:resourceOrder:edit`
- `information:resourceOrder:approve`
- `information:resourceOrder:execute`
- `information:resourceOrder:remove`

## 5. 应用管理

- `GET /information/application/list`
- `GET /information/application/{applicationId}`
- `POST /information/application`
- `PUT /information/application`
- `DELETE /information/application/{applicationIds}`

权限：

- `information:application:list`
- `information:application:query`
- `information:application:add`
- `information:application:edit`
- `information:application:remove`

## 6. 数据资源管理

### 数据资产

- `GET /information/dataAsset/list`
- `GET /information/dataAsset/{assetId}`
- `POST /information/dataAsset`
- `PUT /information/dataAsset`
- `DELETE /information/dataAsset/{assetIds}`
- `GET /information/dataAsset/{assetId}/versions`

权限：

- `information:dataAsset:list`
- `information:dataAsset:query`
- `information:dataAsset:add`
- `information:dataAsset:edit`
- `information:dataAsset:remove`

### 数据工单

- `GET /information/dataAsset/order/list`
- `GET /information/dataAsset/order/{workOrderId}`
- `POST /information/dataAsset/order`
- `PUT /information/dataAsset/order`
- `POST /information/dataAsset/order/{workOrderId}/approve`
- `POST /information/dataAsset/order/{workOrderId}/execute`
- `DELETE /information/dataAsset/order/{workOrderIds}`

权限：

- `information:dataOrder:list`
- `information:dataOrder:query`
- `information:dataOrder:add`
- `information:dataOrder:edit`
- `information:dataOrder:approve`
- `information:dataOrder:execute`
- `information:dataOrder:remove`

## 7. 网络安全管理

### 网络资源

- `GET /information/network/list`
- `GET /information/network/{networkId}`
- `POST /information/network`
- `PUT /information/network`
- `DELETE /information/network/{networkIds}`

权限：

- `information:network:list`
- `information:network:query`
- `information:network:add`
- `information:network:edit`
- `information:network:remove`

### 网络工单

- `GET /information/network/order/list`
- `GET /information/network/order/{workOrderId}`
- `POST /information/network/order`
- `PUT /information/network/order`
- `POST /information/network/order/{workOrderId}/approve`
- `POST /information/network/order/{workOrderId}/execute`
- `DELETE /information/network/order/{workOrderIds}`

权限：

- `information:networkOrder:list`
- `information:networkOrder:query`
- `information:networkOrder:add`
- `information:networkOrder:edit`
- `information:networkOrder:approve`
- `information:networkOrder:execute`
- `information:networkOrder:remove`

## 8. 审批/执行请求体约定

### 审批

请求体：

- `approved`：`true/false`
- `approvalComment`

### 执行

请求体：

- `executionResult`
- `executorName`
- `targetStatus`

## 9. 错误处理

统一使用 `AjaxResult.error(...)` 返回，重点错误场景：

- 目标对象不存在
- 工单状态不允许审批
- 工单状态不允许执行
- 编号重复
- 资源/项目关联缺失
