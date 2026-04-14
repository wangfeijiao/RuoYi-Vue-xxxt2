package com.ruoyi.web.controller.information;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.information.InfoApproveBody;
import com.ruoyi.system.domain.information.InfoExecuteBody;
import com.ruoyi.system.domain.information.InfoProjectDocument;
import com.ruoyi.system.domain.information.InfoProjectDocumentArchiveBody;
import com.ruoyi.system.domain.information.InfoProjectDocumentComplianceBody;
import com.ruoyi.system.domain.information.InfoProject;
import com.ruoyi.system.domain.information.InfoProjectPermissionRel;
import com.ruoyi.system.domain.information.InfoProjectSpaceDirectory;
import com.ruoyi.system.domain.information.InfoProjectTemplate;
import com.ruoyi.system.domain.information.InfoWorkOrder;
import com.ruoyi.system.service.information.IInfoProjectDocumentService;
import com.ruoyi.system.service.information.IInfoProjectPermissionService;
import com.ruoyi.system.service.information.IInfoProjectService;
import com.ruoyi.system.service.information.IInfoProjectSpaceDirectoryService;
import com.ruoyi.system.service.information.IInfoWorkOrderService;

@RestController
@RequestMapping("/information/project")
public class InfoProjectController extends BaseController
{
    @Autowired
    private IInfoProjectService projectService;

    @Autowired
    private IInfoWorkOrderService workOrderService;

    @Autowired
    private IInfoProjectPermissionService projectPermissionService;

    @Autowired
    private IInfoProjectSpaceDirectoryService projectSpaceDirectoryService;

    @Autowired
    private IInfoProjectDocumentService projectDocumentService;

