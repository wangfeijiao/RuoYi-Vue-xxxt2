package com.ruoyi.system.mapper.information;

import java.util.List;
import com.ruoyi.system.domain.information.InfoResource;

public interface InfoResourceMapper
{
    InfoResource selectInfoResourceById(Long resourceId);

    List<InfoResource> selectInfoResourceList(InfoResource resource);

    int insertInfoResource(InfoResource resource);

    int updateInfoResource(InfoResource resource);

    int deleteInfoResourceById(Long resourceId);

    int deleteInfoResourceByIds(Long[] resourceIds);
}
