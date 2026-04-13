package com.ruoyi.system.mapper.information;

import java.util.List;
import com.ruoyi.system.domain.information.InfoApplication;

public interface InfoApplicationMapper
{
    InfoApplication selectInfoApplicationById(Long applicationId);

    List<InfoApplication> selectInfoApplicationList(InfoApplication application);

    int insertInfoApplication(InfoApplication application);

    int updateInfoApplication(InfoApplication application);

    int deleteInfoApplicationById(Long applicationId);

    int deleteInfoApplicationByIds(Long[] applicationIds);
}
