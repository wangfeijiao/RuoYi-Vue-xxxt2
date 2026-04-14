package com.ruoyi.system.service.information.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.information.InfoApproveBody;
import com.ruoyi.system.domain.information.InfoExecuteBody;
import com.ruoyi.system.domain.information.InfoProject;
import com.ruoyi.system.domain.information.InfoResource;
import com.ruoyi.system.domain.information.InfoWorkOrder;
import com.ruoyi.system.mapper.information.InfoDataAssetMapper;
import com.ruoyi.system.mapper.information.InfoNetworkResourceMapper;
import com.ruoyi.system.mapper.information.InfoProjectMapper;
import com.ruoyi.system.mapper.information.InfoResourceMapper;
import com.ruoyi.system.mapper.information.InfoWorkOrderMapper;

@ExtendWith(MockitoExtension.class)
public class InfoWorkOrderServiceImplTest
{
    @Mock
    private InfoWorkOrderMapper workOrderMapper;

    @Mock
    private InfoProjectMapper projectMapper;

    @Mock
    private InfoResourceMapper resourceMapper;

    @Mock
    private InfoDataAssetMapper dataAssetMapper;

    @Mock
    private InfoNetworkResourceMapper networkResourceMapper;

    @InjectMocks
    private InfoWorkOrderServiceImpl service;

    @Test
    public void shouldApprovePendingWorkOrder()
    {
        Long workOrderId = 100L;
        InfoWorkOrder workOrder = new InfoWorkOrder();
        workOrder.setWorkOrderId(workOrderId);
        workOrder.setOrderStatus("PENDING");
        workOrder.setDomainType("RESOURCE");
        workOrder.setRequestType("CHANGE");
        workOrder.setSubjectId(5L);
        when(workOrderMapper.selectInfoWorkOrderById(workOrderId)).thenReturn(workOrder);
        when(workOrderMapper.updateInfoWorkOrder(any(InfoWorkOrder.class))).thenReturn(1);

        InfoResource resource = new InfoResource();
        resource.setResourceId(5L);
        resource.setResourceStatus("IN_USE");
        when(resourceMapper.selectInfoResourceById(5L)).thenReturn(resource);
        when(resourceMapper.updateInfoResource(any(InfoResource.class))).thenReturn(1);

        InfoApproveBody body = new InfoApproveBody();
        body.setApproved(Boolean.TRUE);
        body.setApprovalComment("approved");

        int rows = service.approveInfoWorkOrder(workOrderId, "manager", body);

        assertEquals(1, rows);
        ArgumentCaptor<InfoWorkOrder> captor = ArgumentCaptor.forClass(InfoWorkOrder.class);
        verify(workOrderMapper).updateInfoWorkOrder(captor.capture());
        InfoWorkOrder updated = captor.getValue();
        assertEquals("PENDING_EXECUTION", updated.getOrderStatus());
        assertEquals("manager", updated.getApproverName());
        assertEquals("approved", updated.getApprovalComment());

        ArgumentCaptor<InfoResource> resourceCaptor = ArgumentCaptor.forClass(InfoResource.class);
        verify(resourceMapper).updateInfoResource(resourceCaptor.capture());
        assertEquals("CHANGING", resourceCaptor.getValue().getResourceStatus());
    }

