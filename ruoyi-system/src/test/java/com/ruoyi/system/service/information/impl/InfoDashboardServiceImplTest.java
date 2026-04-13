package com.ruoyi.system.service.information.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ruoyi.system.domain.information.InfoDashboardOverview;
import com.ruoyi.system.domain.information.InfoWorkOrder;
import com.ruoyi.system.mapper.information.InfoDashboardMapper;
import com.ruoyi.system.service.information.IInfoWorkOrderService;

@ExtendWith(MockitoExtension.class)
public class InfoDashboardServiceImplTest
{
    @Mock
    private InfoDashboardMapper dashboardMapper;

    @Mock
    private IInfoWorkOrderService workOrderService;

    @InjectMocks
    private InfoDashboardServiceImpl service;

    @Test
    public void shouldReturnZeroWhenDashboardMetricsAreNull()
    {
        InfoWorkOrder workOrder = new InfoWorkOrder();
        workOrder.setWorkOrderNo("WO-1");
        List<InfoWorkOrder> recentOrders = Arrays.asList(workOrder);

        when(dashboardMapper.selectProjectTotal()).thenReturn(null);
        when(dashboardMapper.selectProjectPendingAcceptance()).thenReturn(2L);
        when(dashboardMapper.selectResourceTotal()).thenReturn(null);
        when(dashboardMapper.selectResourceInUse()).thenReturn(4L);
        when(dashboardMapper.selectApplicationTotal()).thenReturn(null);
        when(dashboardMapper.selectApplicationAlertCount()).thenReturn(1L);
        when(dashboardMapper.selectDataAssetTotal()).thenReturn(null);
        when(dashboardMapper.selectPendingDataOrders()).thenReturn(null);
        when(dashboardMapper.selectNetworkTotal()).thenReturn(6L);
        when(dashboardMapper.selectPendingExecutionOrders()).thenReturn(null);
        when(dashboardMapper.selectTodoOrders()).thenReturn(null);
        when(workOrderService.selectRecentInfoWorkOrders(8)).thenReturn(recentOrders);

        InfoDashboardOverview overview = service.selectOverview();

        assertEquals(0L, overview.getProjectTotal());
        assertEquals(2L, overview.getProjectPendingAcceptance());
        assertEquals(0L, overview.getResourceTotal());
        assertEquals(4L, overview.getResourceInUse());
        assertEquals(0L, overview.getApplicationTotal());
        assertEquals(1L, overview.getApplicationAlertCount());
        assertEquals(0L, overview.getDataAssetTotal());
        assertEquals(0L, overview.getPendingDataOrders());
        assertEquals(6L, overview.getNetworkTotal());
        assertEquals(0L, overview.getPendingExecutionOrders());
        assertEquals(0L, overview.getTodoOrders());
        assertSame(recentOrders, overview.getRecentOrders());
    }
}
