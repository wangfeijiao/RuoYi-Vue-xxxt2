[artifact:TaskBreakdown]
status: READY
owner: tech-lead-architect
scope:
- 将一期 MVP 拆解为 SQL、后端、前端、测试四类可执行任务
- 明确依赖顺序和模块边界
inputs:
- [artifact:PRD]
- [artifact:Prototype]
- [artifact:SystemArch]
- [artifact:DBDesign]
- [artifact:APIDoc]
deliverables:
- 可直接执行的任务批次
- 前后端并行边界
risks:
- 页面较多，若不复用列表/对话框模式，前端工作量会上升
handoff_to:
- engineering-manager
- senior-backend-engineer
- senior-frontend-engineer
- senior-qa-engineer
exit_criteria:
- 任务已明确到文件类型和交付顺序

## 批次 1：基础数据层

- 新增业务 SQL：表结构、菜单、按钮权限、管理员授权
- 新增业务实体、Mapper、Mapper XML
- 新增通用工单和统计聚合查询

## 批次 2：后端服务层

- 实现项目、模板、资源、应用、数据资产、网络资源 CRUD
- 实现工单查询、审批、执行、删除
- 实现数据资产版本快照写入
- 实现 Dashboard 聚合接口

## 批次 3：前端基础接入

- 新增业务 API 文件
- 新增信息化管理菜单页面
- 新增 Dashboard 看板
- 新增五类业务页面和工单分页区块

## 批次 4：交互完善

- 按业务域补齐弹窗表单、审批按钮、执行按钮
- 根据状态控制按钮可见性
- 完成页面分页、重置、详情回显

## 批次 5：验证

- 后端单元测试或服务测试
- Maven 编译/测试
- 前端生产构建验证
- 形成评审、安全、测试报告