    @Test
    public void shouldExecuteWorkOrderAndSyncResourceStatus()
    {
        Long workOrderId = 101L;
        Long resourceId = 11L;

        InfoWorkOrder workOrder = new InfoWorkOrder();
        workOrder.setWorkOrderId(workOrderId);
        workOrder.setOrderStatus("PENDING_EXECUTION");
        workOrder.setDomainType("RESOURCE");
        workOrder.setSubjectId(resourceId);
        workOrder.setRequestType("APPLY");

        InfoResource resource = new InfoResource();
        resource.setResourceId(resourceId);
        resource.setResourceStatus("PENDING");

        when(workOrderMapper.selectInfoWorkOrderById(workOrderId)).thenReturn(workOrder);
        when(workOrderMapper.updateInfoWorkOrder(any(InfoWorkOrder.class))).thenReturn(1);
        when(resourceMapper.selectInfoResourceById(resourceId)).thenReturn(resource);
        when(resourceMapper.updateInfoResource(any(InfoResource.class))).thenReturn(1);

        InfoExecuteBody body = new InfoExecuteBody();
        body.setExecutorName("operator");
        body.setTargetStatus("IN_USE");
        body.setExecutionResult("done");

        int rows = service.executeInfoWorkOrder(workOrderId, body);

        assertEquals(1, rows);

        ArgumentCaptor<InfoWorkOrder> orderCaptor = ArgumentCaptor.forClass(InfoWorkOrder.class);
        verify(workOrderMapper).updateInfoWorkOrder(orderCaptor.capture());
        InfoWorkOrder updatedOrder = orderCaptor.getValue();
        assertEquals("COMPLETED", updatedOrder.getOrderStatus());
        assertEquals("operator", updatedOrder.getExecutorName());
        assertEquals("done", updatedOrder.getExecutionResult());

        ArgumentCaptor<InfoResource> resourceCaptor = ArgumentCaptor.forClass(InfoResource.class);
        verify(resourceMapper).updateInfoResource(resourceCaptor.capture());
        assertEquals(resourceId, resourceCaptor.getValue().getResourceId());
        assertEquals("IN_USE", resourceCaptor.getValue().getResourceStatus());
    }

    @Test
    public void shouldRejectResourceRecycleWithoutSubject()
    {
        InfoWorkOrder workOrder = new InfoWorkOrder();
        workOrder.setDomainType("RESOURCE");
        workOrder.setRequestType("RECYCLE");
        workOrder.setRequestTitle("Recycle");
        workOrder.setApplicantName("user1");
        workOrder.setRequestReason("unused");

        assertThrows(ServiceException.class, () -> service.insertInfoWorkOrder(workOrder));
    }

    @Test
    public void shouldRejectProjectWorkOrderWhenRequestTypeIsNotAcceptance()
    {
        InfoWorkOrder workOrder = new InfoWorkOrder();
        workOrder.setDomainType("PROJECT");
        workOrder.setRequestType("CHANGE");
        workOrder.setProjectId(10L);
        workOrder.setRequestTitle("项目结项");
        workOrder.setApplicantName("user1");
        workOrder.setRequestReason("测试");

        assertThrows(ServiceException.class, () -> service.insertInfoWorkOrder(workOrder));
    }

    @Test
    public void shouldPrepareProjectAcceptanceWorkOrderDefaultsOnCreate()
    {
        InfoWorkOrder workOrder = new InfoWorkOrder();
        workOrder.setDomainType("PROJECT");
        workOrder.setRequestType("ACCEPTANCE");
        workOrder.setProjectId(10L);
        workOrder.setRequestTitle("项目验收");
        workOrder.setApplicantName("user1");
        workOrder.setRequestReason("材料齐备");

        InfoProject project = new InfoProject();
        project.setProjectId(10L);
        project.setProjectName("智慧园区项目");
        when(projectMapper.selectInfoProjectById(10L)).thenReturn(project);
        when(workOrderMapper.insertInfoWorkOrder(any(InfoWorkOrder.class))).thenReturn(1);

        int rows = service.insertInfoWorkOrder(workOrder);

        assertEquals(1, rows);
        ArgumentCaptor<InfoWorkOrder> captor = ArgumentCaptor.forClass(InfoWorkOrder.class);
        verify(workOrderMapper).insertInfoWorkOrder(captor.capture());
        InfoWorkOrder saved = captor.getValue();
        assertEquals(Long.valueOf(10L), saved.getSubjectId());
        assertEquals("PROJECT", saved.getSubjectType());
        assertEquals("智慧园区项目", saved.getSubjectName());
        assertEquals("PENDING", saved.getOrderStatus());
    }
}
