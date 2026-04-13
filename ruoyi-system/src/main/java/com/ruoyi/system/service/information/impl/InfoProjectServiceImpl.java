package com.ruoyi.system.service.information.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.information.InfoProject;
import com.ruoyi.system.domain.information.InfoProjectTemplate;
import com.ruoyi.system.mapper.information.InfoProjectMapper;
import com.ruoyi.system.mapper.information.InfoProjectTemplateMapper;
import com.ruoyi.system.service.information.IInfoProjectService;

@Service
public class InfoProjectServiceImpl implements IInfoProjectService
{
    @Autowired
    private InfoProjectMapper projectMapper;

    @Autowired
    private InfoProjectTemplateMapper projectTemplateMapper;

    @Override
    public InfoProject selectInfoProjectById(Long projectId)
    {
        return projectMapper.selectInfoProjectById(projectId);
    }

    @Override
    public List<InfoProject> selectInfoProjectList(InfoProject project)
    {
        return projectMapper.selectInfoProjectList(project);
    }

    @Override
    public int insertInfoProject(InfoProject project)
    {
        return projectMapper.insertInfoProject(project);
    }

    @Override
    public int updateInfoProject(InfoProject project)
    {
        return projectMapper.updateInfoProject(project);
    }

    @Override
    public int deleteInfoProjectByIds(Long[] projectIds)
    {
        return projectMapper.deleteInfoProjectByIds(projectIds);
    }

    @Override
    public InfoProjectTemplate selectInfoProjectTemplateById(Long templateId)
    {
        return projectTemplateMapper.selectInfoProjectTemplateById(templateId);
    }

    @Override
    public List<InfoProjectTemplate> selectInfoProjectTemplateList(InfoProjectTemplate template)
    {
        return projectTemplateMapper.selectInfoProjectTemplateList(template);
    }

    @Override
    public int insertInfoProjectTemplate(InfoProjectTemplate template)
    {
        return projectTemplateMapper.insertInfoProjectTemplate(template);
    }

    @Override
    public int updateInfoProjectTemplate(InfoProjectTemplate template)
    {
        return projectTemplateMapper.updateInfoProjectTemplate(template);
    }

    @Override
    public int deleteInfoProjectTemplateByIds(Long[] templateIds)
    {
        return projectTemplateMapper.deleteInfoProjectTemplateByIds(templateIds);
    }
}
