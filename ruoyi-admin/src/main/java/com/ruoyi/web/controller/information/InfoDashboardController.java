package com.ruoyi.web.controller.information;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.information.IInfoDashboardService;

@RestController
@RequestMapping("/information/dashboard")
public class InfoDashboardController extends BaseController
{
    @Autowired
    private IInfoDashboardService dashboardService;

    @PreAuthorize("@ss.hasPermi('information:dashboard:list')")
    @GetMapping("/overview")
    public AjaxResult overview()
    {
        return success(dashboardService.selectOverview());
    }
}
