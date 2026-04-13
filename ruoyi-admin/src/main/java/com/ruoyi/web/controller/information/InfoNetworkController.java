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
import com.ruoyi.system.domain.information.InfoApproveBody;
import com.ruoyi.system.domain.information.InfoExecuteBody;
import com.ruoyi.system.domain.information.InfoNetworkResource;
import com.ruoyi.system.domain.information.InfoWorkOrder;
import com.ruoyi.system.service.information.IInfoNetworkResourceService;
import com.ruoyi.system.service.information.IInfoWorkOrderService;

@RestController
@RequestMapping("/information/network")
public class InfoNetworkController extends BaseController
{
    @Autowired
    private IInfoNetworkResourceService networkResourceService;

    @Autowired
    private IInfoWorkOrderService workOrderService;

    @PreAuthorize("@ss.hasPermi('information:network:list')")
    @GetMapping("/list")
    public TableDataInfo list(InfoNetworkResource networkResource)
    {
        startPage();
        List<InfoNetworkResource> list = networkResourceService.selectInfoNetworkResourceList(networkResource);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('information:network:query')")
    @GetMapping("/{networkId}")
    public AjaxResult getInfo(@PathVariable Long networkId)
    {
        return success(networkResourceService.selectInfoNetworkResourceById(networkId));
    }

    @PreAuthorize("@ss.hasPermi('information:network:add')")
    @PostMapping
    public AjaxResult add(@RequestBody InfoNetworkResource networkResource)
    {
        networkResource.setCreateBy(getUsername());
        return toAjax(networkResourceService.insertInfoNetworkResource(networkResource));
    }

    @PreAuthorize("@ss.hasPermi('information:network:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody InfoNetworkResource networkResource)
    {
        networkResource.setUpdateBy(getUsername());
        return toAjax(networkResourceService.updateInfoNetworkResource(networkResource));
    }

    @PreAuthorize("@ss.hasPermi('information:network:remove')")
    @DeleteMapping("/{networkIds}")
    public AjaxResult remove(@PathVariable Long[] networkIds)
    {
        return toAjax(networkResourceService.deleteInfoNetworkResourceByIds(networkIds));
    }

    @PreAuthorize("@ss.hasPermi('information:networkOrder:list')")
    @GetMapping("/order/list")
    public TableDataInfo orderList(InfoWorkOrder workOrder)
    {
        workOrder.setDomainType("NETWORK");
        startPage();
        return getDataTable(workOrderService.selectInfoWorkOrderList(workOrder));
    }

    @PreAuthorize("@ss.hasPermi('information:networkOrder:query')")
    @GetMapping("/order/{workOrderId}")
    public AjaxResult orderInfo(@PathVariable Long workOrderId)
    {
        return success(workOrderService.selectInfoWorkOrderById(workOrderId));
    }

    @PreAuthorize("@ss.hasPermi('information:networkOrder:add')")
    @PostMapping("/order")
    public AjaxResult addOrder(@RequestBody InfoWorkOrder workOrder)
    {
        workOrder.setDomainType("NETWORK");
        workOrder.setCreateBy(getUsername());
        return toAjax(workOrderService.insertInfoWorkOrder(workOrder));
    }

    @PreAuthorize("@ss.hasPermi('information:networkOrder:edit')")
    @PutMapping("/order")
    public AjaxResult editOrder(@RequestBody InfoWorkOrder workOrder)
    {
        workOrder.setDomainType("NETWORK");
        workOrder.setUpdateBy(getUsername());
        return toAjax(workOrderService.updateInfoWorkOrder(workOrder));
    }

    @PreAuthorize("@ss.hasPermi('information:networkOrder:approve')")
    @PostMapping("/order/{workOrderId}/approve")
    public AjaxResult approve(@PathVariable Long workOrderId, @RequestBody InfoApproveBody body)
    {
        return toAjax(workOrderService.approveInfoWorkOrder(workOrderId, getUsername(), body));
    }

    @PreAuthorize("@ss.hasPermi('information:networkOrder:execute')")
    @PostMapping("/order/{workOrderId}/execute")
    public AjaxResult execute(@PathVariable Long workOrderId, @RequestBody InfoExecuteBody body)
    {
        return toAjax(workOrderService.executeInfoWorkOrder(workOrderId, body));
    }
}
