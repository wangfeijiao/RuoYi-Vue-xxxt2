package com.ruoyi.system.service.information;

import java.util.List;
import com.ruoyi.system.domain.information.InfoDataAsset;
import com.ruoyi.system.domain.information.InfoDataAssetVersion;

public interface IInfoDataAssetService
{
    InfoDataAsset selectInfoDataAssetById(Long assetId);

    List<InfoDataAsset> selectInfoDataAssetList(InfoDataAsset asset);

    int insertInfoDataAsset(InfoDataAsset asset);

    int updateInfoDataAsset(InfoDataAsset asset);

    int deleteInfoDataAssetByIds(Long[] assetIds);

    List<InfoDataAssetVersion> selectInfoDataAssetVersionList(Long assetId);
}
