package com.ruoyi.system.service.information;

import java.util.List;
import com.ruoyi.system.domain.information.InfoNetworkResource;

public interface IInfoNetworkResourceService
{
    InfoNetworkResource selectInfoNetworkResourceById(Long networkId);

    List<InfoNetworkResource> selectInfoNetworkResourceList(InfoNetworkResource networkResource);

    int insertInfoNetworkResource(InfoNetworkResource networkResource);

    int updateInfoNetworkResource(InfoNetworkResource networkResource);

    int deleteInfoNetworkResourceByIds(Long[] networkIds);
}
