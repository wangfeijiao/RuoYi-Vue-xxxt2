[artifact:BackendCode]
status: READY
owner: senior-backend-engineer
scope:
- Deliver phase-1 slice-2 backend implementation for project space directories, project documents, and project detail aggregate APIs
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
- Added `inf_project_space_directory` and `inf_project_document` table DDL in SQL baseline
- Implemented new backend domain, mapper, service, and controller endpoints for project space/document lifecycle
- Implemented lightweight `GET /information/project/{projectId}/detail` aggregate with base/template/space/collaboration/acceptance/permission summaries
- Added unit tests covering space init/custom directory, document create/compliance/archive, and detail summary
risks:
- Template snapshot parsing is tolerant but still depends on valid JSON shape; malformed snapshots are rejected with service errors
- Detail aggregate currently returns lightweight summaries only and does not include collaboration/review sub-table deep aggregation
handoff_to:
- code-reviewer
- security-engineer
- senior-qa-engineer
exit_criteria:
- Backend compile passes for `ruoyi-system` and `ruoyi-admin`
- Targeted unit tests for this slice pass
- New APIs are wired under approved permissions and routes
changed_files:
- `docs/artifacts/project-management-backend-implementation-plan-slice2.md`
- `ruoyi-admin/src/main/java/com/ruoyi/web/controller/information/InfoProjectController.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/domain/information/InfoProjectDocument.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/domain/information/InfoProjectDocumentArchiveBody.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/domain/information/InfoProjectDocumentComplianceBody.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/domain/information/InfoProjectSpaceDirectory.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/mapper/information/InfoProjectDocumentMapper.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/mapper/information/InfoProjectPermissionRelMapper.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/mapper/information/InfoProjectSpaceDirectoryMapper.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/information/IInfoProjectDocumentService.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/information/IInfoProjectService.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/information/IInfoProjectSpaceDirectoryService.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/information/impl/InfoProjectDocumentServiceImpl.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/information/impl/InfoProjectServiceImpl.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/information/impl/InfoProjectSpaceDirectoryServiceImpl.java`
- `ruoyi-system/src/main/resources/mapper/information/InfoProjectDocumentMapper.xml`
- `ruoyi-system/src/main/resources/mapper/information/InfoProjectPermissionRelMapper.xml`
- `ruoyi-system/src/main/resources/mapper/information/InfoProjectSpaceDirectoryMapper.xml`
- `ruoyi-system/src/test/java/com/ruoyi/system/service/information/impl/InfoProjectDocumentServiceImplTest.java`
- `ruoyi-system/src/test/java/com/ruoyi/system/service/information/impl/InfoProjectServiceImplTest.java`
- `ruoyi-system/src/test/java/com/ruoyi/system/service/information/impl/InfoProjectSpaceDirectoryServiceImplTest.java`
- `sql/information_management.sql`
contracts_affected:
- `GET /information/project/{projectId}/detail` (new)
- `GET /information/project/{projectId}/space/directories` (new)
- `POST /information/project/{projectId}/space/init` (new)
- `POST /information/project/{projectId}/space/directories/custom` (new)
- `GET /information/project/{projectId}/documents` (new)
- `POST /information/project/{projectId}/documents` (new)
- `POST /information/project/documents/{documentId}/compliance` (new)
- `POST /information/project/documents/{documentId}/archive` (new)
- SQL tables `inf_project_space_directory`, `inf_project_document` (new)
- Mapper contract `InfoProjectPermissionRelMapper#countByProjectId` (new)
tests_run:
- `mvn -pl ruoyi-system "-Dtest=InfoProjectSpaceDirectoryServiceImplTest,InfoProjectDocumentServiceImplTest,InfoProjectServiceImplTest" test` (PASS)
- `mvn -pl ruoyi-admin -am -DskipTests compile` (PASS)
known_issues:
- Existing project frontend page is not yet wired to the new space/document/detail APIs in this slice
