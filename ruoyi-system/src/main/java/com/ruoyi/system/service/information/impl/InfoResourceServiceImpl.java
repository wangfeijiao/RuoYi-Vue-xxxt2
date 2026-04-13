package com.ruoyi.system.service.information.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.information.InfoResource;
import com.ruoyi.system.mapper.information.InfoResourceMapper;
import com.ruoyi.system.service.information.IInfoResourceService;

@Service
public class InfoResourceServiceImpl implements IInfoResourceService
{
    @Autowired
    private InfoResourceMapper resourceMapper;

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
        return resourceMapper.insertInfoResource(resource);
    }

    @Override
    public int updateInfoResource(InfoResource resource)
    {
        return resourceMapper.updateInfoResource(resource);
    }

    @Override
    public int deleteInfoResourceByIds(Long[] resourceIds)
    {
        return resourceMapper.deleteInfoResourceByIds(resourceIds);
    }
}
