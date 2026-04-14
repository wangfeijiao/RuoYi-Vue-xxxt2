package com.ruoyi.system.service.information.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.information.InfoProject;
import com.ruoyi.system.domain.information.InfoProjectPermissionRel;
import com.ruoyi.system.mapper.information.InfoProjectMapper;
import com.ruoyi.system.mapper.information.InfoProjectPermissionRelMapper;

@ExtendWith(MockitoExtension.class)
public class InfoProjectPermissionServiceImplTest
{
    @Mock
    private InfoProjectPermissionRelMapper permissionMapper;

    @Mock
    private InfoProjectMapper projectMapper;

    @InjectMocks
    private InfoProjectPermissionServiceImpl service;

    @Test
    public void shouldRejectDuplicateProjectPermission()
    {
        InfoProjectPermissionRel permission = buildPermission("DIRECTORY", 101L, "ROLE", "project_observer");
        InfoProject project = new InfoProject();
        project.setProjectId(1L);
        when(projectMapper.selectInfoProjectById(1L)).thenReturn(project);
        when(permissionMapper.countDuplicate(any(InfoProjectPermissionRel.class))).thenReturn(1);

        assertThrows(ServiceException.class, () -> service.insertProjectPermission(permission));
    }

    @Test
    public void shouldBuildPermissionMatrixByScope()
    {
        InfoProject project = new InfoProject();
        project.setProjectId(1L);
        when(projectMapper.selectInfoProjectById(1L)).thenReturn(project);
        when(permissionMapper.selectInfoProjectPermissionList(any(InfoProjectPermissionRel.class))).thenReturn(Arrays.asList(
            buildPermission("PROJECT", null, "ROLE", "project_manager"),
            buildPermission("DIRECTORY", 101L, "ROLE", "project_observer"),
            buildPermission("DOCUMENT", 201L, "USER", "u1001")));

        Map<String, Object> matrix = service.selectProjectPermissionMatrix(1L);

        assertEquals(1, ((List<?>) matrix.get("projectPermissions")).size());
        assertEquals(1, ((List<?>) matrix.get("directoryPermissions")).size());
        assertEquals(1, ((List<?>) matrix.get("documentPermissions")).size());
        assertEquals(4, ((List<?>) matrix.get("defaultRoleSuggestions")).size());
    }

    private InfoProjectPermissionRel buildPermission(String scopeType, Long scopeId, String targetType, String targetKey)
    {
        InfoProjectPermissionRel permission = new InfoProjectPermissionRel();
        permission.setProjectId(1L);
        permission.setScopeType(scopeType);
        permission.setScopeId(scopeId);
        permission.setTargetType(targetType);
        permission.setTargetKey(targetKey);
        permission.setCanView("1");
        permission.setCanEdit("0");
        permission.setCanDownload("1");
        permission.setCanDelete("0");
        permission.setInheritFlag("0");
        return permission;
    }
}
