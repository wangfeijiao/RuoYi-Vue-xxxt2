package com.ruoyi.system.mapper.information;

import java.util.List;
import com.ruoyi.system.domain.information.InfoProjectPermissionRel;

public interface InfoProjectPermissionRelMapper
{
    InfoProjectPermissionRel selectInfoProjectPermissionById(Long permissionId);

    List<InfoProjectPermissionRel> selectInfoProjectPermissionList(InfoProjectPermissionRel permission);

    int countByProjectId(Long projectId);

    int countDuplicate(InfoProjectPermissionRel permission);

    int insertInfoProjectPermission(InfoProjectPermissionRel permission);

    int updateInfoProjectPermission(InfoProjectPermissionRel permission);

    int deleteInfoProjectPermissionById(Long permissionId);
}
