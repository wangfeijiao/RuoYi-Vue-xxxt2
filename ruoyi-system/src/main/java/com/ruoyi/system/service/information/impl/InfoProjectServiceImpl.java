package com.ruoyi.system.service.information.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.information.InfoProject;
import com.ruoyi.system.domain.information.InfoProjectTemplate;
import com.ruoyi.system.domain.information.InfoWorkOrder;
import com.ruoyi.system.mapper.information.InfoProjectDocumentMapper;
import com.ruoyi.system.mapper.information.InfoProjectMapper;
import com.ruoyi.system.mapper.information.InfoProjectPermissionRelMapper;
import com.ruoyi.system.mapper.information.InfoProjectSpaceDirectoryMapper;
import com.ruoyi.system.mapper.information.InfoProjectTemplateMapper;
import com.ruoyi.system.mapper.information.InfoWorkOrderMapper;
import com.ruoyi.system.service.information.IInfoProjectService;

@Service
public class InfoProjectServiceImpl implements IInfoProjectService
{
    @Autowired
    private InfoProjectMapper projectMapper;

    @Autowired
    private InfoProjectTemplateMapper projectTemplateMapper;

    @Autowired
    private InfoProjectSpaceDirectoryMapper spaceDirectoryMapper;

    @Autowired
    private InfoProjectDocumentMapper documentMapper;

    @Autowired
    private InfoWorkOrderMapper workOrderMapper;

    @Autowired
    private InfoProjectPermissionRelMapper permissionMapper;

    @Override
    public InfoProject selectInfoProjectById(Long projectId)
    {
        return projectMapper.selectInfoProjectById(projectId);
    }

    @Override
    public List<InfoProject> selectInfoProjectList(InfoProject project)
    {
        return projectMapper.selectInfoProjectList(project);
    }

    @Override
    public Map<String, Object> selectProjectDetail(Long projectId)
    {
        InfoProject project = projectMapper.selectInfoProjectById(projectId);
        if (project == null)
        {
            throw new ServiceException("Project does not exist");
        }

        int directoryTotal = spaceDirectoryMapper.countByProjectId(projectId);
        int requiredDirectoryTotal = spaceDirectoryMapper.countRequiredDirectoryByProjectId(projectId);
        int documentTotal = documentMapper.countByProjectId(projectId);
        int passedDocumentTotal = documentMapper.countPassedComplianceByProjectId(projectId);
        int archivedDocumentTotal = documentMapper.countArchivedByProjectId(projectId);
        double completionRate = calculateCompletionRate(documentTotal, passedDocumentTotal);

        InfoWorkOrder workOrderQuery = new InfoWorkOrder();
        workOrderQuery.setProjectId(projectId);
        workOrderQuery.setDomainType("PROJECT");
        workOrderQuery.setRequestType("ACCEPTANCE");
        List<InfoWorkOrder> acceptanceOrders = workOrderMapper.selectInfoWorkOrderList(workOrderQuery);
        int pendingOrderTotal = 0;
        int completedOrderTotal = 0;
        for (InfoWorkOrder order : acceptanceOrders)
        {
            if ("COMPLETED".equals(order.getOrderStatus()))
            {
                completedOrderTotal++;
            }
            else
            {
                pendingOrderTotal++;
            }
        }

        Map<String, Object> baseInfo = new LinkedHashMap<>();
        baseInfo.put("projectId", project.getProjectId());
        baseInfo.put("projectCode", project.getProjectCode());
        baseInfo.put("projectName", project.getProjectName());
        baseInfo.put("projectType", project.getProjectType());
        baseInfo.put("projectStatus", project.getProjectStatus());
        baseInfo.put("projectPhase", project.getProjectPhase());
        baseInfo.put("projectManager", project.getProjectManager());
        baseInfo.put("acceptanceStatus", project.getAcceptanceStatus());
        baseInfo.put("archiveStatus", project.getArchiveStatus());

        Map<String, Object> templateSnapshot = new LinkedHashMap<>();
        templateSnapshot.put("templateId", project.getTemplateId());
        templateSnapshot.put("templateVersionNo", project.getTemplateVersionNo());
        templateSnapshot.put("directoryTemplateJson", project.getDirectoryTemplateJson());
        templateSnapshot.put("customDirectoryJson", project.getCustomDirectoryJson());

        Map<String, Object> spaceSummary = new LinkedHashMap<>();
        spaceSummary.put("spaceInitStatus", project.getSpaceInitStatus());
        spaceSummary.put("directoryTotal", directoryTotal);
        spaceSummary.put("requiredDirectoryTotal", requiredDirectoryTotal);
        spaceSummary.put("documentTotal", documentTotal);
        spaceSummary.put("passedDocumentTotal", passedDocumentTotal);
        spaceSummary.put("archivedDocumentTotal", archivedDocumentTotal);
        spaceSummary.put("documentCompletionRate", completionRate);

        Map<String, Object> collaborationSummary = new LinkedHashMap<>();
        collaborationSummary.put("lastCollaborationTime", project.getLastCollaborationTime());

        Map<String, Object> acceptanceSummary = new LinkedHashMap<>();
        acceptanceSummary.put("acceptanceStatus", project.getAcceptanceStatus());
        acceptanceSummary.put("currentAcceptanceOrderId", project.getCurrentAcceptanceOrderId());
        acceptanceSummary.put("orderTotal", acceptanceOrders.size());
        acceptanceSummary.put("pendingOrderTotal", pendingOrderTotal);
        acceptanceSummary.put("completedOrderTotal", completedOrderTotal);

        Map<String, Object> permissionSummary = new LinkedHashMap<>();
        permissionSummary.put("permissionTotal", permissionMapper.countByProjectId(projectId));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("baseInfo", baseInfo);
        result.put("templateSnapshot", templateSnapshot);
        result.put("spaceSummary", spaceSummary);
        result.put("collaborationSummary", collaborationSummary);
        result.put("acceptanceSummary", acceptanceSummary);
        result.put("permissionSummary", permissionSummary);
        return result;
    }

