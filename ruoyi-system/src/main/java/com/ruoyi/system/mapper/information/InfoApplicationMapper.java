package com.ruoyi.system.mapper.information;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.information.InfoApplication;
import com.ruoyi.system.domain.information.InfoApplicationAlertEvent;
import com.ruoyi.system.domain.information.InfoApplicationDependencyRel;
import com.ruoyi.system.domain.information.InfoApplicationNoticeLog;
import com.ruoyi.system.domain.information.InfoApplicationProjectRel;

public interface InfoApplicationMapper
{
    InfoApplication selectInfoApplicationById(Long applicationId);

    List<InfoApplication> selectInfoApplicationList(InfoApplication application);

    int insertInfoApplication(InfoApplication application);

    int updateInfoApplication(InfoApplication application);

    int deleteInfoApplicationById(Long applicationId);

    int deleteInfoApplicationByIds(Long[] applicationIds);

    int countInfoApplicationCode(@Param("applicationCode") String applicationCode, @Param("excludeApplicationId") Long excludeApplicationId);

    int existsInfoProjectById(Long projectId);

    int existsInfoResourceById(Long resourceId);

    int existsInfoNetworkById(Long networkId);

    int existsInfoApplicationById(Long applicationId);

    List<InfoApplicationProjectRel> selectInfoApplicationProjectRelList(Long applicationId);

    InfoApplicationProjectRel selectInfoApplicationProjectRelById(@Param("applicationId") Long applicationId, @Param("relId") Long relId);

    InfoApplicationProjectRel selectInfoApplicationProjectRelByProject(@Param("applicationId") Long applicationId, @Param("projectId") Long projectId);

    InfoApplicationProjectRel selectPrimaryInfoApplicationProjectRel(Long applicationId);

    int insertInfoApplicationProjectRel(InfoApplicationProjectRel relation);

    int updateInfoApplicationProjectRel(InfoApplicationProjectRel relation);

    int clearPrimaryInfoApplicationProjectRel(@Param("applicationId") Long applicationId, @Param("updateBy") String updateBy);

    int deleteInfoApplicationProjectRel(@Param("applicationId") Long applicationId, @Param("relId") Long relId);

    int updateInfoApplicationProjectId(@Param("applicationId") Long applicationId, @Param("projectId") Long projectId, @Param("updateBy") String updateBy);

    int countInfoApplicationProjectRel(Long applicationId);

    List<InfoApplicationDependencyRel> selectInfoApplicationDependencyRelList(InfoApplicationDependencyRel relation);

    InfoApplicationDependencyRel selectInfoApplicationDependencyRelById(@Param("applicationId") Long applicationId, @Param("dependencyId") Long dependencyId);

    int countInfoApplicationDependencyRelDuplicate(InfoApplicationDependencyRel relation);

    int insertInfoApplicationDependencyRel(InfoApplicationDependencyRel relation);

    int updateInfoApplicationDependencyRel(InfoApplicationDependencyRel relation);

    int deleteInfoApplicationDependencyRel(@Param("applicationId") Long applicationId, @Param("dependencyId") Long dependencyId);

    int countInfoApplicationDependencyRel(Long applicationId);

    List<InfoApplicationAlertEvent> selectInfoApplicationAlertEventList(InfoApplicationAlertEvent alertEvent);

    InfoApplicationAlertEvent selectInfoApplicationAlertEventById(Long alertId);

    int insertInfoApplicationAlertEvent(InfoApplicationAlertEvent alertEvent);

    int updateInfoApplicationAlertEvent(InfoApplicationAlertEvent alertEvent);

    int countOpenInfoApplicationAlert(@Param("applicationId") Long applicationId, @Param("eventCode") String eventCode);

    int countOpenInfoApplicationAlertByAppId(Long applicationId);

    InfoApplicationAlertEvent selectLatestInfoApplicationAlertByAppId(Long applicationId);

    List<InfoApplicationNoticeLog> selectInfoApplicationNoticeLogList(InfoApplicationNoticeLog noticeLog);

    int insertInfoApplicationNoticeLog(InfoApplicationNoticeLog noticeLog);

    int updateInfoApplicationNoticeAckByAlertId(@Param("alertId") Long alertId, @Param("updateBy") String updateBy);

    int updateInfoApplicationNoticeResolvedByAlertId(@Param("alertId") Long alertId, @Param("updateBy") String updateBy);

    int updateInfoApplicationLinkageStatus(@Param("applicationId") Long applicationId, @Param("linkageStatus") String linkageStatus,
        @Param("lastAlertTime") java.util.Date lastAlertTime, @Param("updateBy") String updateBy);

    List<Map<String, Object>> selectInfoApplicationDependencyTypeSummary(Long applicationId);

    List<Map<String, Object>> selectInfoApplicationRuntimeStatusList(Long applicationId);
}
