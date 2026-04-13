package com.ruoyi.system.domain.information;

import java.util.List;

public class InfoDashboardOverview
{
    private Long projectTotal;
    private Long projectPendingAcceptance;
    private Long resourceTotal;
    private Long resourceInUse;
    private Long applicationTotal;
    private Long applicationAlertCount;
    private Long dataAssetTotal;
    private Long pendingDataOrders;
    private Long networkTotal;
    private Long pendingExecutionOrders;
    private Long todoOrders;
    private List<InfoWorkOrder> recentOrders;

    public Long getProjectTotal() { return projectTotal; }
    public void setProjectTotal(Long projectTotal) { this.projectTotal = projectTotal; }
    public Long getProjectPendingAcceptance() { return projectPendingAcceptance; }
    public void setProjectPendingAcceptance(Long projectPendingAcceptance) { this.projectPendingAcceptance = projectPendingAcceptance; }
    public Long getResourceTotal() { return resourceTotal; }
    public void setResourceTotal(Long resourceTotal) { this.resourceTotal = resourceTotal; }
    public Long getResourceInUse() { return resourceInUse; }
    public void setResourceInUse(Long resourceInUse) { this.resourceInUse = resourceInUse; }
    public Long getApplicationTotal() { return applicationTotal; }
    public void setApplicationTotal(Long applicationTotal) { this.applicationTotal = applicationTotal; }
    public Long getApplicationAlertCount() { return applicationAlertCount; }
    public void setApplicationAlertCount(Long applicationAlertCount) { this.applicationAlertCount = applicationAlertCount; }
    public Long getDataAssetTotal() { return dataAssetTotal; }
    public void setDataAssetTotal(Long dataAssetTotal) { this.dataAssetTotal = dataAssetTotal; }
    public Long getPendingDataOrders() { return pendingDataOrders; }
    public void setPendingDataOrders(Long pendingDataOrders) { this.pendingDataOrders = pendingDataOrders; }
    public Long getNetworkTotal() { return networkTotal; }
    public void setNetworkTotal(Long networkTotal) { this.networkTotal = networkTotal; }
    public Long getPendingExecutionOrders() { return pendingExecutionOrders; }
    public void setPendingExecutionOrders(Long pendingExecutionOrders) { this.pendingExecutionOrders = pendingExecutionOrders; }
    public Long getTodoOrders() { return todoOrders; }
    public void setTodoOrders(Long todoOrders) { this.todoOrders = todoOrders; }
    public List<InfoWorkOrder> getRecentOrders() { return recentOrders; }
    public void setRecentOrders(List<InfoWorkOrder> recentOrders) { this.recentOrders = recentOrders; }
}
