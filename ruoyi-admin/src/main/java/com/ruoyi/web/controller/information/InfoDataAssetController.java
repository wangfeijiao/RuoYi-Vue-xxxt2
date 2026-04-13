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
import com.ruoyi.system.domain.information.InfoDataAsset;
import com.ruoyi.system.domain.information.InfoExecuteBody;
import com.ruoyi.system.domain.information.InfoWorkOrder;
import com.ruoyi.system.service.information.IInfoDataAssetService;
import com.ruoyi.system.service.information.IInfoWorkOrderService;

@RestController
@RequestMapping("/information/dataAsset")
public class InfoDataAssetController extends BaseController
{
    @Autowired
    private IInfoDataAssetService dataAssetService;

    @Autowired
    private IInfoWorkOrderService workOrderService;

    @PreAuthorize("@ss.hasPermi('information:dataAsset:list')")
    @GetMapping("/list")
    public TableDataInfo list(InfoDataAsset asset)
    {
        startPage();
        List<InfoDataAsset> list = dataAssetService.selectInfoDataAssetList(asset);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('information:dataAsset:query')")
    @GetMapping("/{assetId}")
    public AjaxResult getInfo(@PathVariable Long assetId)
    {
        return success(dataAssetService.selectInfoDataAssetById(assetId));
    }

    @PreAuthorize("@ss.hasPermi('information:dataAsset:add')")
    @PostMapping
    public AjaxResult add(@RequestBody InfoDataAsset asset)
    {
        asset.setCreateBy(getUsername());
        return toAjax(dataAssetService.insertInfoDataAsset(asset));
    }

    @PreAuthorize("@ss.hasPermi('information:dataAsset:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody InfoDataAsset asset)
    {
        asset.setUpdateBy(getUsername());
        return toAjax(dataAssetService.updateInfoDataAsset(asset));
    }

    @PreAuthorize("@ss.hasPermi('information:dataAsset:remove')")
    @DeleteMapping("/{assetIds}")
    public AjaxResult remove(@PathVariable Long[] assetIds)
    {
        return toAjax(dataAssetService.deleteInfoDataAssetByIds(assetIds));
    }

    @PreAuthorize("@ss.hasPermi('information:dataAsset:query')")
    @GetMapping("/{assetId}/versions")
    public AjaxResult versions(@PathVariable Long assetId)
    {
        return success(dataAssetService.selectInfoDataAssetVersionList(assetId));
    }

    @PreAuthorize("@ss.hasPermi('information:dataOrder:list')")
    @GetMapping("/order/list")
    public TableDataInfo orderList(InfoWorkOrder workOrder)
    {
        workOrder.setDomainType("DATA_ASSET");
        startPage();
        return getDataTable(workOrderService.selectInfoWorkOrderList(workOrder));
    }

    @PreAuthorize("@ss.hasPermi('information:dataOrder:query')")
    @GetMapping("/order/{workOrderId}")
    public AjaxResult orderInfo(@PathVariable Long workOrderId)
    {
        return success(workOrderService.selectInfoWorkOrderById(workOrderId));
    }

    @PreAuthorize("@ss.hasPermi('information:dataOrder:add')")
    @PostMapping("/order")
    public AjaxResult addOrder(@RequestBody InfoWorkOrder workOrder)
    {
        workOrder.setDomainType("DATA_ASSET");
        workOrder.setCreateBy(getUsername());
        return toAjax(workOrderService.insertInfoWorkOrder(workOrder));
    }

    @PreAuthorize("@ss.hasPermi('information:dataOrder:edit')")
    @PutMapping("/order")
    public AjaxResult editOrder(@RequestBody InfoWorkOrder workOrder)
    {
        workOrder.setDomainType("DATA_ASSET");
        workOrder.setUpdateBy(getUsername());
        return toAjax(workOrderService.updateInfoWorkOrder(workOrder));
    }

    @PreAuthorize("@ss.hasPermi('information:dataOrder:approve')")
    @PostMapping("/order/{workOrderId}/approve")
    public AjaxResult approve(@PathVariable Long workOrderId, @RequestBody InfoApproveBody body)
    {
        return toAjax(workOrderService.approveInfoWorkOrder(workOrderId, getUsername(), body));
    }

    @PreAuthorize("@ss.hasPermi('information:dataOrder:execute')")
    @PostMapping("/order/{workOrderId}/execute")
    public AjaxResult execute(@PathVariable Long workOrderId, @RequestBody InfoExecuteBody body)
    {
        return toAjax(workOrderService.executeInfoWorkOrder(workOrderId, body));
    }
}
