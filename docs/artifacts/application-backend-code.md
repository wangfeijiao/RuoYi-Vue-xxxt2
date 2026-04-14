[artifact:BackendCode]
status: READY
owner: senior-backend-engineer
scope:
- Application management Phase 1 backend delivery and post-review/security hardening
inputs:
- [artifact:ImplementationPlan] docs/artifacts/application-backend-implementation-plan.md
- [artifact:SystemArch] docs/artifacts/application-system-arch.md
- [artifact:DBDesign] docs/artifacts/application-db-design.md
- [artifact:APIDoc] docs/artifacts/application-api-doc.md
- [artifact:Approval] docs/artifacts/application-approval.md
deliverables:
- Completed application master data, project relation, dependency relation, alert, notice, overview, and statistics backend flows for Phase 1.
- Hardened update flow so protected fields are preserved server-side and no longer updated through the generic application edit SQL.
- Fixed linkage recomputation to only count dependencies with `statusLinkEnabled='1'`.
- Fixed primary project synchronization so clearing `projectId` also clears the primary relation and `inf_application.project_id`.
- Sanitized dependency and alert responses so `targetSnapshotJson` and `payloadJson` are not returned to the frontend.
- Added service-layer regression tests for protected field preservation, project clearing, linkage filtering, response sanitization, and alert handler identity.
risks:
- Browser-side visual verification is still pending; current evidence for the network dependency path is backend/API smoke plus database verification.
handoff_to:
- code-reviewer
- security-engineer
- senior-qa-engineer
exit_criteria:
- Backend compiles, packages, and key service regressions pass in the approved Phase 1 scope.
changed_files:
- ruoyi-admin/src/main/java/com/ruoyi/web/controller/information/InfoApplicationController.java
- ruoyi-system/src/main/java/com/ruoyi/system/domain/information/InfoApplication.java
- ruoyi-system/src/main/java/com/ruoyi/system/domain/information/InfoApplicationProjectRel.java
- ruoyi-system/src/main/java/com/ruoyi/system/domain/information/InfoApplicationDependencyRel.java
- ruoyi-system/src/main/java/com/ruoyi/system/domain/information/InfoApplicationAlertEvent.java
- ruoyi-system/src/main/java/com/ruoyi/system/domain/information/InfoApplicationNoticeLog.java
- ruoyi-system/src/main/java/com/ruoyi/system/domain/information/InfoApplicationAlertHandleBody.java
- ruoyi-system/src/main/java/com/ruoyi/system/mapper/information/InfoApplicationMapper.java
- ruoyi-system/src/main/java/com/ruoyi/system/service/information/IInfoApplicationService.java
- ruoyi-system/src/main/java/com/ruoyi/system/service/information/impl/InfoApplicationServiceImpl.java
- ruoyi-system/src/main/resources/mapper/information/InfoApplicationMapper.xml
- ruoyi-system/src/test/java/com/ruoyi/system/service/information/impl/InfoApplicationServiceImplTest.java
- sql/application_management_phase1.sql
contracts_affected:
- `/information/application/*`
- `inf_application`
- `inf_application_project_rel`
- `inf_application_dependency_rel`
- `inf_application_alert_event`
- `inf_application_notice_log`
tests_run:
- `mvn -pl ruoyi-system -Dtest=InfoApplicationServiceImplTest test`
- `mvn -pl ruoyi-admin -am -DskipTests compile`
- `mvn -pl ruoyi-admin -am -DskipTests package`
- `mysql -uroot -p*** -D ry-vue-xxxt2 -e "SELECT ... FROM inf_application / inf_project / inf_resource / inf_network_resource / inf_application_dependency_rel / inf_application_alert_event / inf_application_notice_log"`
known_issues:
- None
