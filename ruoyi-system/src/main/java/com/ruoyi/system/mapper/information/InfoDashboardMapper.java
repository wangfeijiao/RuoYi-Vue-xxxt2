package com.ruoyi.system.mapper.information;

public interface InfoDashboardMapper
{
    Long selectProjectTotal();

    Long selectProjectPendingAcceptance();

    Long selectResourceTotal();

    Long selectResourceInUse();

    Long selectApplicationTotal();

    Long selectApplicationAlertCount();

    Long selectDataAssetTotal();

    Long selectPendingDataOrders();

    Long selectNetworkTotal();

    Long selectPendingExecutionOrders();

    Long selectTodoOrders();
}
