# Artifact Templates

以下模板从 [`AGENTS.md`](../AGENTS.md) 中提炼而来，适合在 GitHub 项目中直接复用。

## 通用输出头

```md
[artifact:<Tag>]
status: READY | BLOCKED | PASS | FAIL
owner: <agent-name>
scope:
- <本次处理范围>
inputs:
- <使用到的上游 artifact 或上下文>
handoff_to:
- <下一个责任角色>
```

## BLOCKED 模板

```md
[artifact:<Tag>]
status: BLOCKED
owner: <agent-name>
scope:
- <希望完成但尚未能执行的工作>
missing_inputs:
- <缺失的 artifact 或上下文>
blocking_reasons:
- <为什么不能继续>
handoff_to:
- <应补齐该输入的角色>
next_action:
- <上游补齐后如何重新进入流程>
```

## READY 模板

```md
[artifact:<Tag>]
status: READY
owner: <agent-name>
scope:
- <本次交付范围>
inputs:
- <使用到的输入>
deliverables:
- <产物核心内容摘要>
risks:
- <剩余风险；没有则写 None>
handoff_to:
- <下游角色>
exit_criteria:
- <本阶段完成标准>
```

## Approval 模板

```md
[artifact:Approval]
result: APPROVED | REJECTED
owner: engineering-manager
scope:
- <审批范围>
required_inputs:
- <审查过的输入>
checklist:
- [x] PRD/Prototype 已齐备
- [x] 设计或架构产物已齐备
- [x] TaskBreakdown 已齐备
- [x] 实施范围清晰
- [x] 风险可控
blocking_issues:
- <阻断问题；没有则写 None>
approved_scope:
- <允许进入实现的范围；拒绝时写 None>
handoff_to:
- <实现角色或返回上游角色>
```

## Review / Security / Test 模板

```md
verdict: PASS | FAIL
findings:
- [severity:blocker|high|medium|low] <问题>
must_fix:
- <发布前必须修复的问题；没有则写 None>
can_follow_up:
- <允许后续处理的问题；没有则写 None>
```

## ImplementationPlan 模板

```md
[artifact:ImplementationPlan]
status: READY
owner: <agent-name>
scope:
- <本次实现范围>
inputs:
- <使用到的输入>
handoff_to:
- <下游角色>
goal:
- <要实现什么>
changed_areas:
- <模块/文件/接口范围>
steps:
- <实现步骤>
risks:
- <技术风险>
validation:
- <测试与验证方式>
```

## FrontendCode / BackendCode / AICode 模板

```md
changed_files:
- <文件或模块>
contracts_affected:
- <接口/表结构/Prompt 契约；没有则写 None>
tests_run:
- <已执行验证；未执行则明确写 Not Run>
known_issues:
- <已知残余问题；没有则写 None>
```

## DeploymentPlan 模板

```md
precheck:
- <发布前检查项>
steps:
- <部署步骤>
rollback:
- <回滚步骤>
monitoring:
- <监控与告警项>
owner:
- <发布责任方>
```
