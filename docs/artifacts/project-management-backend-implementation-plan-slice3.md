[artifact:ImplementationPlan]
status: READY
owner: senior-backend-engineer
scope:
- Information project management phase-1 slice-3 backend delivery: collaboration records, multi-line acceptance reviews, and project statistics APIs
inputs:
- [artifact:PRD] `docs/artifacts/project-management-prd.md`
- [artifact:Prototype] `docs/artifacts/project-management-prototype.md`
- [artifact:SystemArch] `docs/artifacts/project-management-system-arch.md`
- [artifact:DBDesign] `docs/artifacts/project-management-db-design.md`
- [artifact:APIDoc] `docs/artifacts/project-management-api-doc.md`
- [artifact:TaskBreakdown] `docs/artifacts/project-management-task-breakdown.md`
- [artifact:Approval] `docs/artifacts/project-management-approval.md`
- [artifact:ImplementationPlan] `docs/artifacts/project-management-backend-implementation-plan-slice2.md`
deliverables:
- New backend tables and persistence layer for `inf_project_collaboration_record` and `inf_project_acceptance_review`
- New project APIs:
- `GET /information/project/{projectId}/collaboration`
- `POST /information/project/{projectId}/collaboration`
- `GET /information/project/order/{workOrderId}/reviews`
- `POST /information/project/order/{workOrderId}/reviews`
- `POST /information/project/order/{workOrderId}/reviews/{reviewId}/decision`
- `GET /information/project/statistics/overview`
- `GET /information/project/statistics/compliance`
- `GET /information/project/statistics/acceptance`
- Approval gate enhancement for project acceptance main order: required review lines must be completed before aggregate approve
- Unit tests for collaboration create/list, review initialize/decision, and order approval gating
risks:
- Existing acceptance flow already in use; adding review gate must remain limited to `domainType=PROJECT` and `requestType=ACCEPTANCE`
- Review line initialization defaults may not cover all tenant-specific line models; should support custom payload rebuild
- Statistics APIs are summary-oriented and do not include cross-module analytics join logic in this slice
handoff_to:
- code-reviewer
- security-engineer
- senior-qa-engineer
exit_criteria:
- Collaboration/review/statistics APIs compile and are callable under approved permissions
- Project acceptance main-order approve is blocked when required review lines are unfinished or rejected
- Targeted tests pass and backend code artifact is delivered
goal:
- Complete the remaining backend core of approved phase-1 scope so project lifecycle can run with collaboration trace, line-level review, and management summaries
changed_areas:
- `sql/information_management.sql`
- `ruoyi-system/src/main/java/com/ruoyi/system/domain/information/*`
- `ruoyi-system/src/main/java/com/ruoyi/system/mapper/information/*`
- `ruoyi-system/src/main/resources/mapper/information/*`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/information/*`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/information/impl/*`
- `ruoyi-admin/src/main/java/com/ruoyi/web/controller/information/InfoProjectController.java`
- `ruoyi-system/src/test/java/com/ruoyi/system/service/information/impl/*`
steps:
- Add failing tests for collaboration insertion side effect on project last collaboration time
- Add failing tests for review line initialization and decision update
- Add failing tests for acceptance approve gate with pending required review lines
- Implement collaboration record domain/mapper/service/controller
- Implement acceptance review domain/mapper/service/controller and default line bootstrap logic
- Integrate required-line gate check into `InfoWorkOrderServiceImpl#approveInfoWorkOrder`
- Add project statistics service methods and controller endpoints
- Append SQL DDL and indexes for collaboration/review tables
- Run focused tests and admin compile verification
risks:
- Snapshot and status data quality in existing rows can affect statistics precision
- Mock-based tests cover service logic but not full transaction behavior against real DB constraints
- Default review-line dictionary values are static in this slice
validation:
- `mvn -pl ruoyi-system "-Dtest=InfoProjectCollaborationRecordServiceImplTest,InfoProjectAcceptanceReviewServiceImplTest,InfoWorkOrderServiceImplTest,InfoProjectServiceImplTest" test`
- `mvn -pl ruoyi-admin -am -DskipTests compile`
