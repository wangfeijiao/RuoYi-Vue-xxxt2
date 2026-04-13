package com.ruoyi.system.service.information;

import java.util.List;
import com.ruoyi.system.domain.information.InfoApplication;

public interface IInfoApplicationService
{
    InfoApplication selectInfoApplicationById(Long applicationId);

    List<InfoApplication> selectInfoApplicationList(InfoApplication application);

    int insertInfoApplication(InfoApplication application);

    int updateInfoApplication(InfoApplication application);

    int deleteInfoApplicationByIds(Long[] applicationIds);
}
