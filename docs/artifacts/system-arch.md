[artifact:SystemArch]
status: READY
owner: tech-lead-architect
scope:
- 为 RuoYi-Vue 单体项目设计 IT 资源管理、应用管理、信息化项目管理、数据资源管理、网络安全管理的模块边界与集成方式
- 明确一阶段 MVP 的系统分层、数据流、审批流与外部依赖处理策略
inputs:
- [artifact:PRD]
- [artifact:UserStory]
- [artifact:Prototype]
- 现有 RuoYi-Vue 项目结构与 SQL 基线
deliverables:
- 基于现有 `ruoyi-admin`、`ruoyi-system`、`ruoyi-ui` 的单体扩展架构
- 项目、资源、应用、数据资产、网络资源与通用工单的领域划分
- 前后端、SQL、权限菜单、统计看板的一期实现边界
risks:
- 短信、统一认证中心、外部监控平台、CMDB、网络设备自动化配置暂无现成集成，MVP 以预留字段和人工闭环替代
- 项目过程资料上传与目录模板先采用 JSON/文本结构管理，不在一期实现完整文档中心
handoff_to:
- engineering-manager
- senior-backend-engineer
- senior-frontend-engineer
exit_criteria:
- 系统边界、模块职责、接口风格、数据流和实现落点明确
- 后续前后端角色具备按文档开工的条件

## Entry Check

- 当前任务目标明确：需要为五个业务域在现有 RuoYi-Vue 项目中形成可实施架构方案
- 必需 artifact 齐备：`[artifact:PRD]`、`[artifact:Prototype]`、`[artifact:UserStory]` 已存在
- 本角色权限匹配：`tech-lead-architect` 负责输出系统、数据库、接口和任务拆解
- 上游审批前置不存在：架构阶段是审批前置输入，本阶段可继续推进

## 1. 总体架构

系统沿用现有 RuoYi-Vue 单体分层，新增“信息化管理”业务域，采用“后台模块化 + 前端多页面 + SQL 初始化”的实现方式：

- `ruoyi-admin`
  负责 REST Controller 暴露、权限注解、分页查询、状态流转入口
- `ruoyi-system`
  负责领域对象、Mapper、Service、统计聚合逻辑、审批状态变更
- `ruoyi-ui`
  负责业务页面、对话框、统计卡片、台账/工单分页、审批动作
- `sql`
  负责新增业务表、菜单、按钮权限、管理员授权初始化

## 2. 业务模块划分

### 2.1 项目中心

项目是五个业务域的主关联中心，负责承接：

- 项目基本信息与责任人矩阵
- 模板管理与目录模板配置
- 项目验收申请与审批闭环
- 项目与 IT 资源、应用、数据资产、网络资源的关联

### 2.2 IT 资源中心

负责：

- IT 资源台账
- 资源申请、变更、交付、回收
- 资源状态、配置、责任人和项目归属
- 资源利用情况统计

### 2.3 应用中心

负责：

- 应用台账
- 应用与项目、资源、管理团队负责人关系
- 应用分级分类与运行状态总览

### 2.4 数据资产中心

负责：

- 数据资产目录
- 数据元数据与治理属性
- 数据资产版本快照
- 数据使用/变更申请审批

### 2.5 网络安全中心

负责：

- 网络资源台账
- 网络需求申请、审核、执行、回收
- 安全策略文本、IP/VLAN/带宽分配记录
- 网络资源监控状态与风险提示

### 2.6 通用工单中心

为降低一期复杂度，资源申请/变更/回收、数据申请/变更、网络申请/变更/回收、项目验收统一落入通用工单模型，通过 `domainType + requestType` 区分业务语义。这样可以：

- 复用审批状态机和分页查询能力
- 统一“待审核/已通过/已驳回/待执行/已完成”状态
- 降低前后端重复代码

## 3. 模块落点

### 3.1 后端包结构

- `com.ruoyi.web.controller.information`
  放置五大业务域控制器和统计控制器
- `com.ruoyi.system.domain.information`
  放置业务实体
- `com.ruoyi.system.mapper.information`
  放置业务 Mapper 接口
- `com.ruoyi.system.service.information`
  放置业务 Service 接口
- `com.ruoyi.system.service.information.impl`
  放置业务 Service 实现
- `ruoyi-system/src/main/resources/mapper/information`
  放置 MyBatis XML

### 3.2 前端目录结构

- `src/api/information`
  放置业务 API 调用文件
- `src/views/information/dashboard`
- `src/views/information/project`
- `src/views/information/resource`
- `src/views/information/application`
- `src/views/information/dataAsset`
- `src/views/information/network`

## 4. 核心数据流

### 4.1 项目创建流

1. 前端提交项目信息
2. 后端创建项目记录并保存目录模板 JSON
3. 项目列表和项目详情页显示项目状态与责任人矩阵

### 4.2 通用工单流

1. 业务页面发起申请
2. 后端创建通用工单并记录当前快照/申请参数 JSON
3. 审批人执行“通过/驳回”
4. 通过后可进入“待执行”，执行人补录执行结果
5. 完成后反写目标台账对象状态

### 4.3 数据版本流

1. 数据资产维护时写入最新属性
2. 同时在版本表插入快照记录
3. 前端可查看历史版本列表

### 4.4 统计总览流

1. Dashboard Service 聚合项目、资源、应用、数据、网络五类表
2. 返回数量分布和待办统计
3. 首页以卡片和列表方式呈现

## 5. 一期 MVP 约束

一期严格实现以下能力：

- 五大业务域的台账 CRUD
- 项目模板与项目验收申请
- 通用工单式申请/审批/执行闭环
- 数据资产版本留痕
- 首页统计总览
- 菜单、按钮权限与管理员初始化授权

一期不实现以下复杂集成：

- 短信真实发送
- 监控平台 API 拉取
- 文档文件实际存储与在线预览
- 统一认证中心同步
- 网络设备自动化下发

## 6. 权限模型

权限沿用 RuoYi 菜单/按钮权限模型：

- 目录：`information:*`
- 菜单：`dashboard`、`project`、`resource`、`application`、`dataAsset`、`network`
- 按钮维度：`list/query/add/edit/remove/export/approve/execute`

角色由现有系统角色管理配置，不在一期新增独立角色表。

## 7. 技术决策

- 不引入工作流引擎，审批流使用字段驱动状态机
- 不新增独立模块工程，保持在 `ruoyi-system` 中实现，减少 Spring 扫描和打包调整
- 不新增前端状态管理层，页面内完成查询与对话框交互
- 使用文本/JSON 字段承接动态表单参数、目录模板、执行结果，换取实现速度

## 8. 下游实施约束

- 后端必须优先完成 SQL、实体、Mapper、Service、Controller 与统计接口
- 前端必须按页面域拆分，不允许把五类业务硬塞进单一超大页面
- 测试必须至少覆盖：主流程 CRUD、审批状态流转、统计接口可用性