    @PreAuthorize("@ss.hasPermi('information:project:list')")
    @GetMapping("/list")
    public TableDataInfo list(InfoProject project)
    {
        startPage();
        List<InfoProject> list = projectService.selectInfoProjectList(project);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('information:project:query')")
    @GetMapping("/{projectId}")
    public AjaxResult getInfo(@PathVariable Long projectId)
    {
        return success(projectService.selectInfoProjectById(projectId));
    }

    @PreAuthorize("@ss.hasPermi('information:project:detail')")
    @GetMapping("/{projectId}/detail")
    public AjaxResult detail(@PathVariable Long projectId)
    {
        return success(projectService.selectProjectDetail(projectId));
    }

    @PreAuthorize("@ss.hasPermi('information:project:add')")
    @Log(title = "信息化项目", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody InfoProject project)
    {
        project.setCreateBy(getUsername());
        return toAjax(projectService.insertInfoProject(project));
    }

    @PreAuthorize("@ss.hasPermi('information:project:edit')")
    @Log(title = "信息化项目", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody InfoProject project)
    {
        project.setUpdateBy(getUsername());
        return toAjax(projectService.updateInfoProject(project));
    }

    @PreAuthorize("@ss.hasPermi('information:project:remove')")
    @Log(title = "信息化项目", businessType = BusinessType.DELETE)
    @DeleteMapping("/{projectIds}")
    public AjaxResult remove(@PathVariable Long[] projectIds)
    {
        return toAjax(projectService.deleteInfoProjectByIds(projectIds));
    }

    @PreAuthorize("@ss.hasPermi('information:projectTemplate:list')")
    @GetMapping("/template/list")
    public TableDataInfo templateList(InfoProjectTemplate template)
    {
        startPage();
        return getDataTable(projectService.selectInfoProjectTemplateList(template));
    }

    @PreAuthorize("@ss.hasPermi('information:projectTemplate:query')")
    @GetMapping("/template/{templateId}")
    public AjaxResult templateInfo(@PathVariable Long templateId)
    {
        return success(projectService.selectInfoProjectTemplateById(templateId));
    }

    @PreAuthorize("@ss.hasPermi('information:projectTemplate:add')")
    @PostMapping("/template")
    public AjaxResult addTemplate(@RequestBody InfoProjectTemplate template)
    {
        template.setCreateBy(getUsername());
        return toAjax(projectService.insertInfoProjectTemplate(template));
    }

    @PreAuthorize("@ss.hasPermi('information:projectTemplate:edit')")
    @PutMapping("/template")
    public AjaxResult editTemplate(@RequestBody InfoProjectTemplate template)
    {
        template.setUpdateBy(getUsername());
        return toAjax(projectService.updateInfoProjectTemplate(template));
    }

    @PreAuthorize("@ss.hasPermi('information:projectTemplate:remove')")
    @DeleteMapping("/template/{templateIds}")
    public AjaxResult removeTemplate(@PathVariable Long[] templateIds)
    {
        return toAjax(projectService.deleteInfoProjectTemplateByIds(templateIds));
    }

    @PreAuthorize("@ss.hasPermi('information:projectOrder:list')")
    @GetMapping("/order/list")
    public TableDataInfo orderList(InfoWorkOrder workOrder)
    {
        workOrder.setDomainType("PROJECT");
        workOrder.setRequestType("ACCEPTANCE");
        startPage();
        return getDataTable(workOrderService.selectInfoWorkOrderList(workOrder));
    }

    @PreAuthorize("@ss.hasPermi('information:projectOrder:add')")
    @PostMapping("/order")
    public AjaxResult addOrder(@RequestBody InfoWorkOrder workOrder)
    {
        workOrder.setDomainType("PROJECT");
        workOrder.setRequestType("ACCEPTANCE");
        workOrder.setCreateBy(getUsername());
        return toAjax(workOrderService.insertInfoWorkOrder(workOrder));
    }

    @PreAuthorize("@ss.hasPermi('information:projectOrder:edit')")
    @PutMapping("/order")
    public AjaxResult editOrder(@RequestBody InfoWorkOrder workOrder)
    {
        workOrder.setDomainType("PROJECT");
        workOrder.setRequestType("ACCEPTANCE");
        workOrder.setUpdateBy(getUsername());
        return toAjax(workOrderService.updateInfoWorkOrder(workOrder));
    }

    @PreAuthorize("@ss.hasPermi('information:projectOrder:approve')")
    @PostMapping("/order/{workOrderId}/approve")
    public AjaxResult approve(@PathVariable Long workOrderId, @RequestBody InfoApproveBody body)
    {
        return toAjax(workOrderService.approveInfoWorkOrder(workOrderId, getUsername(), body));
    }

    @PreAuthorize("@ss.hasPermi('information:projectOrder:execute')")
    @PostMapping("/order/{workOrderId}/execute")
    public AjaxResult execute(@PathVariable Long workOrderId, @RequestBody InfoExecuteBody body)
    {
        return toAjax(workOrderService.executeInfoWorkOrder(workOrderId, body));
    }

    @PreAuthorize("@ss.hasPermi('information:project:permission')")
    @GetMapping("/{projectId}/permissions")
    public AjaxResult listPermissions(@PathVariable Long projectId, InfoProjectPermissionRel permission)
    {
        permission.setProjectId(projectId);
        return success(projectPermissionService.selectInfoProjectPermissionList(permission));
    }

    @PreAuthorize("@ss.hasPermi('information:project:permission')")
    @PostMapping("/{projectId}/permissions")
    public AjaxResult addPermission(@PathVariable Long projectId, @RequestBody InfoProjectPermissionRel permission)
    {
        permission.setProjectId(projectId);
        permission.setCreateBy(getUsername());
        return toAjax(projectPermissionService.insertProjectPermission(permission));
    }

    @PreAuthorize("@ss.hasPermi('information:project:permission')")
    @PutMapping("/{projectId}/permissions/{permissionId}")
    public AjaxResult editPermission(@PathVariable Long projectId, @PathVariable Long permissionId,
        @RequestBody InfoProjectPermissionRel permission)
    {
        permission.setProjectId(projectId);
        permission.setPermissionId(permissionId);
        permission.setUpdateBy(getUsername());
        return toAjax(projectPermissionService.updateProjectPermission(permission));
    }

    @PreAuthorize("@ss.hasPermi('information:project:permission')")
    @DeleteMapping("/{projectId}/permissions/{permissionId}")
    public AjaxResult removePermission(@PathVariable Long projectId, @PathVariable Long permissionId)
    {
        return toAjax(projectPermissionService.deleteProjectPermission(projectId, permissionId));
    }

    @PreAuthorize("@ss.hasPermi('information:project:permission')")
    @GetMapping("/{projectId}/permission-matrix")
    public AjaxResult permissionMatrix(@PathVariable Long projectId)
    {
        return success(projectPermissionService.selectProjectPermissionMatrix(projectId));
    }

    @PreAuthorize("@ss.hasPermi('information:project:space')")
    @GetMapping("/{projectId}/space/directories")
    public AjaxResult listSpaceDirectories(@PathVariable Long projectId)
    {
        return success(projectSpaceDirectoryService.selectInfoProjectSpaceDirectoryList(projectId));
    }

    @PreAuthorize("@ss.hasPermi('information:project:space')")
    @PostMapping("/{projectId}/space/init")
    public AjaxResult initProjectSpace(@PathVariable Long projectId)
    {
        return toAjax(projectSpaceDirectoryService.initProjectSpace(projectId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('information:project:space')")
    @PostMapping("/{projectId}/space/directories/custom")
    public AjaxResult addCustomDirectory(@PathVariable Long projectId, @RequestBody InfoProjectSpaceDirectory directory)
    {
        directory.setCreateBy(getUsername());
        return toAjax(projectSpaceDirectoryService.insertCustomDirectory(projectId, directory));
    }

    @PreAuthorize("@ss.hasPermi('information:project:document')")
    @GetMapping("/{projectId}/documents")
    public AjaxResult listDocuments(@PathVariable Long projectId)
    {
        return success(projectDocumentService.selectInfoProjectDocumentList(projectId));
    }

    @PreAuthorize("@ss.hasPermi('information:project:document')")
    @PostMapping("/{projectId}/documents")
    public AjaxResult addDocument(@PathVariable Long projectId, @RequestBody InfoProjectDocument document)
    {
        document.setCreateBy(getUsername());
        return toAjax(projectDocumentService.insertProjectDocument(projectId, document));
    }

    @PreAuthorize("@ss.hasPermi('information:project:document')")
    @PostMapping("/documents/{documentId}/compliance")
    public AjaxResult updateDocumentCompliance(@PathVariable Long documentId,
        @RequestBody InfoProjectDocumentComplianceBody body)
    {
        return toAjax(projectDocumentService.updateDocumentCompliance(documentId,
            body == null ? null : body.getComplianceStatus(),
            body == null ? null : body.getCheckComment(),
            getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('information:project:document')")
    @PostMapping("/documents/{documentId}/archive")
    public AjaxResult archiveDocument(@PathVariable Long documentId, @RequestBody InfoProjectDocumentArchiveBody body)
    {
        return toAjax(projectDocumentService.archiveDocument(documentId, body == null ? null : body.getArchiveStatus(),
            getUsername()));
    }
}
