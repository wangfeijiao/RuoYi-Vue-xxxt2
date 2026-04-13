package com.ruoyi.system.mapper.information;

import java.util.List;
import com.ruoyi.system.domain.information.InfoDataAsset;

public interface InfoDataAssetMapper
{
    InfoDataAsset selectInfoDataAssetById(Long assetId);

    List<InfoDataAsset> selectInfoDataAssetList(InfoDataAsset asset);

    int insertInfoDataAsset(InfoDataAsset asset);

    int updateInfoDataAsset(InfoDataAsset asset);

    int deleteInfoDataAssetById(Long assetId);

    int deleteInfoDataAssetByIds(Long[] assetIds);
}
