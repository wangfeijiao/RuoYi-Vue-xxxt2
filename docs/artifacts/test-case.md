[artifact:TestCase]
status: READY
owner: senior-qa-engineer
scope:
- 覆盖信息化管理模块五大业务域的核心流程、边界流程与回归点
inputs:
- [artifact:PRD]
- [artifact:Prototype]
- [artifact:APIDoc]
- [artifact:BackendCode]
- [artifact:FrontendCode]
- [artifact:ReviewReport]
- [artifact:SecurityReport]
deliverables:
- 项目管理：项目 CRUD、模板 CRUD、验收工单发起/审批/执行
- IT 资源管理：资源 CRUD、资源工单发起/审批/执行/删除
- 应用管理：应用 CRUD、项目关联与状态维护
- 数据资源管理：资产 CRUD、版本快照生成、工单审批/执行
- 网络安全管理：网络资源 CRUD、工单审批/执行、资源状态回写
- 看板总览：项目、资源、应用、数据、网络统计与最近工单展示
risks:
- 需要上线前在真实数据库执行初始化 SQL 并做一次菜单/权限冒烟
handoff_to:
- senior-qa-engineer
exit_criteria:
- 关键主流程、核心状态流转与构建验证均有对应用例
