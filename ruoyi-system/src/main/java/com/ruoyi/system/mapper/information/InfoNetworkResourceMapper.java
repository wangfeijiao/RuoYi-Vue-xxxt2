package com.ruoyi.system.mapper.information;

import java.util.List;
import com.ruoyi.system.domain.information.InfoNetworkResource;

public interface InfoNetworkResourceMapper
{
    InfoNetworkResource selectInfoNetworkResourceById(Long networkId);

    List<InfoNetworkResource> selectInfoNetworkResourceList(InfoNetworkResource networkResource);

    int insertInfoNetworkResource(InfoNetworkResource networkResource);

    int updateInfoNetworkResource(InfoNetworkResource networkResource);

    int deleteInfoNetworkResourceById(Long networkId);

    int deleteInfoNetworkResourceByIds(Long[] networkIds);
}
