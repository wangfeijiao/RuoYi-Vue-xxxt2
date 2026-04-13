package com.ruoyi.system.service.information.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.information.InfoDashboardOverview;
import com.ruoyi.system.mapper.information.InfoDashboardMapper;
import com.ruoyi.system.service.information.IInfoDashboardService;
import com.ruoyi.system.service.information.IInfoWorkOrderService;

@Service
public class InfoDashboardServiceImpl implements IInfoDashboardService
{
    @Autowired
    private InfoDashboardMapper dashboardMapper;

    @Autowired
    private IInfoWorkOrderService workOrderService;

    @Override
    public InfoDashboardOverview selectOverview()
    {
        InfoDashboardOverview overview = new InfoDashboardOverview();
        overview.setProjectTotal(defaultValue(dashboardMapper.selectProjectTotal()));
        overview.setProjectPendingAcceptance(defaultValue(dashboardMapper.selectProjectPendingAcceptance()));
        overview.setResourceTotal(defaultValue(dashboardMapper.selectResourceTotal()));
        overview.setResourceInUse(defaultValue(dashboardMapper.selectResourceInUse()));
        overview.setApplicationTotal(defaultValue(dashboardMapper.selectApplicationTotal()));
        overview.setApplicationAlertCount(defaultValue(dashboardMapper.selectApplicationAlertCount()));
        overview.setDataAssetTotal(defaultValue(dashboardMapper.selectDataAssetTotal()));
        overview.setPendingDataOrders(defaultValue(dashboardMapper.selectPendingDataOrders()));
        overview.setNetworkTotal(defaultValue(dashboardMapper.selectNetworkTotal()));
        overview.setPendingExecutionOrders(defaultValue(dashboardMapper.selectPendingExecutionOrders()));
        overview.setTodoOrders(defaultValue(dashboardMapper.selectTodoOrders()));
        overview.setRecentOrders(workOrderService.selectRecentInfoWorkOrders(8));
        return overview;
    }

    private Long defaultValue(Long value)
    {
        return value == null ? 0L : value;
    }
}
