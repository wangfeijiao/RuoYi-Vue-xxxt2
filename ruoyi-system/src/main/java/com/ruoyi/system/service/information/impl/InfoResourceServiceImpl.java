package com.ruoyi.system.service.information.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.information.InfoResource;
import com.ruoyi.system.mapper.information.InfoResourceMapper;
import com.ruoyi.system.mapper.information.InfoWorkOrderMapper;
import com.ruoyi.system.service.information.IInfoResourceService;

@Service
public class InfoResourceServiceImpl implements IInfoResourceService
{
    @Autowired
    private InfoResourceMapper resourceMapper;

    @Autowired
    private InfoWorkOrderMapper workOrderMapper;

    @Override
    public InfoResource selectInfoResourceById(Long resourceId)
    {
        return resourceMapper.selectInfoResourceById(resourceId);
    }

    @Override
    public List<InfoResource> selectInfoResourceList(InfoResource resource)
    {
        return resourceMapper.selectInfoResourceList(resource);
    }

    @Override
    public int insertInfoResource(InfoResource resource)
    {
        validateRequiredFields(resource);
        applyDefaults(resource);
        validateUniqueResourceCode(resource);
        validateActiveIpAddress(resource);
        return resourceMapper.insertInfoResource(resource);
    }

    @Override
    public int updateInfoResource(InfoResource resource)
    {
        if (resource.getResourceId() == null)
        {
            throw new ServiceException("资源ID不能为空");
        }
        InfoResource current = resourceMapper.selectInfoResourceById(resource.getResourceId());
        if (current == null)
        {
            throw new ServiceException("目标资源不存在");
        }
        validateRequiredFieldsForUpdate(resource, current);
        validateUniqueResourceCode(resource);
        validateActiveIpAddress(resource);
        return resourceMapper.updateInfoResource(resource);
    }

    @Override
    public int deleteInfoResourceByIds(Long[] resourceIds)
    {
        if (resourceIds == null || resourceIds.length == 0)
        {
            throw new ServiceException("请选择要删除的资源");
        }
        int linkedOrders = workOrderMapper.countResourceWorkOrdersBySubjectIds(resourceIds);
        if (linkedOrders > 0)
        {
            throw new ServiceException("资源已被工单引用，不能直接删除");
        }
        return resourceMapper.deleteInfoResourceByIds(resourceIds);
    }

    private void validateRequiredFields(InfoResource resource)
    {
        if (StringUtils.isEmpty(resource.getResourceCode()))
        {
            throw new ServiceException("资源编码不能为空");
        }
        if (StringUtils.isEmpty(resource.getResourceName()))
        {
            throw new ServiceException("资源名称不能为空");
        }
        if (StringUtils.isEmpty(resource.getResourceType()))
        {
            throw new ServiceException("资源类型不能为空");
        }
    }

    private void validateRequiredFieldsForUpdate(InfoResource resource, InfoResource current)
    {
        if (StringUtils.isEmpty(resource.getResourceCode()))
        {
            resource.setResourceCode(current.getResourceCode());
        }
        if (StringUtils.isEmpty(resource.getResourceName()))
        {
            resource.setResourceName(current.getResourceName());
        }
        if (StringUtils.isEmpty(resource.getResourceType()))
        {
            resource.setResourceType(current.getResourceType());
        }
    }

    private void applyDefaults(InfoResource resource)
    {
        if (StringUtils.isEmpty(resource.getResourceStatus()))
        {
            resource.setResourceStatus("IDLE");
        }
        if (StringUtils.isEmpty(resource.getMonitorStatus()))
        {
            resource.setMonitorStatus("UNKNOWN");
        }
    }

    private void validateUniqueResourceCode(InfoResource resource)
    {
        InfoResource existing = resourceMapper.selectInfoResourceByCode(resource.getResourceCode());
        if (existing != null && !existing.getResourceId().equals(resource.getResourceId()))
        {
            throw new ServiceException("资源编码已存在");
        }
    }

    private void validateActiveIpAddress(InfoResource resource)
    {
        if (StringUtils.isEmpty(resource.getIpAddress()))
        {
            return;
        }
        if ("RECYCLED".equals(resource.getResourceStatus()))
        {
            return;
        }
        InfoResource existing = resourceMapper.selectActiveInfoResourceByIpAddress(resource);
        if (existing != null)
        {
            throw new ServiceException("IP地址与运行中的资源冲突");
        }
    }
}
