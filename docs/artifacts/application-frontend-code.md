[artifact:FrontendCode]
status: READY
owner: senior-frontend-engineer
scope:
- Application management Phase 1 frontend delivery and post-review fixes
inputs:
- [artifact:ImplementationPlan] docs/artifacts/application-frontend-implementation-plan.md
- [artifact:APIDoc] docs/artifacts/application-api-doc.md
- [artifact:Approval] docs/artifacts/application-approval.md
deliverables:
- Delivered the application list, detail dialog, project relation, dependency relation, alert, notice, and overview UI for Phase 1.
- Added real network candidate loading through `/information/network/list` so `NETWORK` dependencies reuse actual environment data when present.
- Added detail loading reset and failure handling so partial `Promise.all` failures do not leave stale data in the dialog.
- Kept the edit form compatible with clearing `projectId`, which now maps to the fixed backend synchronization logic.
risks:
- None
handoff_to:
- code-reviewer
- senior-qa-engineer
exit_criteria:
- Frontend production build passes and the approved Phase 1 views remain routable and compilable.
changed_files:
- ruoyi-ui/src/api/information/application.js
- ruoyi-ui/src/views/information/application/index.vue
contracts_affected:
- `/information/application/list`
- `/information/application/{id}`
- `/information/application/{id}/detail`
- `/information/application/{id}/projects`
- `/information/application/{id}/dependencies`
- `/information/application/{id}/status-overview`
- `/information/application/{id}/alerts`
- `/information/application/{id}/notices`
- `/information/application/statistics/overview`
- `/information/network/list`
tests_run:
- `npm run build:prod`
- Authenticated browser smoke on `http://localhost:8082/information/application` with `admin/admin123`, covering list render, detail dialog, dependency tab, status overview tab, and Add Dependency -> Network candidate loading
known_issues:
- None
