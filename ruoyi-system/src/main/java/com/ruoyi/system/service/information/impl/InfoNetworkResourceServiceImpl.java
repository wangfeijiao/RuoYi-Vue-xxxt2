package com.ruoyi.system.service.information.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.information.InfoNetworkResource;
import com.ruoyi.system.mapper.information.InfoNetworkResourceMapper;
import com.ruoyi.system.service.information.IInfoNetworkResourceService;

@Service
public class InfoNetworkResourceServiceImpl implements IInfoNetworkResourceService
{
    @Autowired
    private InfoNetworkResourceMapper networkResourceMapper;

    @Override
    public InfoNetworkResource selectInfoNetworkResourceById(Long networkId)
    {
        return networkResourceMapper.selectInfoNetworkResourceById(networkId);
    }

    @Override
    public List<InfoNetworkResource> selectInfoNetworkResourceList(InfoNetworkResource networkResource)
    {
        return networkResourceMapper.selectInfoNetworkResourceList(networkResource);
    }

    @Override
    public int insertInfoNetworkResource(InfoNetworkResource networkResource)
    {
        return networkResourceMapper.insertInfoNetworkResource(networkResource);
    }

    @Override
    public int updateInfoNetworkResource(InfoNetworkResource networkResource)
    {
        return networkResourceMapper.updateInfoNetworkResource(networkResource);
    }

    @Override
    public int deleteInfoNetworkResourceByIds(Long[] networkIds)
    {
        return networkResourceMapper.deleteInfoNetworkResourceByIds(networkIds);
    }
}