    @Override
    public int insertInfoProject(InfoProject project)
    {
        populateProjectDefaults(project);
        return projectMapper.insertInfoProject(project);
    }

    @Override
    public int updateInfoProject(InfoProject project)
    {
        return projectMapper.updateInfoProject(project);
    }

    @Override
    public int deleteInfoProjectByIds(Long[] projectIds)
    {
        return projectMapper.deleteInfoProjectByIds(projectIds);
    }

    @Override
    public InfoProjectTemplate selectInfoProjectTemplateById(Long templateId)
    {
        return projectTemplateMapper.selectInfoProjectTemplateById(templateId);
    }

    @Override
    public List<InfoProjectTemplate> selectInfoProjectTemplateList(InfoProjectTemplate template)
    {
        return projectTemplateMapper.selectInfoProjectTemplateList(template);
    }

    @Override
    public int insertInfoProjectTemplate(InfoProjectTemplate template)
    {
        return projectTemplateMapper.insertInfoProjectTemplate(template);
    }

    @Override
    public int updateInfoProjectTemplate(InfoProjectTemplate template)
    {
        return projectTemplateMapper.updateInfoProjectTemplate(template);
    }

    @Override
    public int deleteInfoProjectTemplateByIds(Long[] templateIds)
    {
        return projectTemplateMapper.deleteInfoProjectTemplateByIds(templateIds);
    }

    private void populateProjectDefaults(InfoProject project)
    {
        if (StringUtils.isEmpty(project.getProjectStatus()))
        {
            project.setProjectStatus("DRAFT");
        }
        if (StringUtils.isEmpty(project.getAcceptanceStatus()))
        {
            project.setAcceptanceStatus("PENDING");
        }
        if (StringUtils.isEmpty(project.getSpaceInitStatus()))
        {
            project.setSpaceInitStatus("PENDING");
        }
        if (StringUtils.isEmpty(project.getArchiveStatus()))
        {
            project.setArchiveStatus("UNARCHIVED");
        }
        if (project.getDocumentCompletionRate() == null)
        {
            project.setDocumentCompletionRate(BigDecimal.ZERO);
        }
    }

    private double calculateCompletionRate(int documentTotal, int passedDocumentTotal)
    {
        if (documentTotal <= 0 || passedDocumentTotal <= 0)
        {
            return 0D;
        }
        return BigDecimal.valueOf(passedDocumentTotal * 100.0 / documentTotal)
            .setScale(2, RoundingMode.HALF_UP)
            .doubleValue();
    }
}
