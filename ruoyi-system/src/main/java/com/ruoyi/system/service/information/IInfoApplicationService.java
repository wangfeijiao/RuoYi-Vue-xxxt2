package com.ruoyi.system.service.information;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.information.InfoApplication;
import com.ruoyi.system.domain.information.InfoApplicationAlertEvent;
import com.ruoyi.system.domain.information.InfoApplicationAlertHandleBody;
import com.ruoyi.system.domain.information.InfoApplicationDependencyRel;
import com.ruoyi.system.domain.information.InfoApplicationNoticeLog;
import com.ruoyi.system.domain.information.InfoApplicationProjectRel;

public interface IInfoApplicationService
{
    InfoApplication selectInfoApplicationById(Long applicationId);

    List<InfoApplication> selectInfoApplicationList(InfoApplication application);

    int insertInfoApplication(InfoApplication application);

    int updateInfoApplication(InfoApplication application);

    int deleteInfoApplicationByIds(Long[] applicationIds);

    Map<String, Object> selectInfoApplicationDetail(Long applicationId);

    List<InfoApplicationProjectRel> selectInfoApplicationProjectRelList(Long applicationId);

    int insertInfoApplicationProjectRel(Long applicationId, InfoApplicationProjectRel relation, String operator);

    int deleteInfoApplicationProjectRel(Long applicationId, Long relId, String operator);

    List<InfoApplicationDependencyRel> selectInfoApplicationDependencyRelList(Long applicationId, InfoApplicationDependencyRel relation);

    int insertInfoApplicationDependencyRel(Long applicationId, InfoApplicationDependencyRel relation, String operator);

    int updateInfoApplicationDependencyRel(Long applicationId, Long dependencyId, InfoApplicationDependencyRel relation, String operator);

    int deleteInfoApplicationDependencyRel(Long applicationId, Long dependencyId, String operator);

    Map<String, Object> selectInfoApplicationStatusOverview(Long applicationId);

    int recalculateInfoApplicationStatus(Long applicationId, String operator);

    List<InfoApplicationAlertEvent> selectInfoApplicationAlertEventList(Long applicationId, InfoApplicationAlertEvent alertEvent);

    List<InfoApplicationNoticeLog> selectInfoApplicationNoticeLogList(Long applicationId, InfoApplicationNoticeLog noticeLog);

    int ackInfoApplicationAlert(Long alertId, String operator, InfoApplicationAlertHandleBody body);

    int resolveInfoApplicationAlert(Long alertId, String operator, InfoApplicationAlertHandleBody body);

    Map<String, Object> selectInfoApplicationStatisticsOverview(InfoApplication query);
}
