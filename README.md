# Open Multi-Agent R&D Team

将当前项目内的研发团队多 Agent 配置，整理为一个可直接开源到 GitHub 的仓库骨架。

这个项目的核心不是“一个 Agent”，而是一套可执行的研发协作协议：

- 用统一的 artifact 标签约束交付物
- 用明确的 gate 控制编码前置条件
- 用固定角色边界避免职责串位
- 用评审、安全、测试、发布链路形成完整闭环

## 项目内容

- [`AGENTS.md`](./AGENTS.md): 多 Agent 执行总规约
- [`.codex/agents`](./.codex/agents): 研发团队角色配置
- [`docs/agent-catalog.md`](./docs/agent-catalog.md): 角色矩阵与职责说明
- [`docs/artifact-templates.md`](./docs/artifact-templates.md): 标准 artifact 模板与输出规范
- [`index.html`](./index.html): 项目介绍页，可直接作为 GitHub Pages 首页

## 角色矩阵

当前仓库已内置以下角色：

- `senior-ai-agent-pm`
- `senior-ui-designer`
- `tech-lead-architect`
- `engineering-manager`
- `senior-frontend-engineer`
- `senior-backend-engineer`
- `ai-engineer`
- `code-reviewer`
- `system-designer`
- `security-engineer`
- `senior-qa-engineer`
- `devops-engineer`

这些角色覆盖产品、设计、架构、审批、实现、评审、安全、测试、发布的完整研发流程。

## 适合的场景

- 想把 AI Agent 从“单助手”升级为“团队协作系统”
- 想在企业研发流程中引入明确门禁和标准产物
- 想为 Codex/Agent 平台沉淀一套可复用的角色配置
- 想把内部 Agent 配置整理成可分享、可复刻、可演示的开源仓库

## 快速开始

1. 阅读 [`AGENTS.md`](./AGENTS.md)，理解统一执行协议。
2. 根据你的场景调整 [`.codex/agents`](./.codex/agents) 中的角色描述与约束。
3. 按 [`docs/artifact-templates.md`](./docs/artifact-templates.md) 产出标准文档。
4. 将 `index.html` 发布到 GitHub Pages，作为项目说明页。

## 推荐仓库结构

```text
.
├── .codex/
│   ├── agents/
│   └── config.toml
├── docs/
│   ├── agent-catalog.md
│   └── artifact-templates.md
├── AGENTS.md
├── CONTRIBUTING.md
├── LICENSE
├── README.md
└── index.html
```

## 设计原则

- Artifact First
- Gate Before Build
- One Role, One Decision
- Read-Only Reviewers
- Traceable Delivery

## 开源建议

- 使用 GitHub Issues 管理需求、缺陷与角色扩展
- 使用 Pull Request 审核 Agent 配置变更
- 在 PR 模板中要求引用对应 artifact
- 将 `index.html` 作为 GitHub Pages 项目首页

## License

本仓库默认使用 [MIT License](./LICENSE)。
