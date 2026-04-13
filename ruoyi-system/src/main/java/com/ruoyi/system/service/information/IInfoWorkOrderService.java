package com.ruoyi.system.service.information;

import java.util.List;
import com.ruoyi.system.domain.information.InfoApproveBody;
import com.ruoyi.system.domain.information.InfoExecuteBody;
import com.ruoyi.system.domain.information.InfoWorkOrder;

public interface IInfoWorkOrderService
{
    InfoWorkOrder selectInfoWorkOrderById(Long workOrderId);

    List<InfoWorkOrder> selectInfoWorkOrderList(InfoWorkOrder workOrder);

    List<InfoWorkOrder> selectRecentInfoWorkOrders(int limit);

    int insertInfoWorkOrder(InfoWorkOrder workOrder);

    int updateInfoWorkOrder(InfoWorkOrder workOrder);

    int deleteInfoWorkOrderByIds(Long[] workOrderIds);

    int approveInfoWorkOrder(Long workOrderId, String approverName, InfoApproveBody body);

    int executeInfoWorkOrder(Long workOrderId, InfoExecuteBody body);
}
