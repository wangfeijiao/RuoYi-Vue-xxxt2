[artifact:FrontendCode]
status: READY
owner: senior-frontend-engineer
scope:
- Refine the IT resource page usability without changing approved backend contracts
inputs:
- [artifact:ImplementationPlan] docs/artifacts/it-resource-frontend-implementation-plan.md
- [artifact:Approval] docs/artifacts/it-resource-approval.md
- [artifact:APIDoc] docs/artifacts/it-resource-api-doc.md
deliverables:
- Added project selectors to the resource form and resource-apply work order form
- Added resource work order detail dialog for request, approval, execution, snapshot, and payload traceability
- Added order action gating in the UI for approve/execute/delete based on approved work-order states
- Extended resource query with delivery time range and kept resource/order views aligned to approved status values
risks:
- Browser-side manual regression is still recommended for real permission accounts and real data linkage
handoff_to:
- code-reviewer
- senior-qa-engineer
exit_criteria:
- Resource page behavior matches approved workflow constraints and frontend production build passes
changed_files:
- `ruoyi-ui/src/views/information/resource/index.vue`
contracts_affected:
- `/information/resource/*`
- `/information/resource/order/*`
- `/information/project/list`
tests_run:
- `npm run build:prod`
known_issues:
- None
