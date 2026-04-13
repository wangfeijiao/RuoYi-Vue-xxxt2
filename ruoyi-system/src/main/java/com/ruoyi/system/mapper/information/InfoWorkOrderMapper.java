package com.ruoyi.system.mapper.information;

import java.util.List;
import com.ruoyi.system.domain.information.InfoWorkOrder;

public interface InfoWorkOrderMapper
{
    InfoWorkOrder selectInfoWorkOrderById(Long workOrderId);

    List<InfoWorkOrder> selectInfoWorkOrderList(InfoWorkOrder workOrder);

    List<InfoWorkOrder> selectRecentInfoWorkOrders(int limit);

    int countResourceWorkOrdersBySubjectIds(Long[] subjectIds);

    int insertInfoWorkOrder(InfoWorkOrder workOrder);

    int updateInfoWorkOrder(InfoWorkOrder workOrder);

    int deleteInfoWorkOrderById(Long workOrderId);

    int deleteInfoWorkOrderByIds(Long[] workOrderIds);
}
