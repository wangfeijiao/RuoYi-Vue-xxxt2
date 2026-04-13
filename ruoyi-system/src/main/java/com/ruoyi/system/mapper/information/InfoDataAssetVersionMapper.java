package com.ruoyi.system.mapper.information;

import java.util.List;
import com.ruoyi.system.domain.information.InfoDataAssetVersion;

public interface InfoDataAssetVersionMapper
{
    List<InfoDataAssetVersion> selectInfoDataAssetVersionList(Long assetId);

    int insertInfoDataAssetVersion(InfoDataAssetVersion version);
}
