package com.ruoyi.web.controller.information;

import java.util.List;
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
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.information.InfoApplication;
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

    @PreAuthorize("@ss.hasPermi('information:application:add')")
    @PostMapping
    public AjaxResult add(@RequestBody InfoApplication application)
    {
        application.setCreateBy(getUsername());
        return toAjax(applicationService.insertInfoApplication(application));
    }

    @PreAuthorize("@ss.hasPermi('information:application:edit')")
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
}
