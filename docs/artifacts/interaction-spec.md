[artifact:InteractionSpec]
status: READY
owner: senior-ui-designer
scope:
- 规定五类业务页在查询、新增、编辑、审批、执行、删除时的状态切换与反馈
- 明确空态、错态、权限态和审批态
inputs:
- [artifact:Prototype]
- [artifact:DesignSpec]
- [artifact:APIDoc]
deliverables:
- 页面状态与动作规则
- 审批/执行按钮可见性规则
- 异常提示规则
risks:
- 外部系统真实告警接入后，异常提示内容可能补充来源字段
handoff_to:
- engineering-manager
- senior-frontend-engineer
exit_criteria:
- 页面交互逻辑具备可执行性

## 1. 查询与空态

- 首次进入页面自动查询第一页
- 无数据时显示模块说明和“新增”入口
- 查询后无结果时显示“暂无匹配数据”

## 2. 表单提交

- 新增/编辑弹窗提交前执行必填校验
- 成功后提示“新增成功”或“修改成功”
- 失败时保留表单内容

## 3. 工单审批

仅当 `orderStatus = PENDING` 时显示“审批”按钮。

审批弹窗包含：

- 审批结果：通过/驳回
- 审批意见

审批通过后：

- 若需要执行，状态改为 `PENDING_EXECUTION`
- 否则直接改为 `COMPLETED`

驳回后：

- 状态改为 `REJECTED`

## 4. 工单执行

仅当 `orderStatus = PENDING_EXECUTION` 时显示“执行”按钮。

执行弹窗包含：

- 执行人
- 执行结果
- 目标状态

执行完成后：

- 工单状态变为 `COMPLETED`
- 主台账状态同步更新

## 5. 删除确认

- 删除必须弹出确认框
- 批量删除时提示编号列表或数量

## 6. 错态与权限态

- 接口失败提示后端错误消息
- 无权限按钮直接隐藏
- 详情对象不存在时提示“数据已不存在，请刷新后重试”
