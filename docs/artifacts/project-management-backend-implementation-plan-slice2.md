[artifact:ImplementationPlan]
status: READY
owner: senior-backend-engineer
scope:
- Information project management phase-1 slice-2 backend delivery: project space directory initialization/custom directory, project documents (create/compliance/archive), and lightweight project detail aggregate endpoint
inputs:
- [artifact:PRD] `docs/artifacts/project-management-prd.md`
- [artifact:Prototype] `docs/artifacts/project-management-prototype.md`
- [artifact:SystemArch] `docs/artifacts/project-management-system-arch.md`
- [artifact:DBDesign] `docs/artifacts/project-management-db-design.md`
- [artifact:APIDoc] `docs/artifacts/project-management-api-doc.md`
- [artifact:TaskBreakdown] `docs/artifacts/project-management-task-breakdown.md`
- [artifact:Approval] `docs/artifacts/project-management-approval.md`
deliverables:
- New backend tables and persistence layer for `inf_project_space_directory` and `inf_project_document`
- New project APIs:
- `GET /information/project/{projectId}/space/directories`
- `POST /information/project/{projectId}/space/init`
- `POST /information/project/{projectId}/space/directories/custom`
- `GET /information/project/{projectId}/documents`
- `POST /information/project/{projectId}/documents`
- `POST /information/project/documents/{documentId}/compliance`
- `POST /information/project/documents/{documentId}/archive`
- `GET /information/project/{projectId}/detail`
- Unit tests for directory initialization, custom directory create, document create/compliance/archive, and detail aggregate summary
handoff_to:
- code-reviewer
- security-engineer
- senior-qa-engineer
exit_criteria:
- New APIs and services compile successfully in `ruoyi-system` and `ruoyi-admin`
- Targeted tests for new slice pass
- New backend artifact `[artifact:BackendCode]` is delivered with changed files and verification evidence
goal:
- Deliver the approved project-space and project-document capabilities and provide a lightweight detail aggregate API without breaking existing project CRUD/order compatibility
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
- Add failing unit tests for project space initialization/custom directory, project document lifecycle actions, and detail aggregate summary
- Add new domain models, mapper interfaces/XML, and service contracts for project space directories and project documents
- Implement service logic with project existence validation, template snapshot parsing, status defaulting, and update flows
- Extend `InfoProjectController` with approved APIs and required permissions
- Extend project service with `detail` aggregate endpoint logic and summary calculations
- Append SQL DDL for new tables and indexes
- Run focused Maven tests and admin compile verification
risks:
- Snapshot parser assumptions could reject some legacy snapshots if structure diverges from expected fields
- Mock-based unit tests cannot fully cover MyBatis generated-key runtime behavior
- Detail aggregate uses lightweight summaries only; deeper collaboration/review aggregates remain out of this slice
validation:
- `mvn -pl ruoyi-system "-Dtest=InfoProjectSpaceDirectoryServiceImplTest,InfoProjectDocumentServiceImplTest,InfoProjectServiceImplTest" test`
- `mvn -pl ruoyi-admin -am -DskipTests compile`
