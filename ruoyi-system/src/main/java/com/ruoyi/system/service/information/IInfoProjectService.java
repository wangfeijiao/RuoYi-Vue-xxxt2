package com.ruoyi.system.service.information;

import java.util.List;
import com.ruoyi.system.domain.information.InfoProject;
import com.ruoyi.system.domain.information.InfoProjectTemplate;

public interface IInfoProjectService
{
    InfoProject selectInfoProjectById(Long projectId);

    List<InfoProject> selectInfoProjectList(InfoProject project);

    int insertInfoProject(InfoProject project);

    int updateInfoProject(InfoProject project);

    int deleteInfoProjectByIds(Long[] projectIds);

    InfoProjectTemplate selectInfoProjectTemplateById(Long templateId);

    List<InfoProjectTemplate> selectInfoProjectTemplateList(InfoProjectTemplate template);

    int insertInfoProjectTemplate(InfoProjectTemplate template);

    int updateInfoProjectTemplate(InfoProjectTemplate template);

    int deleteInfoProjectTemplateByIds(Long[] templateIds);
}
