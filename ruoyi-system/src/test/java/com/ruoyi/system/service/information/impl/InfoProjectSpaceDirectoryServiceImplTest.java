package com.ruoyi.system.service.information.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ruoyi.system.domain.information.InfoProject;
import com.ruoyi.system.domain.information.InfoProjectSpaceDirectory;
import com.ruoyi.system.mapper.information.InfoProjectMapper;
import com.ruoyi.system.mapper.information.InfoProjectSpaceDirectoryMapper;
import com.ruoyi.system.mapper.information.InfoProjectTemplateMapper;

@ExtendWith(MockitoExtension.class)
public class InfoProjectSpaceDirectoryServiceImplTest
{
    @Mock
    private InfoProjectMapper projectMapper;

    @Mock
    private InfoProjectTemplateMapper projectTemplateMapper;

    @Mock
    private InfoProjectSpaceDirectoryMapper spaceDirectoryMapper;

    @InjectMocks
    private InfoProjectSpaceDirectoryServiceImpl service;

    @Test
    public void shouldInitializeProjectSpaceFromTemplateSnapshot()
    {
        InfoProject project = new InfoProject();
        project.setProjectId(1L);
        project.setTemplateId(11L);
        project.setDirectoryTemplateJson(
            "[{\"directoryCode\":\"REQ\",\"directoryName\":\"Requirements\",\"requiredFlag\":\"1\",\"children\":[{\"directoryCode\":\"DES\",\"directoryName\":\"Design\"}]}]");

        when(projectMapper.selectInfoProjectById(1L)).thenReturn(project);
        when(spaceDirectoryMapper.countByProjectId(1L)).thenReturn(0);
        AtomicLong idGenerator = new AtomicLong(100L);
        when(spaceDirectoryMapper.insertInfoProjectSpaceDirectory(any(InfoProjectSpaceDirectory.class)))
            .thenAnswer(invocation -> {
                InfoProjectSpaceDirectory directory = invocation.getArgument(0);
                directory.setDirectoryId(idGenerator.getAndIncrement());
                return 1;
            });
        when(projectMapper.updateInfoProject(any(InfoProject.class))).thenReturn(1);

        int inserted = service.initProjectSpace(1L, "tester");

        assertEquals(2, inserted);
        ArgumentCaptor<InfoProject> projectCaptor = ArgumentCaptor.forClass(InfoProject.class);
        verify(projectMapper).updateInfoProject(projectCaptor.capture());
        assertEquals("INITIALIZED", projectCaptor.getValue().getSpaceInitStatus());
    }

    @Test
    public void shouldAddCustomDirectoryAndBuildPath()
    {
        InfoProject project = new InfoProject();
        project.setProjectId(1L);
        when(projectMapper.selectInfoProjectById(1L)).thenReturn(project);
        when(spaceDirectoryMapper.countDirectoryCode(1L, "CUSTOM-001")).thenReturn(0);

        InfoProjectSpaceDirectory parent = new InfoProjectSpaceDirectory();
        parent.setDirectoryId(10L);
        parent.setProjectId(1L);
        parent.setDirectoryLevel(1);
        parent.setFullPath("/Root");
        when(spaceDirectoryMapper.selectInfoProjectSpaceDirectoryById(10L)).thenReturn(parent);
        when(spaceDirectoryMapper.insertInfoProjectSpaceDirectory(any(InfoProjectSpaceDirectory.class))).thenReturn(1);

        InfoProjectSpaceDirectory custom = new InfoProjectSpaceDirectory();
        custom.setParentId(10L);
        custom.setDirectoryCode("CUSTOM-001");
        custom.setDirectoryName("ChangeRequest");
        custom.setCreateBy("tester");

        int rows = service.insertCustomDirectory(1L, custom);

        assertEquals(1, rows);
        ArgumentCaptor<InfoProjectSpaceDirectory> directoryCaptor = ArgumentCaptor.forClass(InfoProjectSpaceDirectory.class);
        verify(spaceDirectoryMapper).insertInfoProjectSpaceDirectory(directoryCaptor.capture());
        InfoProjectSpaceDirectory saved = directoryCaptor.getValue();
        assertEquals("CUSTOM", saved.getDirectoryType());
        assertEquals(2, saved.getDirectoryLevel().intValue());
        assertEquals("/Root/ChangeRequest", saved.getFullPath());
        assertEquals("PENDING_CHECK", saved.getComplianceStatus());
        assertEquals("UNARCHIVED", saved.getArchiveStatus());
    }
}
