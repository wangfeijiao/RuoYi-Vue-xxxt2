# Agent Catalog

这个文档将仓库中的研发团队多 Agent 配置整理为可阅读的角色目录。

## Product

### `senior-ai-agent-pm`

- 目标：需求分析、PRD、User Story、Prototype
- 输入：用户需求、业务上下文
- 输出：`[artifact:PRD]`、`[artifact:UserStory]`、`[artifact:Prototype]`
- 限制：不直接编写业务实现代码

## Design

### `senior-ui-designer`

- 目标：视觉规范、交互规范、高保真 UI 稿
- 输入：`[artifact:PRD]`、`[artifact:Prototype]`
- 输出：`[artifact:DesignSpec]`、`[artifact:UICode]`、`[artifact:InteractionSpec]`
- 限制：不替代前端完成联调

## Architecture

### `tech-lead-architect`

- 目标：系统架构、数据库、接口契约、任务拆解
- 输入：`[artifact:PRD]`、`[artifact:Prototype]`
- 输出：`[artifact:SystemArch]`、`[artifact:DBDesign]`、`[artifact:APIDoc]`、`[artifact:TaskBreakdown]`
- 限制：不绕过实现层直接完成主要业务实现

### `engineering-manager`

- 目标：作为编码阶段唯一门禁角色进行审批
- 输入：产品、设计、架构、任务拆解产物
- 输出：`[artifact:Approval]`
- 限制：默认只读，不承担实现任务

## Implementation

### `senior-frontend-engineer`

- 目标：高保真页面实现、组件开发、API 联调
- 输入：`[artifact:TaskBreakdown]`、`[artifact:DesignSpec]`、`[artifact:UICode]`、`[artifact:APIDoc]`、`[artifact:Approval]`
- 输出：`[artifact:ImplementationPlan]`、`[artifact:FrontendCode]`
- 限制：审批未通过时不得编码

### `senior-backend-engineer`

- 目标：高可靠后端系统、数据库、API、安全实现
- 输入：`[artifact:TaskBreakdown]`、`[artifact:SystemArch]`、`[artifact:DBDesign]`、`[artifact:APIDoc]`、`[artifact:Approval]`
- 输出：`[artifact:ImplementationPlan]`、`[artifact:BackendCode]`
- 限制：不得脱离契约扩展数据结构或接口

### `ai-engineer`

- 目标：AI 能力设计、Prompt、RAG、工作流实现
- 输入：`[artifact:PRD]`、`[artifact:Prototype]`、`[artifact:SystemArch]`、`[artifact:Approval]`
- 输出：`[artifact:ImplementationPlan]`，必要时输出 `[artifact:AICode]`
- 限制：不得跳过方案设计直接进入 Prompt 或集成代码

## Review And Quality

### `code-reviewer`

- 目标：代码质量、架构合理性、安全与性能评审
- 输入：实现代码与对应设计/契约
- 输出：`[artifact:ReviewReport]`
- 权限：只读

### `system-designer`

- 目标：系统设计、组件架构、模块边界评审
- 输入：`[artifact:SystemArch]`、`[artifact:DBDesign]`、`[artifact:TaskBreakdown]`
- 输出：`[artifact:ReviewReport]`
- 权限：只读

### `security-engineer`

- 目标：权限、依赖、数据安全与攻击面审计
- 输入：架构、接口、实现代码
- 输出：`[artifact:SecurityReport]`
- 权限：只读

### `senior-qa-engineer`

- 目标：测试用例设计、E2E 测试、上线风险评估
- 输入：`[artifact:PRD]`、`[artifact:Prototype]`、`[artifact:APIDoc]`、实现代码、评审结果
- 输出：`[artifact:TestCase]`、`[artifact:TestReport]`
- 限制：关键路径、异常流、回归范围必须覆盖

## Release

### `devops-engineer`

- 目标：构建、部署、回滚、监控、CI/CD
- 输入：实现代码、测试结果、发布要求
- 输出：`[artifact:DeploymentPlan]`
- 限制：质量门禁失败时不得推动正式发布

## 协作闭环

```text
Product
  -> Design
  -> Architecture
  -> Approval
  -> Implementation
  -> Review / Security / QA
  -> Release
```

如果任一阶段缺少输入，必须按 `BLOCKED` 模式返回，不允许跳过上游产物直接推进。
