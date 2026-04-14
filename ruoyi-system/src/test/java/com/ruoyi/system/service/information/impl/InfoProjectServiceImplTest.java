package com.ruoyi.system.service.information.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ruoyi.system.domain.information.InfoProject;
import com.ruoyi.system.domain.information.InfoWorkOrder;
import com.ruoyi.system.mapper.information.InfoProjectDocumentMapper;
import com.ruoyi.system.mapper.information.InfoProjectMapper;
import com.ruoyi.system.mapper.information.InfoProjectPermissionRelMapper;
import com.ruoyi.system.mapper.information.InfoProjectSpaceDirectoryMapper;
import com.ruoyi.system.mapper.information.InfoProjectTemplateMapper;
import com.ruoyi.system.mapper.information.InfoWorkOrderMapper;

@ExtendWith(MockitoExtension.class)
public class InfoProjectServiceImplTest
{
    @Mock
    private InfoProjectMapper projectMapper;

    @Mock
    private InfoProjectTemplateMapper projectTemplateMapper;

    @Mock
    private InfoProjectSpaceDirectoryMapper spaceDirectoryMapper;

    @Mock
    private InfoProjectDocumentMapper documentMapper;

    @Mock
    private InfoWorkOrderMapper workOrderMapper;

    @Mock
    private InfoProjectPermissionRelMapper permissionMapper;

    @InjectMocks
    private InfoProjectServiceImpl service;

    @Test
    public void shouldApplyDefaultLifecycleFieldsWhenInsertProject()
    {
        InfoProject project = new InfoProject();
        project.setProjectCode("PRJ-001");
        project.setProjectName("园区数智化改造");
        project.setProjectType("DIGITALIZATION");
        project.setProjectManager("张三");
        when(projectMapper.insertInfoProject(any(InfoProject.class))).thenReturn(1);

        int rows = service.insertInfoProject(project);

        assertEquals(1, rows);
        ArgumentCaptor<InfoProject> captor = ArgumentCaptor.forClass(InfoProject.class);
        verify(projectMapper).insertInfoProject(captor.capture());
        InfoProject saved = captor.getValue();
        assertEquals("DRAFT", saved.getProjectStatus());
        assertEquals("PENDING", saved.getAcceptanceStatus());
        assertEquals("PENDING", saved.getSpaceInitStatus());
        assertEquals("UNARCHIVED", saved.getArchiveStatus());
        assertEquals(BigDecimal.ZERO, saved.getDocumentCompletionRate());
    }

    @Test
    public void shouldBuildProjectDetailAggregateSummary()
    {
        InfoProject project = new InfoProject();
        project.setProjectId(1L);
        project.setProjectCode("PRJ-001");
        project.setProjectName("Demo Project");
        project.setAcceptanceStatus("PENDING");
        project.setSpaceInitStatus("INITIALIZED");
        when(projectMapper.selectInfoProjectById(1L)).thenReturn(project);

        when(spaceDirectoryMapper.countByProjectId(1L)).thenReturn(5);
        when(spaceDirectoryMapper.countRequiredDirectoryByProjectId(1L)).thenReturn(2);
        when(documentMapper.countByProjectId(1L)).thenReturn(4);
        when(documentMapper.countPassedComplianceByProjectId(1L)).thenReturn(3);
        when(documentMapper.countArchivedByProjectId(1L)).thenReturn(1);
        when(permissionMapper.countByProjectId(1L)).thenReturn(6);

        InfoWorkOrder pending = new InfoWorkOrder();
        pending.setOrderStatus("PENDING");
        InfoWorkOrder completed = new InfoWorkOrder();
        completed.setOrderStatus("COMPLETED");
        when(workOrderMapper.selectInfoWorkOrderList(any(InfoWorkOrder.class))).thenReturn(Arrays.asList(pending, completed));

        Map<String, Object> detail = service.selectProjectDetail(1L);

        Map<String, Object> spaceSummary = (Map<String, Object>) detail.get("spaceSummary");
        Map<String, Object> acceptanceSummary = (Map<String, Object>) detail.get("acceptanceSummary");
        Map<String, Object> permissionSummary = (Map<String, Object>) detail.get("permissionSummary");
        assertEquals(5, spaceSummary.get("directoryTotal"));
        assertEquals(75.00, spaceSummary.get("documentCompletionRate"));
        assertEquals(2, acceptanceSummary.get("orderTotal"));
        assertEquals(1, acceptanceSummary.get("pendingOrderTotal"));
        assertEquals(1, acceptanceSummary.get("completedOrderTotal"));
        assertEquals(6, permissionSummary.get("permissionTotal"));
    }
}
