package com.ruoyi.system.service.information.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
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
        validateWorkOrderForCreate(workOrder);
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
        populateResourceSnapshot(workOrder);
        return workOrderMapper.insertInfoWorkOrder(workOrder);
    }

    @Override
    public int updateInfoWorkOrder(InfoWorkOrder workOrder)
    {
        if (workOrder.getWorkOrderId() == null)
        {
            throw new ServiceException("工单ID不能为空");
        }
        InfoWorkOrder current = workOrderMapper.selectInfoWorkOrderById(workOrder.getWorkOrderId());
        if (current == null)
        {
            throw new ServiceException("目标工单不存在");
        }
        if (!"DRAFT".equals(current.getOrderStatus()) && !"PENDING".equals(current.getOrderStatus()))
        {
            throw new ServiceException("当前工单状态不允许编辑");
        }
        validateWorkOrderForCreate(mergeForValidation(current, workOrder));
        populateResourceSnapshot(workOrder);
        return workOrderMapper.updateInfoWorkOrder(workOrder);
    }

    @Override
    public int deleteInfoWorkOrderByIds(Long[] workOrderIds)
    {
        if (workOrderIds == null || workOrderIds.length == 0)
        {
            throw new ServiceException("请选择要删除的工单");
        }
        for (Long workOrderId : workOrderIds)
        {
            InfoWorkOrder current = workOrderMapper.selectInfoWorkOrderById(workOrderId);
            if (current == null)
            {
                continue;
            }
            if (!"DRAFT".equals(current.getOrderStatus()) && !"PENDING".equals(current.getOrderStatus()))
            {
                throw new ServiceException("仅草稿或待审批工单允许删除");
            }
        }
        return workOrderMapper.deleteInfoWorkOrderByIds(workOrderIds);
    }

    @Override
    public int approveInfoWorkOrder(Long workOrderId, String approverName, InfoApproveBody body)
    {
        if (body == null || body.getApproved() == null)
        {
            throw new ServiceException("审批结果不能为空");
        }
        InfoWorkOrder current = workOrderMapper.selectInfoWorkOrderById(workOrderId);
        if (current == null)
        {
            throw new ServiceException("目标工单不存在");
        }
        if (!"PENDING".equals(current.getOrderStatus()))
        {
            throw new ServiceException("仅待审批工单允许审批");
        }
        if (!Boolean.TRUE.equals(body.getApproved()) && StringUtils.isEmpty(body.getApprovalComment()))
        {
            throw new ServiceException("驳回时必须填写审批意见");
        }
        current.setApproverName(approverName);
        current.setApprovalComment(body.getApprovalComment());
        current.setApprovedTime(new Date());
        current.setOrderStatus(Boolean.TRUE.equals(body.getApproved()) ? "PENDING_EXECUTION" : "REJECTED");
        int rows = workOrderMapper.updateInfoWorkOrder(current);
        if (rows > 0 && Boolean.TRUE.equals(body.getApproved()))
        {
            syncApprovalPendingStatus(current);
        }
        return rows;
    }

    @Override
    public int executeInfoWorkOrder(Long workOrderId, InfoExecuteBody body)
    {
        if (body == null)
        {
            throw new ServiceException("执行内容不能为空");
        }
        if (StringUtils.isEmpty(body.getExecutorName()))
        {
            throw new ServiceException("执行人不能为空");
        }
        if (StringUtils.isEmpty(body.getExecutionResult()))
        {
            throw new ServiceException("执行结果不能为空");
        }
        InfoWorkOrder current = workOrderMapper.selectInfoWorkOrderById(workOrderId);
        if (current == null)
        {
            throw new ServiceException("目标工单不存在");
        }
        if (!"PENDING_EXECUTION".equals(current.getOrderStatus()))
        {
            throw new ServiceException("仅待执行工单允许执行");
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
        if ("RESOURCE".equals(workOrder.getDomainType()) && "CHANGE".equals(workOrder.getRequestType()))
        {
            return "IN_USE";
        }
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

    private void validateWorkOrderForCreate(InfoWorkOrder workOrder)
    {
        if (StringUtils.isEmpty(workOrder.getDomainType()))
        {
            throw new ServiceException("业务域不能为空");
        }
        if ("RESOURCE".equals(workOrder.getDomainType()))
        {
            validateResourceWorkOrder(workOrder);
        }
    }

    private void validateResourceWorkOrder(InfoWorkOrder workOrder)
    {
        if (StringUtils.isEmpty(workOrder.getRequestType()))
        {
            throw new ServiceException("工单类型不能为空");
        }
        if (!"APPLY".equals(workOrder.getRequestType()) && !"CHANGE".equals(workOrder.getRequestType()) && !"RECYCLE".equals(workOrder.getRequestType()))
        {
            throw new ServiceException("资源工单类型仅支持 APPLY、CHANGE、RECYCLE");
        }
        if (StringUtils.isEmpty(workOrder.getRequestTitle()))
        {
            throw new ServiceException("工单标题不能为空");
        }
        if (StringUtils.isEmpty(workOrder.getApplicantName()))
        {
            throw new ServiceException("申请人不能为空");
        }
        if (StringUtils.isEmpty(workOrder.getRequestReason()))
        {
            throw new ServiceException("申请原因不能为空");
        }
        if ("APPLY".equals(workOrder.getRequestType()))
        {
            if (workOrder.getProjectId() == null)
            {
                throw new ServiceException("资源申请工单必须关联项目");
            }
        }
        else
        {
            if (workOrder.getSubjectId() == null)
            {
                throw new ServiceException("变更或回收工单必须关联资源");
            }
            InfoResource resource = resourceMapper.selectInfoResourceById(workOrder.getSubjectId());
            if (resource == null)
            {
                throw new ServiceException("目标资源不存在");
            }
            if ("RECYCLED".equals(resource.getResourceStatus()) && "CHANGE".equals(workOrder.getRequestType()))
            {
                throw new ServiceException("已回收资源不允许发起变更");
            }
            if ("RECYCLE".equals(workOrder.getRequestType()) && "RECYCLED".equals(resource.getResourceStatus()))
            {
                throw new ServiceException("已回收资源不允许重复回收");
            }
        }
    }

    private void populateResourceSnapshot(InfoWorkOrder workOrder)
    {
        if (!"RESOURCE".equals(workOrder.getDomainType()) || workOrder.getSubjectId() == null)
        {
            return;
        }
        InfoResource resource = resourceMapper.selectInfoResourceById(workOrder.getSubjectId());
        if (resource == null)
        {
            return;
        }
        if (StringUtils.isEmpty(workOrder.getSubjectName()))
        {
            workOrder.setSubjectName(resource.getResourceName());
        }
        if (StringUtils.isEmpty(workOrder.getCurrentSnapshotJson()))
        {
            workOrder.setCurrentSnapshotJson(buildResourceSnapshot(resource));
        }
    }

    private void syncApprovalPendingStatus(InfoWorkOrder workOrder)
    {
        if (!"RESOURCE".equals(workOrder.getDomainType()) || workOrder.getSubjectId() == null)
        {
            return;
        }
        InfoResource resource = resourceMapper.selectInfoResourceById(workOrder.getSubjectId());
        if (resource == null)
        {
            return;
        }
        if ("CHANGE".equals(workOrder.getRequestType()))
        {
            resource.setResourceStatus("CHANGING");
        }
        else if ("RECYCLE".equals(workOrder.getRequestType()))
        {
            resource.setResourceStatus("PENDING_RECYCLE");
        }
        else if ("APPLY".equals(workOrder.getRequestType()))
        {
            resource.setResourceStatus("PENDING_DELIVERY");
        }
        resourceMapper.updateInfoResource(resource);
    }

    private InfoWorkOrder mergeForValidation(InfoWorkOrder current, InfoWorkOrder updates)
    {
        InfoWorkOrder merged = new InfoWorkOrder();
        merged.setWorkOrderId(current.getWorkOrderId());
        merged.setDomainType(StringUtils.isEmpty(updates.getDomainType()) ? current.getDomainType() : updates.getDomainType());
        merged.setRequestType(StringUtils.isEmpty(updates.getRequestType()) ? current.getRequestType() : updates.getRequestType());
        merged.setProjectId(updates.getProjectId() == null ? current.getProjectId() : updates.getProjectId());
        merged.setSubjectId(updates.getSubjectId() == null ? current.getSubjectId() : updates.getSubjectId());
        merged.setRequestTitle(StringUtils.isEmpty(updates.getRequestTitle()) ? current.getRequestTitle() : updates.getRequestTitle());
        merged.setApplicantName(StringUtils.isEmpty(updates.getApplicantName()) ? current.getApplicantName() : updates.getApplicantName());
        merged.setRequestReason(StringUtils.isEmpty(updates.getRequestReason()) ? current.getRequestReason() : updates.getRequestReason());
        return merged;
    }

    private String buildResourceSnapshot(InfoResource resource)
    {
        return String.format(
            "{\"resourceId\":%d,\"resourceCode\":\"%s\",\"resourceName\":\"%s\",\"resourceType\":\"%s\",\"resourceStatus\":\"%s\",\"projectId\":%s,\"projectName\":\"%s\",\"ownerName\":\"%s\",\"maintainerName\":\"%s\",\"ipAddress\":\"%s\"}",
            resource.getResourceId(),
            safe(resource.getResourceCode()),
            safe(resource.getResourceName()),
            safe(resource.getResourceType()),
            safe(resource.getResourceStatus()),
            resource.getProjectId() == null ? "null" : resource.getProjectId().toString(),
            safe(resource.getProjectName()),
            safe(resource.getOwnerName()),
            safe(resource.getMaintainerName()),
            safe(resource.getIpAddress()));
    }

    private String safe(String value)
    {
        if (value == null)
        {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String generateWorkOrderNo()
    {
        return "WO" + System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(100, 999);
    }
}
