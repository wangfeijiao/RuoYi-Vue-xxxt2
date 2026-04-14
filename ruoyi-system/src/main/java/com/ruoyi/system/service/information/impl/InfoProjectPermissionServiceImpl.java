package com.ruoyi.system.service.information.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.information.InfoProject;
import com.ruoyi.system.domain.information.InfoProjectPermissionRel;
import com.ruoyi.system.mapper.information.InfoProjectMapper;
import com.ruoyi.system.mapper.information.InfoProjectPermissionRelMapper;
import com.ruoyi.system.service.information.IInfoProjectPermissionService;

@Service
public class InfoProjectPermissionServiceImpl implements IInfoProjectPermissionService
{
    private static final String SCOPE_PROJECT = "PROJECT";
    private static final String SCOPE_DIRECTORY = "DIRECTORY";
    private static final String SCOPE_DOCUMENT = "DOCUMENT";

    private static final String TARGET_USER = "USER";
    private static final String TARGET_ROLE = "ROLE";
    private static final String TARGET_TEAM = "TEAM";

    @Autowired
    private InfoProjectPermissionRelMapper permissionMapper;

    @Autowired
    private InfoProjectMapper projectMapper;

    @Override
    public List<InfoProjectPermissionRel> selectInfoProjectPermissionList(InfoProjectPermissionRel permission)
    {
        ensureProjectExists(permission.getProjectId());
        return permissionMapper.selectInfoProjectPermissionList(permission);
    }

