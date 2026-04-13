[artifact:BackendCode]
status: READY
owner: senior-backend-engineer
scope:
- 交付 IT 资源管理模块后端规则增强
inputs:
- [artifact:ImplementationPlan] docs/artifacts/it-resource-backend-implementation-plan.md
- [artifact:Approval] docs/artifacts/it-resource-approval.md
deliverables:
- 资源编码唯一性校验
- 活跃资源 IP 冲突校验
- 资源删除工单引用保护
- 资源工单类型与必填校验
- 资源工单审批中间状态回写
- 资源工单执行结果必填校验与快照填充
risks:
- 资源逻辑删除和更细粒度字段级编辑控制仍可作为下一轮迭代项
handoff_to:
- code-reviewer
- senior-qa-engineer
exit_criteria:
- 后端规则增强代码与测试通过，可支撑资源模块进一步联调
changed_files:
- `ruoyi-system/src/main/java/com/ruoyi/system/service/information/impl/InfoResourceServiceImpl.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/information/impl/InfoWorkOrderServiceImpl.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/mapper/information/InfoResourceMapper.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/mapper/information/InfoWorkOrderMapper.java`
- `ruoyi-system/src/main/resources/mapper/information/InfoResourceMapper.xml`
- `ruoyi-system/src/main/resources/mapper/information/InfoWorkOrderMapper.xml`
- `ruoyi-system/src/test/java/com/ruoyi/system/service/information/impl/InfoResourceServiceImplTest.java`
- `ruoyi-system/src/test/java/com/ruoyi/system/service/information/impl/InfoWorkOrderServiceImplTest.java`
contracts_affected:
- `/information/resource/*`
- `/information/resource/order/*`
- 资源状态与资源工单类型约束
tests_run:
- `mvn -pl ruoyi-admin -am test`
known_issues:
- None
