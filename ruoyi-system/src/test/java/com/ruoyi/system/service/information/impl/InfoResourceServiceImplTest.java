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
import com.ruoyi.system.domain.information.InfoResource;
import com.ruoyi.system.mapper.information.InfoResourceMapper;
import com.ruoyi.system.mapper.information.InfoWorkOrderMapper;

@ExtendWith(MockitoExtension.class)
public class InfoResourceServiceImplTest
{
    @Mock
    private InfoResourceMapper resourceMapper;

    @Mock
    private InfoWorkOrderMapper workOrderMapper;

    @InjectMocks
    private InfoResourceServiceImpl service;

    @Test
    public void shouldApplyDefaultStatusWhenInsertResource()
    {
        InfoResource resource = new InfoResource();
        resource.setResourceCode("RES-001");
        resource.setResourceName("Test Resource");
        resource.setResourceType("SERVER");
        when(resourceMapper.insertInfoResource(any(InfoResource.class))).thenReturn(1);

        int rows = service.insertInfoResource(resource);

        assertEquals(1, rows);
        ArgumentCaptor<InfoResource> captor = ArgumentCaptor.forClass(InfoResource.class);
        verify(resourceMapper).insertInfoResource(captor.capture());
        assertEquals("IDLE", captor.getValue().getResourceStatus());
        assertEquals("UNKNOWN", captor.getValue().getMonitorStatus());
    }

    @Test
    public void shouldRejectDuplicateResourceCode()
    {
        InfoResource resource = new InfoResource();
        resource.setResourceCode("RES-001");
        resource.setResourceName("Resource");
        resource.setResourceType("SERVER");

        InfoResource existing = new InfoResource();
        existing.setResourceId(1L);
        existing.setResourceCode("RES-001");

        when(resourceMapper.selectInfoResourceByCode("RES-001")).thenReturn(existing);

        assertThrows(ServiceException.class, () -> service.insertInfoResource(resource));
    }

    @Test
    public void shouldRejectActiveIpConflict()
    {
        InfoResource resource = new InfoResource();
        resource.setResourceCode("RES-002");
        resource.setResourceName("Resource");
        resource.setResourceType("SERVER");
        resource.setIpAddress("10.0.0.8");

        InfoResource existing = new InfoResource();
        existing.setResourceId(9L);
        existing.setIpAddress("10.0.0.8");

        when(resourceMapper.selectActiveInfoResourceByIpAddress(any(InfoResource.class))).thenReturn(existing);

        assertThrows(ServiceException.class, () -> service.insertInfoResource(resource));
    }

    @Test
    public void shouldRejectDeleteWhenReferencedByWorkOrders()
    {
        when(workOrderMapper.countResourceWorkOrdersBySubjectIds(any(Long[].class))).thenReturn(1);

        assertThrows(ServiceException.class, () -> service.deleteInfoResourceByIds(new Long[] { 1L }));
    }
}
