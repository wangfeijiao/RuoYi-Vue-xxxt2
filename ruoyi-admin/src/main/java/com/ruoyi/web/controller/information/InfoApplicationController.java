package com.ruoyi.web.controller.information;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.information.InfoApplication;
import com.ruoyi.system.domain.information.InfoApplicationAlertEvent;
import com.ruoyi.system.domain.information.InfoApplicationAlertHandleBody;
import com.ruoyi.system.domain.information.InfoApplicationDependencyRel;
import com.ruoyi.system.domain.information.InfoApplicationNoticeLog;
import com.ruoyi.system.domain.information.InfoApplicationProjectRel;
import com.ruoyi.system.service.information.IInfoApplicationService;

@RestController
@RequestMapping("/information/application")
public class InfoApplicationController extends BaseController
{
    @Autowired
    private IInfoApplicationService applicationService;

    @PreAuthorize("@ss.hasPermi('information:application:list')")
    @GetMapping("/list")
    public TableDataInfo list(InfoApplication application)
    {
        startPage();
        List<InfoApplication> list = applicationService.selectInfoApplicationList(application);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('information:application:query')")
    @GetMapping("/{applicationId}")
    public AjaxResult getInfo(@PathVariable Long applicationId)
    {
        return success(applicationService.selectInfoApplicationById(applicationId));
    }

    @PreAuthorize("@ss.hasPermi('information:application:query')")
    @GetMapping("/{applicationId}/detail")
    public AjaxResult getDetail(@PathVariable Long applicationId)
    {
        return success(applicationService.selectInfoApplicationDetail(applicationId));
    }

    @PreAuthorize("@ss.hasPermi('information:application:add')")
    @Log(title = "应用管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody InfoApplication application)
    {
        application.setCreateBy(getUsername());
        return toAjax(applicationService.insertInfoApplication(application));
    }

    @PreAuthorize("@ss.hasPermi('information:application:edit')")
    @Log(title = "应用管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody InfoApplication application)
    {
        application.setUpdateBy(getUsername());
        return toAjax(applicationService.updateInfoApplication(application));
    }

    @PreAuthorize("@ss.hasPermi('information:application:remove')")
    @DeleteMapping("/{applicationIds}")
    public AjaxResult remove(@PathVariable Long[] applicationIds)
    {
        return toAjax(applicationService.deleteInfoApplicationByIds(applicationIds));
    }

    @PreAuthorize("@ss.hasPermi('information:application:query')")
    @GetMapping("/{applicationId}/projects")
    public AjaxResult listProjects(@PathVariable Long applicationId)
    {
        return success(applicationService.selectInfoApplicationProjectRelList(applicationId));
    }

    @PreAuthorize("@ss.hasPermi('information:application:edit')")
    @PostMapping("/{applicationId}/projects")
    public AjaxResult addProject(@PathVariable Long applicationId, @RequestBody InfoApplicationProjectRel relation)
    {
        return toAjax(applicationService.insertInfoApplicationProjectRel(applicationId, relation, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('information:application:edit')")
    @DeleteMapping("/{applicationId}/projects/{relId}")
    public AjaxResult removeProject(@PathVariable Long applicationId, @PathVariable Long relId)
    {
        return toAjax(applicationService.deleteInfoApplicationProjectRel(applicationId, relId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('information:application:query')")
    @GetMapping("/{applicationId}/dependencies")
    public AjaxResult listDependencies(@PathVariable Long applicationId, InfoApplicationDependencyRel relation)
    {
        return success(applicationService.selectInfoApplicationDependencyRelList(applicationId, relation));
    }

    @PreAuthorize("@ss.hasPermi('information:application:edit')")
    @PostMapping("/{applicationId}/dependencies")
    public AjaxResult addDependency(@PathVariable Long applicationId, @RequestBody InfoApplicationDependencyRel relation)
    {
        return toAjax(applicationService.insertInfoApplicationDependencyRel(applicationId, relation, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('information:application:edit')")
    @PutMapping("/{applicationId}/dependencies/{dependencyId}")
    public AjaxResult editDependency(@PathVariable Long applicationId, @PathVariable Long dependencyId,
        @RequestBody InfoApplicationDependencyRel relation)
    {
        return toAjax(applicationService.updateInfoApplicationDependencyRel(applicationId, dependencyId, relation, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('information:application:edit')")
    @DeleteMapping("/{applicationId}/dependencies/{dependencyId}")
    public AjaxResult removeDependency(@PathVariable Long applicationId, @PathVariable Long dependencyId)
    {
        return toAjax(applicationService.deleteInfoApplicationDependencyRel(applicationId, dependencyId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('information:application:query')")
    @GetMapping("/{applicationId}/status-overview")
    public AjaxResult getStatusOverview(@PathVariable Long applicationId)
    {
        return success(applicationService.selectInfoApplicationStatusOverview(applicationId));
    }

    @PreAuthorize("@ss.hasPermi('information:application:edit')")
    @PostMapping("/{applicationId}/status/recalculate")
    public AjaxResult recalculateStatus(@PathVariable Long applicationId)
    {
        return toAjax(applicationService.recalculateInfoApplicationStatus(applicationId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('information:application:query')")
    @GetMapping("/{applicationId}/alerts")
    public AjaxResult listAlerts(@PathVariable Long applicationId, InfoApplicationAlertEvent alertEvent)
    {
        return success(applicationService.selectInfoApplicationAlertEventList(applicationId, alertEvent));
    }

    @PreAuthorize("@ss.hasPermi('information:application:query')")
    @GetMapping("/{applicationId}/notices")
    public AjaxResult listNotices(@PathVariable Long applicationId, InfoApplicationNoticeLog noticeLog)
    {
        return success(applicationService.selectInfoApplicationNoticeLogList(applicationId, noticeLog));
    }

    @PreAuthorize("@ss.hasPermi('information:application:edit')")
    @PostMapping("/alerts/{alertId}/ack")
    public AjaxResult ackAlert(@PathVariable Long alertId, @RequestBody(required = false) InfoApplicationAlertHandleBody body)
    {
        return toAjax(applicationService.ackInfoApplicationAlert(alertId, getUsername(), body));
    }

    @PreAuthorize("@ss.hasPermi('information:application:edit')")
    @PostMapping("/alerts/{alertId}/resolve")
    public AjaxResult resolveAlert(@PathVariable Long alertId, @RequestBody(required = false) InfoApplicationAlertHandleBody body)
    {
        return toAjax(applicationService.resolveInfoApplicationAlert(alertId, getUsername(), body));
    }

    @PreAuthorize("@ss.hasPermi('information:application:query')")
    @GetMapping("/statistics/overview")
    public AjaxResult getStatisticsOverview(InfoApplication application)
    {
        Map<String, Object> overview = applicationService.selectInfoApplicationStatisticsOverview(application);
        return success(overview);
    }
}
