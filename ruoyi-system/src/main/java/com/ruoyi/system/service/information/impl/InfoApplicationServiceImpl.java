package com.ruoyi.system.service.information.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.information.InfoApplication;
import com.ruoyi.system.domain.information.InfoApplicationAlertEvent;
import com.ruoyi.system.domain.information.InfoApplicationAlertHandleBody;
import com.ruoyi.system.domain.information.InfoApplicationDependencyRel;
import com.ruoyi.system.domain.information.InfoApplicationNoticeLog;
import com.ruoyi.system.domain.information.InfoApplicationProjectRel;
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
    @Transactional(rollbackFor = Exception.class)
    public int insertInfoApplication(InfoApplication application)
    {
        validateRequiredFields(application);
        applyDefaults(application);
        validateUniqueApplicationCode(application);
        validateProjectIfPresent(application.getProjectId());
        int rows = applicationMapper.insertInfoApplication(application);
        syncPrimaryProjectRelation(application.getApplicationId(), application.getProjectId(), application.getCreateBy());
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInfoApplication(InfoApplication application)
    {
        if (application.getApplicationId() == null)
        {
            throw new ServiceException("应用ID不能为空");
        }
        InfoApplication current = requireApplication(application.getApplicationId());
        mergeRequiredFields(application, current);
        preserveProtectedFields(application, current);
        validateUniqueApplicationCode(application);
        validateProjectIfPresent(application.getProjectId());
        int rows = applicationMapper.updateInfoApplication(application);
        syncPrimaryProjectRelation(application.getApplicationId(), application.getProjectId(), application.getUpdateBy());
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInfoApplicationByIds(Long[] applicationIds)
    {
        if (applicationIds == null || applicationIds.length == 0)
        {
            throw new ServiceException("请选择要删除的应用");
        }
        return applicationMapper.deleteInfoApplicationByIds(applicationIds);
    }

    @Override
    public Map<String, Object> selectInfoApplicationDetail(Long applicationId)
    {
        InfoApplication application = requireApplication(applicationId);
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("application", application);
        detail.put("projectRelations", selectInfoApplicationProjectRelList(applicationId));
        detail.put("dependencyRelations", sanitizeDependencies(selectInfoApplicationDependencyRelList(applicationId, new InfoApplicationDependencyRel())));
        detail.put("statusOverview", selectInfoApplicationStatusOverview(applicationId));
        detail.put("alerts", sanitizeAlerts(selectInfoApplicationAlertEventList(applicationId, new InfoApplicationAlertEvent())));
        detail.put("notices", selectInfoApplicationNoticeLogList(applicationId, new InfoApplicationNoticeLog()));
        return detail;
    }

    @Override
    public List<InfoApplicationProjectRel> selectInfoApplicationProjectRelList(Long applicationId)
    {
        requireApplication(applicationId);
        return applicationMapper.selectInfoApplicationProjectRelList(applicationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInfoApplicationProjectRel(Long applicationId, InfoApplicationProjectRel relation, String operator)
    {
        requireApplication(applicationId);
        if (relation == null || relation.getProjectId() == null)
        {
            throw new ServiceException("关联项目不能为空");
        }
        if (applicationMapper.existsInfoProjectById(relation.getProjectId()) == 0)
        {
            throw new ServiceException("关联项目不存在");
        }
        if (applicationMapper.selectInfoApplicationProjectRelByProject(applicationId, relation.getProjectId()) != null)
        {
            throw new ServiceException("该项目已关联到当前应用");
        }
        relation.setApplicationId(applicationId);
        relation.setCreateBy(operator);
        if (StringUtils.isEmpty(relation.getRelationType()))
        {
            relation.setRelationType("RELATED");
        }
        if (StringUtils.isEmpty(relation.getActiveFlag()))
        {
            relation.setActiveFlag("1");
        }
        if ("PRIMARY".equals(relation.getRelationType()))
        {
            applicationMapper.clearPrimaryInfoApplicationProjectRel(applicationId, operator);
        }
        int rows = applicationMapper.insertInfoApplicationProjectRel(relation);
        if ("PRIMARY".equals(relation.getRelationType()))
        {
            applicationMapper.updateInfoApplicationProjectId(applicationId, relation.getProjectId(), operator);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInfoApplicationProjectRel(Long applicationId, Long relId, String operator)
    {
        requireApplication(applicationId);
        InfoApplicationProjectRel relation = applicationMapper.selectInfoApplicationProjectRelById(applicationId, relId);
        if (relation == null)
        {
            throw new ServiceException("应用项目关联不存在");
        }
        int rows = applicationMapper.deleteInfoApplicationProjectRel(applicationId, relId);
        if ("PRIMARY".equals(relation.getRelationType()))
        {
            List<InfoApplicationProjectRel> remainList = applicationMapper.selectInfoApplicationProjectRelList(applicationId);
            InfoApplicationProjectRel fallback = remainList.isEmpty() ? null : remainList.get(0);
            if (fallback == null)
            {
                applicationMapper.updateInfoApplicationProjectId(applicationId, null, operator);
            }
            else
            {
                applicationMapper.clearPrimaryInfoApplicationProjectRel(applicationId, operator);
                fallback.setRelationType("PRIMARY");
                fallback.setActiveFlag("1");
                fallback.setUpdateBy(operator);
                applicationMapper.updateInfoApplicationProjectRel(fallback);
                applicationMapper.updateInfoApplicationProjectId(applicationId, fallback.getProjectId(), operator);
            }
        }
        return rows;
    }

    @Override
    public List<InfoApplicationDependencyRel> selectInfoApplicationDependencyRelList(Long applicationId, InfoApplicationDependencyRel relation)
    {
        requireApplication(applicationId);
        InfoApplicationDependencyRel query = relation == null ? new InfoApplicationDependencyRel() : relation;
        query.setApplicationId(applicationId);
        return sanitizeDependencies(applicationMapper.selectInfoApplicationDependencyRelList(query));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInfoApplicationDependencyRel(Long applicationId, InfoApplicationDependencyRel relation, String operator)
    {
        requireApplication(applicationId);
        prepareDependency(applicationId, relation, false);
        relation.setApplicationId(applicationId);
        relation.setCreateBy(operator);
        int rows = applicationMapper.insertInfoApplicationDependencyRel(relation);
        recalculateInfoApplicationStatus(applicationId, operator);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInfoApplicationDependencyRel(Long applicationId, Long dependencyId, InfoApplicationDependencyRel relation, String operator)
    {
        requireApplication(applicationId);
        InfoApplicationDependencyRel current = applicationMapper.selectInfoApplicationDependencyRelById(applicationId, dependencyId);
        if (current == null)
        {
            throw new ServiceException("应用依赖不存在");
        }
        relation.setApplicationId(applicationId);
        relation.setDependencyId(dependencyId);
        mergeDependency(current, relation);
        prepareDependency(applicationId, relation, true);
        relation.setUpdateBy(operator);
        int rows = applicationMapper.updateInfoApplicationDependencyRel(relation);
        recalculateInfoApplicationStatus(applicationId, operator);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInfoApplicationDependencyRel(Long applicationId, Long dependencyId, String operator)
    {
        requireApplication(applicationId);
        InfoApplicationDependencyRel current = applicationMapper.selectInfoApplicationDependencyRelById(applicationId, dependencyId);
        if (current == null)
        {
            throw new ServiceException("应用依赖不存在");
        }
        int rows = applicationMapper.deleteInfoApplicationDependencyRel(applicationId, dependencyId);
        recalculateInfoApplicationStatus(applicationId, operator);
        return rows;
    }

    @Override
    public Map<String, Object> selectInfoApplicationStatusOverview(Long applicationId)
    {
        InfoApplication application = requireApplication(applicationId);
        List<Map<String, Object>> runtimeStatusList = applicationMapper.selectInfoApplicationRuntimeStatusList(applicationId);
        List<Map<String, Object>> linkedRuntimeStatusList = filterLinkedRuntimeStatuses(runtimeStatusList);
        Map<String, Object> overview = new LinkedHashMap<>();
        overview.put("applicationId", applicationId);
        overview.put("applicationName", application.getApplicationName());
        overview.put("applicationStatus", application.getApplicationStatus());
        overview.put("linkageStatus", application.getLinkageStatus());
        overview.put("lastAlertTime", application.getLastAlertTime());
        overview.put("projectCount", applicationMapper.countInfoApplicationProjectRel(applicationId));
        overview.put("dependencyCount", applicationMapper.countInfoApplicationDependencyRel(applicationId));
        overview.put("openAlertCount", applicationMapper.countOpenInfoApplicationAlertByAppId(applicationId));
        overview.put("latestAlert", sanitizeAlert(applicationMapper.selectLatestInfoApplicationAlertByAppId(applicationId)));
        overview.put("dependencySummary", applicationMapper.selectInfoApplicationDependencyTypeSummary(applicationId));
        overview.put("runtimeStatusList", runtimeStatusList);
        overview.put("abnormalDependencyCount", linkedRuntimeStatusList.stream().filter(this::isRuntimeAbnormal).count());
        return overview;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int recalculateInfoApplicationStatus(Long applicationId, String operator)
    {
        requireApplication(applicationId);
        List<Map<String, Object>> runtimeStatusList =
            filterLinkedRuntimeStatuses(applicationMapper.selectInfoApplicationRuntimeStatusList(applicationId));
        int openAlertCount = applicationMapper.countOpenInfoApplicationAlertByAppId(applicationId);
        InfoApplicationAlertEvent latestAlert = applicationMapper.selectLatestInfoApplicationAlertByAppId(applicationId);
        String linkageStatus = resolveLinkageStatus(runtimeStatusList, openAlertCount);
        return applicationMapper.updateInfoApplicationLinkageStatus(
            applicationId,
            linkageStatus,
            latestAlert == null ? null : latestAlert.getEventTime(),
            operator);
    }

    @Override
    public List<InfoApplicationAlertEvent> selectInfoApplicationAlertEventList(Long applicationId, InfoApplicationAlertEvent alertEvent)
    {
        requireApplication(applicationId);
        InfoApplicationAlertEvent query = alertEvent == null ? new InfoApplicationAlertEvent() : alertEvent;
        query.setApplicationId(applicationId);
        return sanitizeAlerts(applicationMapper.selectInfoApplicationAlertEventList(query));
    }

    @Override
    public List<InfoApplicationNoticeLog> selectInfoApplicationNoticeLogList(Long applicationId, InfoApplicationNoticeLog noticeLog)
    {
        requireApplication(applicationId);
        InfoApplicationNoticeLog query = noticeLog == null ? new InfoApplicationNoticeLog() : noticeLog;
        query.setApplicationId(applicationId);
        return applicationMapper.selectInfoApplicationNoticeLogList(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int ackInfoApplicationAlert(Long alertId, String operator, InfoApplicationAlertHandleBody body)
    {
        InfoApplicationAlertEvent current = requireAlert(alertId);
        if ("RESOLVED".equals(current.getEventStatus()))
        {
            throw new ServiceException("已恢复的告警不能重复确认");
        }
        InfoApplicationAlertEvent update = new InfoApplicationAlertEvent();
        update.setAlertId(alertId);
        update.setEventStatus("ACKED");
        update.setAckTime(new Date());
        update.setHandlerName(operator);
        update.setUpdateBy(operator);
        update.setRemark(body == null ? null : body.getRemark());
        int rows = applicationMapper.updateInfoApplicationAlertEvent(update);
        applicationMapper.updateInfoApplicationNoticeAckByAlertId(alertId, operator);
        recalculateInfoApplicationStatus(current.getApplicationId(), operator);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int resolveInfoApplicationAlert(Long alertId, String operator, InfoApplicationAlertHandleBody body)
    {
        InfoApplicationAlertEvent current = requireAlert(alertId);
        InfoApplicationAlertEvent update = new InfoApplicationAlertEvent();
        update.setAlertId(alertId);
        update.setEventStatus("RESOLVED");
        update.setResolvedTime(new Date());
        update.setHandlerName(operator);
        update.setUpdateBy(operator);
        update.setRemark(body == null ? null : body.getRemark());
        int rows = applicationMapper.updateInfoApplicationAlertEvent(update);
        applicationMapper.updateInfoApplicationNoticeResolvedByAlertId(alertId, operator);
        recalculateInfoApplicationStatus(current.getApplicationId(), operator);
        return rows;
    }

    @Override
    public Map<String, Object> selectInfoApplicationStatisticsOverview(InfoApplication query)
    {
        List<InfoApplication> applications = applicationMapper.selectInfoApplicationList(query == null ? new InfoApplication() : query);
        Map<String, Object> overview = new LinkedHashMap<>();
        overview.put("totalCount", applications.size());
        overview.put("runningCount", countBy(applications, item -> "RUNNING".equals(item.getApplicationStatus())));
        overview.put("buildCount", countBy(applications, item -> "BUILDING".equals(item.getApplicationStatus())));
        overview.put("alertCount", countBy(applications, item -> "1".equals(item.getHasOpenAlert()) || safeLong(item.getOpenAlertCount()) > 0));
        overview.put("normalLinkageCount", countBy(applications, item -> "NORMAL".equals(item.getLinkageStatus())));
        overview.put("riskLinkageCount", countBy(applications, item -> isRiskStatus(item.getLinkageStatus())));
        overview.put("statusDistribution", buildDistribution(applications, InfoApplication::getApplicationStatus));
        overview.put("linkageDistribution", buildDistribution(applications, InfoApplication::getLinkageStatus));
        overview.put("typeDistribution", buildDistribution(applications, InfoApplication::getApplicationType));
        overview.put("constructionSourceDistribution", buildDistribution(applications, InfoApplication::getConstructionSource));
        return overview;
    }

    private void validateRequiredFields(InfoApplication application)
    {
        if (StringUtils.isEmpty(application.getApplicationCode()))
        {
            throw new ServiceException("应用编码不能为空");
        }
        if (StringUtils.isEmpty(application.getApplicationName()))
        {
            throw new ServiceException("应用名称不能为空");
        }
    }

    private void mergeRequiredFields(InfoApplication application, InfoApplication current)
    {
        if (StringUtils.isEmpty(application.getApplicationCode()))
        {
            application.setApplicationCode(current.getApplicationCode());
        }
        if (StringUtils.isEmpty(application.getApplicationName()))
        {
            application.setApplicationName(current.getApplicationName());
        }
    }

    private void preserveProtectedFields(InfoApplication application, InfoApplication current)
    {
        application.setDeletedFlag(current.getDeletedFlag());
        application.setLinkageStatus(current.getLinkageStatus());
        application.setLastAlertTime(current.getLastAlertTime());
    }

    private void applyDefaults(InfoApplication application)
    {
        application.setConstructionSource(StringUtils.isEmpty(application.getConstructionSource()) ? "SELF_BUILT" : application.getConstructionSource());
        application.setApplicationStatus(StringUtils.isEmpty(application.getApplicationStatus()) ? "BUILDING" : application.getApplicationStatus());
        application.setLinkageStatus("UNKNOWN");
        application.setDeletedFlag("0");
        application.setLastAlertTime(null);
    }

    private void validateUniqueApplicationCode(InfoApplication application)
    {
        if (applicationMapper.countInfoApplicationCode(application.getApplicationCode(), application.getApplicationId()) > 0)
        {
            throw new ServiceException("应用编码已存在");
        }
    }

    private void validateProjectIfPresent(Long projectId)
    {
        if (projectId != null && applicationMapper.existsInfoProjectById(projectId) == 0)
        {
            throw new ServiceException("关联项目不存在");
        }
    }

    private InfoApplication requireApplication(Long applicationId)
    {
        InfoApplication application = applicationMapper.selectInfoApplicationById(applicationId);
        if (application == null)
        {
            throw new ServiceException("目标应用不存在");
        }
        return application;
    }

    private InfoApplicationAlertEvent requireAlert(Long alertId)
    {
        InfoApplicationAlertEvent alertEvent = applicationMapper.selectInfoApplicationAlertEventById(alertId);
        if (alertEvent == null)
        {
            throw new ServiceException("告警事件不存在");
        }
        return alertEvent;
    }

    private void syncPrimaryProjectRelation(Long applicationId, Long projectId, String operator)
    {
        if (projectId == null)
        {
            applicationMapper.clearPrimaryInfoApplicationProjectRel(applicationId, operator);
            applicationMapper.updateInfoApplicationProjectId(applicationId, null, operator);
            return;
        }
        InfoApplicationProjectRel existing = applicationMapper.selectInfoApplicationProjectRelByProject(applicationId, projectId);
        applicationMapper.clearPrimaryInfoApplicationProjectRel(applicationId, operator);
        if (existing == null)
        {
            InfoApplicationProjectRel relation = new InfoApplicationProjectRel();
            relation.setApplicationId(applicationId);
            relation.setProjectId(projectId);
            relation.setRelationType("PRIMARY");
            relation.setActiveFlag("1");
            relation.setCreateBy(operator);
            applicationMapper.insertInfoApplicationProjectRel(relation);
        }
        else
        {
            existing.setRelationType("PRIMARY");
            existing.setActiveFlag("1");
            existing.setUpdateBy(operator);
            applicationMapper.updateInfoApplicationProjectRel(existing);
        }
        applicationMapper.updateInfoApplicationProjectId(applicationId, projectId, operator);
    }

    private void prepareDependency(Long applicationId, InfoApplicationDependencyRel relation, boolean updating)
    {
        if (relation == null)
        {
            throw new ServiceException("依赖关系不能为空");
        }
        if (StringUtils.isEmpty(relation.getDependencyType()))
        {
            throw new ServiceException("依赖类型不能为空");
        }
        if (relation.getTargetId() == null && StringUtils.isEmpty(relation.getTargetName()))
        {
            throw new ServiceException("依赖目标不能为空");
        }
        if (StringUtils.isEmpty(relation.getDependencyDirection()))
        {
            relation.setDependencyDirection("UPSTREAM");
        }
        if (StringUtils.isEmpty(relation.getDependencyRole()))
        {
            relation.setDependencyRole("RUNTIME");
        }
        if (StringUtils.isEmpty(relation.getImportanceLevel()))
        {
            relation.setImportanceLevel("MEDIUM");
        }
        if (StringUtils.isEmpty(relation.getTargetSource()))
        {
            relation.setTargetSource("INTERNAL");
        }
        if (StringUtils.isEmpty(relation.getStatusLinkEnabled()))
        {
            relation.setStatusLinkEnabled("1");
        }
        if (StringUtils.isEmpty(relation.getAlertLinkEnabled()))
        {
            relation.setAlertLinkEnabled("1");
        }
        if (StringUtils.isEmpty(relation.getDependencyStatus()))
        {
            relation.setDependencyStatus("UNKNOWN");
        }
        validateDependencyTarget(applicationId, relation);
        if (applicationMapper.countInfoApplicationDependencyRelDuplicate(relation) > 0)
        {
            throw new ServiceException(updating ? "依赖关系与现有记录重复" : "依赖关系已存在");
        }
    }

    private void validateDependencyTarget(Long applicationId, InfoApplicationDependencyRel relation)
    {
        if (relation.getTargetId() == null)
        {
            return;
        }
        String dependencyType = relation.getDependencyType();
        if ("RESOURCE".equals(dependencyType) && applicationMapper.existsInfoResourceById(relation.getTargetId()) == 0)
        {
            throw new ServiceException("关联资源不存在");
        }
        if ("NETWORK".equals(dependencyType) && applicationMapper.existsInfoNetworkById(relation.getTargetId()) == 0)
        {
            throw new ServiceException("关联网络资源不存在");
        }
        if ("PROJECT".equals(dependencyType) && applicationMapper.existsInfoProjectById(relation.getTargetId()) == 0)
        {
            throw new ServiceException("关联项目不存在");
        }
        if ("APPLICATION".equals(dependencyType))
        {
            if (Objects.equals(applicationId, relation.getTargetId()))
            {
                throw new ServiceException("应用不能依赖自身");
            }
            if (applicationMapper.existsInfoApplicationById(relation.getTargetId()) == 0)
            {
                throw new ServiceException("关联应用不存在");
            }
        }
    }

    private void mergeDependency(InfoApplicationDependencyRel current, InfoApplicationDependencyRel target)
    {
        if (StringUtils.isEmpty(target.getDependencyType()))
        {
            target.setDependencyType(current.getDependencyType());
        }
        if (target.getTargetId() == null)
        {
            target.setTargetId(current.getTargetId());
        }
        if (StringUtils.isEmpty(target.getTargetCode()))
        {
            target.setTargetCode(current.getTargetCode());
        }
        if (StringUtils.isEmpty(target.getTargetName()))
        {
            target.setTargetName(current.getTargetName());
        }
        if (StringUtils.isEmpty(target.getTargetSource()))
        {
            target.setTargetSource(current.getTargetSource());
        }
        if (StringUtils.isEmpty(target.getTargetKey()))
        {
            target.setTargetKey(current.getTargetKey());
        }
        if (StringUtils.isEmpty(target.getDependencyDirection()))
        {
            target.setDependencyDirection(current.getDependencyDirection());
        }
        if (StringUtils.isEmpty(target.getDependencyRole()))
        {
            target.setDependencyRole(current.getDependencyRole());
        }
        if (StringUtils.isEmpty(target.getImportanceLevel()))
        {
            target.setImportanceLevel(current.getImportanceLevel());
        }
        if (StringUtils.isEmpty(target.getStatusLinkEnabled()))
        {
            target.setStatusLinkEnabled(current.getStatusLinkEnabled());
        }
        if (StringUtils.isEmpty(target.getAlertLinkEnabled()))
        {
            target.setAlertLinkEnabled(current.getAlertLinkEnabled());
        }
        if (StringUtils.isEmpty(target.getDependencyStatus()))
        {
            target.setDependencyStatus(current.getDependencyStatus());
        }
        if (target.getLastSyncTime() == null)
        {
            target.setLastSyncTime(current.getLastSyncTime());
        }
        if (StringUtils.isEmpty(target.getTargetSnapshotJson()))
        {
            target.setTargetSnapshotJson(current.getTargetSnapshotJson());
        }
        if (StringUtils.isEmpty(target.getRemark()))
        {
            target.setRemark(current.getRemark());
        }
    }

    private String resolveLinkageStatus(List<Map<String, Object>> runtimeStatusList, int openAlertCount)
    {
        if (openAlertCount > 0)
        {
            return "RISK";
        }
        boolean hasBlocked = runtimeStatusList.stream().anyMatch(item -> "BLOCKED".equals(String.valueOf(item.get("runtimeStatus"))));
        if (hasBlocked)
        {
            return "BLOCKED";
        }
        boolean hasRisk = runtimeStatusList.stream().anyMatch(this::isRuntimeAbnormal);
        if (hasRisk)
        {
            return "RISK";
        }
        return runtimeStatusList.isEmpty() ? "UNKNOWN" : "NORMAL";
    }

    private List<Map<String, Object>> filterLinkedRuntimeStatuses(List<Map<String, Object>> runtimeStatusList)
    {
        return runtimeStatusList.stream()
            .filter(item -> "1".equals(String.valueOf(item.get("statusLinkEnabled"))))
            .collect(Collectors.toList());
    }

    private boolean isRuntimeAbnormal(Map<String, Object> runtime)
    {
        String runtimeStatus = String.valueOf(runtime.get("runtimeStatus"));
        return isRiskStatus(runtimeStatus);
    }

    private boolean isRiskStatus(String status)
    {
        return "RISK".equals(status) || "BLOCKED".equals(status) || "WARNING".equals(status)
            || "CRITICAL".equals(status) || "ABNORMAL".equals(status);
    }

    private List<InfoApplicationDependencyRel> sanitizeDependencies(List<InfoApplicationDependencyRel> dependencies)
    {
        return dependencies.stream().map(this::sanitizeDependency).collect(Collectors.toList());
    }

    private InfoApplicationDependencyRel sanitizeDependency(InfoApplicationDependencyRel dependency)
    {
        if (dependency == null)
        {
            return null;
        }
        dependency.setTargetSnapshotJson(null);
        return dependency;
    }

    private List<InfoApplicationAlertEvent> sanitizeAlerts(List<InfoApplicationAlertEvent> alerts)
    {
        return alerts.stream().map(this::sanitizeAlert).collect(Collectors.toList());
    }

    private InfoApplicationAlertEvent sanitizeAlert(InfoApplicationAlertEvent alert)
    {
        if (alert == null)
        {
            return null;
        }
        alert.setPayloadJson(null);
        return alert;
    }

    private int countBy(List<InfoApplication> applications, Function<InfoApplication, Boolean> predicate)
    {
        return (int) applications.stream().filter(item -> Boolean.TRUE.equals(predicate.apply(item))).count();
    }

    private List<Map<String, Object>> buildDistribution(List<InfoApplication> applications, Function<InfoApplication, String> extractor)
    {
        Map<String, Long> grouped = applications.stream()
            .collect(Collectors.groupingBy(item -> defaultLabel(extractor.apply(item)), LinkedHashMap::new, Collectors.counting()));
        List<Map<String, Object>> distribution = new ArrayList<>();
        grouped.forEach((label, value) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("label", label);
            row.put("value", value);
            distribution.add(row);
        });
        return distribution;
    }

    private String defaultLabel(String value)
    {
        return StringUtils.isEmpty(value) ? "UNKNOWN" : value;
    }

    private long safeLong(Long value)
    {
        return value == null ? 0L : value;
    }
}
