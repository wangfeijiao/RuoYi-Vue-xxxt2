package com.ruoyi.system.service.information.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.system.domain.information.InfoDataAsset;
import com.ruoyi.system.domain.information.InfoDataAssetVersion;
import com.ruoyi.system.mapper.information.InfoDataAssetMapper;
import com.ruoyi.system.mapper.information.InfoDataAssetVersionMapper;
import com.ruoyi.system.service.information.IInfoDataAssetService;

@Service
public class InfoDataAssetServiceImpl implements IInfoDataAssetService
{
    @Autowired
    private InfoDataAssetMapper dataAssetMapper;

    @Autowired
    private InfoDataAssetVersionMapper dataAssetVersionMapper;

    @Override
    public InfoDataAsset selectInfoDataAssetById(Long assetId)
    {
        return dataAssetMapper.selectInfoDataAssetById(assetId);
    }

    @Override
    public List<InfoDataAsset> selectInfoDataAssetList(InfoDataAsset asset)
    {
        return dataAssetMapper.selectInfoDataAssetList(asset);
    }

    @Override
    public int insertInfoDataAsset(InfoDataAsset asset)
    {
        int rows = dataAssetMapper.insertInfoDataAsset(asset);
        if (rows > 0 && asset.getAssetId() != null)
        {
            recordVersion(asset, "CREATE");
        }
        return rows;
    }

    @Override
    public int updateInfoDataAsset(InfoDataAsset asset)
    {
        int rows = dataAssetMapper.updateInfoDataAsset(asset);
        if (rows > 0 && asset.getAssetId() != null)
        {
            recordVersion(asset, "UPDATE");
        }
        return rows;
    }

    @Override
    public int deleteInfoDataAssetByIds(Long[] assetIds)
    {
        return dataAssetMapper.deleteInfoDataAssetByIds(assetIds);
    }

    @Override
    public List<InfoDataAssetVersion> selectInfoDataAssetVersionList(Long assetId)
    {
        return dataAssetVersionMapper.selectInfoDataAssetVersionList(assetId);
    }

    private void recordVersion(InfoDataAsset asset, String changeType)
    {
        InfoDataAssetVersion version = new InfoDataAssetVersion();
        version.setAssetId(asset.getAssetId());
        version.setVersionNo("V" + System.currentTimeMillis());
        version.setChangeType(changeType);
        version.setSnapshotJson(JSON.toJSONString(asset));
        version.setChangeReason(changeType + " snapshot");
        version.setChangedBy(asset.getUpdateBy() != null && !asset.getUpdateBy().isEmpty() ? asset.getUpdateBy() : asset.getCreateBy());
        version.setChangedTime(new Date());
        dataAssetVersionMapper.insertInfoDataAssetVersion(version);
    }
}