    @Override
    public Map<String, Object> selectProjectPermissionMatrix(Long projectId)
    {
        InfoProjectPermissionRel query = new InfoProjectPermissionRel();
        query.setProjectId(projectId);
        List<InfoProjectPermissionRel> permissions = selectInfoProjectPermissionList(query);

        List<InfoProjectPermissionRel> projectPermissions = new ArrayList<>();
        List<InfoProjectPermissionRel> directoryPermissions = new ArrayList<>();
        List<InfoProjectPermissionRel> documentPermissions = new ArrayList<>();
        for (InfoProjectPermissionRel permission : permissions)
        {
            if (SCOPE_PROJECT.equals(permission.getScopeType()))
            {
                projectPermissions.add(permission);
            }
            else if (SCOPE_DIRECTORY.equals(permission.getScopeType()))
            {
                directoryPermissions.add(permission);
            }
            else if (SCOPE_DOCUMENT.equals(permission.getScopeType()))
            {
                documentPermissions.add(permission);
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("projectPermissions", projectPermissions);
        result.put("directoryPermissions", directoryPermissions);
        result.put("documentPermissions", documentPermissions);
        result.put("defaultRoleSuggestions", buildDefaultRoleSuggestions());
        return result;
    }

    @Override
    public int insertProjectPermission(InfoProjectPermissionRel permission)
    {
        validatePermission(permission, true);
        applyDefaultFlags(permission);
        if (permissionMapper.countDuplicate(permission) > 0)
        {
            throw new ServiceException("项目权限已存在，不能重复授权");
        }
        return permissionMapper.insertInfoProjectPermission(permission);
    }

    @Override
    public int updateProjectPermission(InfoProjectPermissionRel permission)
    {
        if (permission.getPermissionId() == null)
        {
            throw new ServiceException("权限ID不能为空");
        }
        InfoProjectPermissionRel current = permissionMapper.selectInfoProjectPermissionById(permission.getPermissionId());
        if (current == null)
        {
            throw new ServiceException("目标权限不存在");
        }
        if (!Objects.equals(current.getProjectId(), permission.getProjectId()))
        {
            throw new ServiceException("项目权限归属不匹配");
        }

        current.setCanView(defaultFlag(permission.getCanView(), current.getCanView()));
        current.setCanEdit(defaultFlag(permission.getCanEdit(), current.getCanEdit()));
        current.setCanDownload(defaultFlag(permission.getCanDownload(), current.getCanDownload()));
        current.setCanDelete(defaultFlag(permission.getCanDelete(), current.getCanDelete()));
        current.setInheritFlag(defaultFlag(permission.getInheritFlag(), current.getInheritFlag()));
        current.setStartTime(permission.getStartTime() == null ? current.getStartTime() : permission.getStartTime());
        current.setEndTime(permission.getEndTime() == null ? current.getEndTime() : permission.getEndTime());
        current.setRemark(permission.getRemark() == null ? current.getRemark() : permission.getRemark());
        current.setUpdateBy(permission.getUpdateBy());
        validateDateRange(current.getStartTime(), current.getEndTime());
        return permissionMapper.updateInfoProjectPermission(current);
    }

    @Override
    public int deleteProjectPermission(Long projectId, Long permissionId)
    {
        InfoProjectPermissionRel current = permissionMapper.selectInfoProjectPermissionById(permissionId);
        if (current == null)
        {
            return 0;
        }
        if (!Objects.equals(current.getProjectId(), projectId))
        {
            throw new ServiceException("项目权限归属不匹配");
        }
        return permissionMapper.deleteInfoProjectPermissionById(permissionId);
    }

    private void validatePermission(InfoProjectPermissionRel permission, boolean create)
    {
        ensureProjectExists(permission.getProjectId());
        if (!SCOPE_PROJECT.equals(permission.getScopeType())
            && !SCOPE_DIRECTORY.equals(permission.getScopeType())
            && !SCOPE_DOCUMENT.equals(permission.getScopeType()))
        {
            throw new ServiceException("授权范围仅支持 PROJECT、DIRECTORY、DOCUMENT");
        }
        if (!TARGET_USER.equals(permission.getTargetType())
            && !TARGET_ROLE.equals(permission.getTargetType())
            && !TARGET_TEAM.equals(permission.getTargetType()))
        {
            throw new ServiceException("授权对象仅支持 USER、ROLE、TEAM");
        }
        if (StringUtils.isEmpty(permission.getTargetKey()))
        {
            throw new ServiceException("授权对象标识不能为空");
        }
        if (!SCOPE_PROJECT.equals(permission.getScopeType()) && permission.getScopeId() == null)
        {
            throw new ServiceException("目录或文档授权必须指定范围ID");
        }
        if (create && permission.getPermissionId() != null)
        {
            throw new ServiceException("新增权限不能指定权限ID");
        }
        validateDateRange(permission.getStartTime(), permission.getEndTime());
    }

    private void validateDateRange(Date startTime, Date endTime)
    {
        if (startTime != null && endTime != null && endTime.before(startTime))
        {
            throw new ServiceException("授权结束时间不能早于开始时间");
        }
    }

    private void applyDefaultFlags(InfoProjectPermissionRel permission)
    {
        permission.setCanView(defaultFlag(permission.getCanView(), "0"));
        permission.setCanEdit(defaultFlag(permission.getCanEdit(), "0"));
        permission.setCanDownload(defaultFlag(permission.getCanDownload(), "0"));
        permission.setCanDelete(defaultFlag(permission.getCanDelete(), "0"));
        permission.setInheritFlag(defaultFlag(permission.getInheritFlag(), "1"));
        if (SCOPE_PROJECT.equals(permission.getScopeType()))
        {
            permission.setScopeId(null);
        }
    }

    private String defaultFlag(String value, String fallback)
    {
        return StringUtils.isEmpty(value) ? fallback : value;
    }

    private void ensureProjectExists(Long projectId)
    {
        if (projectId == null)
        {
            throw new ServiceException("项目ID不能为空");
        }
        if (projectMapper.selectInfoProjectById(projectId) == null)
        {
            throw new ServiceException("目标项目不存在");
        }
    }

    private List<Map<String, String>> buildDefaultRoleSuggestions()
    {
        List<Map<String, String>> suggestions = new ArrayList<>();
        suggestions.add(buildRoleSuggestion("project_manager", "项目经理", "1", "1", "1", "1"));
        suggestions.add(buildRoleSuggestion("project_member", "项目成员", "1", "1", "1", "0"));
        suggestions.add(buildRoleSuggestion("project_observer", "观察员", "1", "0", "1", "0"));
        suggestions.add(buildRoleSuggestion("project_reviewer", "评审专家", "1", "0", "1", "0"));
        return suggestions;
    }

    private Map<String, String> buildRoleSuggestion(String targetKey, String label, String canView, String canEdit,
        String canDownload, String canDelete)
    {
        Map<String, String> suggestion = new LinkedHashMap<>();
        suggestion.put("targetType", TARGET_ROLE);
        suggestion.put("targetKey", targetKey);
        suggestion.put("label", label);
        suggestion.put("canView", canView);
        suggestion.put("canEdit", canEdit);
        suggestion.put("canDownload", canDownload);
        suggestion.put("canDelete", canDelete);
        return suggestion;
    }
}
