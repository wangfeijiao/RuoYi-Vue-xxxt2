[artifact:TestCase]
status: READY
owner: senior-qa-engineer
scope:
- Functional, regression, and boundary coverage for application management Phase 1 after review/security fixes
inputs:
- [artifact:PRD] docs/artifacts/application-prd.md
- [artifact:Prototype] docs/artifacts/application-prototype.md
- [artifact:APIDoc] docs/artifacts/application-api-doc.md
- [artifact:BackendCode] docs/artifacts/application-backend-code.md
- [artifact:FrontendCode] docs/artifacts/application-frontend-code.md
- [artifact:ReviewReport] docs/artifacts/application-review-report.md
- [artifact:SecurityReport] docs/artifacts/application-security-report.md
deliverables:
- Verify application create/edit/delete/list flow still works for Phase 1 fields.
- Verify clearing the main `projectId` removes the primary project relation and clears `inf_application.project_id`.
- Verify dependencies with `statusLinkEnabled='0'` do not drive linkage status or abnormal dependency counts.
- Verify manual/external dependencies with empty `targetId` and `targetKey` are de-duplicated by `targetName` instead of collapsing unrelated records.
- Verify alert acknowledge/resolve records the current operator as `handlerName` and updates notice business status.
- Verify dependency and alert query/detail responses do not expose `targetSnapshotJson` or `payloadJson`.
- Verify `NETWORK` dependency options are loaded from `/information/network/list` when environment data exists.
- Verify the detail dialog resets stale data and surfaces an error if one of the parallel detail APIs fails.
- Verify the real environment can complete the authenticated API smoke path with seeded samples `NET-2026-PARK-CORE` and `APP-2026-NETFLOW`.
- Verify the authenticated browser flow after login can visually complete application list access, detail dialog inspection, and `NETWORK` dependency candidate selection.
risks:
- None
handoff_to:
- senior-qa-engineer
exit_criteria:
- Critical Phase 1 business paths, boundary conditions, and regression checks have an explicit execution basis.
