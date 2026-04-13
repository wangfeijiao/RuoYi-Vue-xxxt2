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
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.information.InfoApproveBody;
import com.ruoyi.system.domain.information.InfoExecuteBody;
import com.ruoyi.system.domain.information.InfoResource;
import com.ruoyi.system.domain.information.InfoWorkOrder;
import com.ruoyi.system.service.information.IInfoResourceService;
import com.ruoyi.system.service.information.IInfoWorkOrderService;

@RestController
@RequestMapping("/information/resource")
public class InfoResourceController extends BaseController
{
    @Autowired
    private IInfoResourceService resourceService;

    @Autowired
    private IInfoWorkOrderService workOrderService;

    @PreAuthorize("@ss.hasPermi('information:resource:list')")
    @GetMapping("/list")
    public TableDataInfo list(InfoResource resource)
    {
        startPage();
        List<InfoResource> list = resourceService.selectInfoResourceList(resource);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('information:resource:query')")
    @GetMapping("/{resourceId}")
    public AjaxResult getInfo(@PathVariable Long resourceId)
    {
        return success(resourceService.selectInfoResourceById(resourceId));
    }

    @PreAuthorize("@ss.hasPermi('information:resource:add')")
    @Log(title = "IT资源", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody InfoResource resource)
    {
        resource.setCreateBy(getUsername());
        return toAjax(resourceService.insertInfoResource(resource));
    }

    @PreAuthorize("@ss.hasPermi('information:resource:edit')")
    @Log(title = "IT资源", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody InfoResource resource)
    {
        resource.setUpdateBy(getUsername());
        return toAjax(resourceService.updateInfoResource(resource));
    }

    @PreAuthorize("@ss.hasPermi('information:resource:remove')")
    @DeleteMapping("/{resourceIds}")
    public AjaxResult remove(@PathVariable Long[] resourceIds)
    {
        return toAjax(resourceService.deleteInfoResourceByIds(resourceIds));
    }

    @PreAuthorize("@ss.hasPermi('information:resourceOrder:list')")
    @GetMapping("/order/list")
    public TableDataInfo orderList(InfoWorkOrder workOrder)
    {
        workOrder.setDomainType("RESOURCE");
        startPage();
        return getDataTable(workOrderService.selectInfoWorkOrderList(workOrder));
    }

    @PreAuthorize("@ss.hasPermi('information:resourceOrder:query')")
    @GetMapping("/order/{workOrderId}")
    public AjaxResult orderInfo(@PathVariable Long workOrderId)
    {
        return success(workOrderService.selectInfoWorkOrderById(workOrderId));
    }

    @PreAuthorize("@ss.hasPermi('information:resourceOrder:add')")
    @PostMapping("/order")
    public AjaxResult addOrder(@RequestBody InfoWorkOrder workOrder)
    {
        workOrder.setDomainType("RESOURCE");
        workOrder.setCreateBy(getUsername());
        return toAjax(workOrderService.insertInfoWorkOrder(workOrder));
    }

    @PreAuthorize("@ss.hasPermi('information:resourceOrder:edit')")
    @PutMapping("/order")
    public AjaxResult editOrder(@RequestBody InfoWorkOrder workOrder)
    {
        workOrder.setDomainType("RESOURCE");
        workOrder.setUpdateBy(getUsername());
        return toAjax(workOrderService.updateInfoWorkOrder(workOrder));
    }

    @PreAuthorize("@ss.hasPermi('information:resourceOrder:approve')")
    @PostMapping("/order/{workOrderId}/approve")
    public AjaxResult approve(@PathVariable Long workOrderId, @RequestBody InfoApproveBody body)
    {
        return toAjax(workOrderService.approveInfoWorkOrder(workOrderId, getUsername(), body));
    }

    @PreAuthorize("@ss.hasPermi('information:resourceOrder:execute')")
    @PostMapping("/order/{workOrderId}/execute")
    public AjaxResult execute(@PathVariable Long workOrderId, @RequestBody InfoExecuteBody body)
    {
        return toAjax(workOrderService.executeInfoWorkOrder(workOrderId, body));
    }

    @PreAuthorize("@ss.hasPermi('information:resourceOrder:remove')")
    @DeleteMapping("/order/{workOrderIds}")
    public AjaxResult removeOrder(@PathVariable Long[] workOrderIds)
    {
        return toAjax(workOrderService.deleteInfoWorkOrderByIds(workOrderIds));
    }
}
