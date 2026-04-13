package com.ruoyi.system.service.information.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.information.InfoApproveBody;
import com.ruoyi.system.domain.information.InfoDataAsset;
import com.ruoyi.system.domain.information.InfoExecuteBody;
import com.ruoyi.system.domain.information.InfoNetworkResource;
import com.ruoyi.system.domain.information.InfoProject;
import com.ruoyi.system.domain.information.InfoResource;
import com.ruoyi.system.domain.information.InfoWorkOrder;
import com.ruoyi.system.mapper.information.InfoDataAssetMapper;
import com.ruoyi.system.mapper.information.InfoNetworkResourceMapper;
import com.ruoyi.system.mapper.information.InfoProjectMapper;
import com.ruoyi.system.mapper.information.InfoResourceMapper;
import com.ruoyi.system.mapper.information.InfoWorkOrderMapper;
import com.ruoyi.system.service.information.IInfoWorkOrderService;

@Service
public class InfoWorkOrderServiceImpl implements IInfoWorkOrderService
{
    @Autowired
    private InfoWorkOrderMapper workOrderMapper;

    @Autowired
    private InfoProjectMapper projectMapper;

    @Autowired
    private InfoResourceMapper resourceMapper;

    @Autowired
    private InfoDataAssetMapper dataAssetMapper;

    @Autowired
    private InfoNetworkResourceMapper networkResourceMapper;

    @Override
    public InfoWorkOrder selectInfoWorkOrderById(Long workOrderId)
    {
        return workOrderMapper.selectInfoWorkOrderById(workOrderId);
    }

    @Override
    public List<InfoWorkOrder> selectInfoWorkOrderList(InfoWorkOrder workOrder)
    {
        return workOrderMapper.selectInfoWorkOrderList(workOrder);
    }

    @Override
    public List<InfoWorkOrder> selectRecentInfoWorkOrders(int limit)
    {
        return workOrderMapper.selectRecentInfoWorkOrders(limit);
    }

    @Override
    public int insertInfoWorkOrder(InfoWorkOrder workOrder)
    {
        if (workOrder.getWorkOrderNo() == null || workOrder.getWorkOrderNo().isEmpty())
        {
            workOrder.setWorkOrderNo(generateWorkOrderNo());
        }
        if (workOrder.getOrderStatus() == null || workOrder.getOrderStatus().isEmpty())
        {
            workOrder.setOrderStatus("PENDING");
        }
        if (workOrder.getSubmittedTime() == null)
        {
            workOrder.setSubmittedTime(new Date());
        }
        return workOrderMapper.insertInfoWorkOrder(workOrder);
    }

    @Override
    public int updateInfoWorkOrder(InfoWorkOrder workOrder)
    {
        return workOrderMapper.updateInfoWorkOrder(workOrder);
    }

    @Override
    public int deleteInfoWorkOrderByIds(Long[] workOrderIds)
    {
        return workOrderMapper.deleteInfoWorkOrderByIds(workOrderIds);
    }

    @Override
    public int approveInfoWorkOrder(Long workOrderId, String approverName, InfoApproveBody body)
    {
        InfoWorkOrder current = workOrderMapper.selectInfoWorkOrderById(workOrderId);
        if (current == null || !"PENDING".equals(current.getOrderStatus()))
        {
            return 0;
        }
        current.setApproverName(approverName);
        current.setApprovalComment(body.getApprovalComment());
        current.setApprovedTime(new Date());
        current.setOrderStatus(Boolean.TRUE.equals(body.getApproved()) ? "PENDING_EXECUTION" : "REJECTED");
        return workOrderMapper.updateInfoWorkOrder(current);
    }

    @Override
    public int executeInfoWorkOrder(Long workOrderId, InfoExecuteBody body)
    {
        InfoWorkOrder current = workOrderMapper.selectInfoWorkOrderById(workOrderId);
        if (current == null || !"PENDING_EXECUTION".equals(current.getOrderStatus()))
        {
            return 0;
        }
        current.setExecutorName(body.getExecutorName());
        current.setExecutionResult(body.getExecutionResult());
        current.setExecutedTime(new Date());
        current.setOrderStatus("COMPLETED");
        int rows = workOrderMapper.updateInfoWorkOrder(current);
        if (rows > 0)
        {
            syncSubjectStatus(current, body.getTargetStatus());
        }
        return rows;
    }

    private void syncSubjectStatus(InfoWorkOrder workOrder, String targetStatus)
    {
        String status = (targetStatus == null || targetStatus.isEmpty()) ? defaultStatus(workOrder) : targetStatus;
        if ("PROJECT".equals(workOrder.getDomainType()) && workOrder.getProjectId() != null)
        {
            InfoProject project = projectMapper.selectInfoProjectById(workOrder.getProjectId());
            if (project != null)
            {
                project.setAcceptanceStatus(status);
                if ("ACCEPTED".equals(status) || "COMPLETED".equals(status))
                {
                    project.setProjectStatus("COMPLETED");
                }
                projectMapper.updateInfoProject(project);
            }
        }
        else if ("RESOURCE".equals(workOrder.getDomainType()) && workOrder.getSubjectId() != null)
        {
            InfoResource resource = resourceMapper.selectInfoResourceById(workOrder.getSubjectId());
            if (resource != null)
            {
                resource.setResourceStatus(status);
                resourceMapper.updateInfoResource(resource);
            }
        }
        else if ("DATA_ASSET".equals(workOrder.getDomainType()) && workOrder.getSubjectId() != null)
        {
            InfoDataAsset asset = dataAssetMapper.selectInfoDataAssetById(workOrder.getSubjectId());
            if (asset != null)
            {
                asset.setAssetStatus(status);
                dataAssetMapper.updateInfoDataAsset(asset);
            }
        }
        else if ("NETWORK".equals(workOrder.getDomainType()) && workOrder.getSubjectId() != null)
        {
            InfoNetworkResource network = networkResourceMapper.selectInfoNetworkResourceById(workOrder.getSubjectId());
            if (network != null)
            {
                network.setResourceStatus(status);
                networkResourceMapper.updateInfoNetworkResource(network);
            }
        }
    }

    private String defaultStatus(InfoWorkOrder workOrder)
    {
        if ("RECYCLE".equals(workOrder.getRequestType()))
        {
            return "RECYCLED";
        }
        if ("ACCEPTANCE".equals(workOrder.getRequestType()))
        {
            return "ACCEPTED";
        }
        return "IN_USE";
    }

    private String generateWorkOrderNo()
    {
        return "WO" + System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(100, 999);
    }
}
