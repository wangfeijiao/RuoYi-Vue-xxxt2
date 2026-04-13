package com.ruoyi.system.mapper.information;

import java.util.List;
import com.ruoyi.system.domain.information.InfoProjectTemplate;

public interface InfoProjectTemplateMapper
{
    InfoProjectTemplate selectInfoProjectTemplateById(Long templateId);

    List<InfoProjectTemplate> selectInfoProjectTemplateList(InfoProjectTemplate template);

    int insertInfoProjectTemplate(InfoProjectTemplate template);

    int updateInfoProjectTemplate(InfoProjectTemplate template);

    int deleteInfoProjectTemplateById(Long templateId);

    int deleteInfoProjectTemplateByIds(Long[] templateIds);
}
