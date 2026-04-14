package com.ruoyi.system.service.information;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.information.InfoProjectPermissionRel;

public interface IInfoProjectPermissionService
{
    List<InfoProjectPermissionRel> selectInfoProjectPermissionList(InfoProjectPermissionRel permission);

    Map<String, Object> selectProjectPermissionMatrix(Long projectId);

    int insertProjectPermission(InfoProjectPermissionRel permission);

    int updateProjectPermission(InfoProjectPermissionRel permission);

    int deleteProjectPermission(Long projectId, Long permissionId);
}
