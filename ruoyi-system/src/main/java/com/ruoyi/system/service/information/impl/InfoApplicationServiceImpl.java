package com.ruoyi.system.service.information.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.information.InfoApplication;
import com.ruoyi.system.mapper.information.InfoApplicationMapper;
import com.ruoyi.system.service.information.IInfoApplicationService;

@Service
public class InfoApplicationServiceImpl implements IInfoApplicationService
{
    @Autowired
    private InfoApplicationMapper applicationMapper;

    @Override
    public InfoApplication selectInfoApplicationById(Long applicationId)
    {
        return applicationMapper.selectInfoApplicationById(applicationId);
    }

    @Override
    public List<InfoApplication> selectInfoApplicationList(InfoApplication application)
    {
        return applicationMapper.selectInfoApplicationList(application);
    }

    @Override
    public int insertInfoApplication(InfoApplication application)
    {
        return applicationMapper.insertInfoApplication(application);
    }

    @Override
    public int updateInfoApplication(InfoApplication application)
    {
        return applicationMapper.updateInfoApplication(application);
    }

    @Override
    public int deleteInfoApplicationByIds(Long[] applicationIds)
    {
        return applicationMapper.deleteInfoApplicationByIds(applicationIds);
    }
}
