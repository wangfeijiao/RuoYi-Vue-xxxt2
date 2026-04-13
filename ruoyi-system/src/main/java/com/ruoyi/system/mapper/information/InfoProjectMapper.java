package com.ruoyi.system.mapper.information;

import java.util.List;
import com.ruoyi.system.domain.information.InfoProject;

public interface InfoProjectMapper
{
    InfoProject selectInfoProjectById(Long projectId);

    List<InfoProject> selectInfoProjectList(InfoProject project);

    int insertInfoProject(InfoProject project);

    int updateInfoProject(InfoProject project);

    int deleteInfoProjectById(Long projectId);

    int deleteInfoProjectByIds(Long[] projectIds);
}
