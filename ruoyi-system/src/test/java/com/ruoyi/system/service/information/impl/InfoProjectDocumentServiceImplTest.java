package com.ruoyi.system.service.information.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ruoyi.system.domain.information.InfoProject;
import com.ruoyi.system.domain.information.InfoProjectDocument;
import com.ruoyi.system.domain.information.InfoProjectSpaceDirectory;
import com.ruoyi.system.mapper.information.InfoProjectDocumentMapper;
import com.ruoyi.system.mapper.information.InfoProjectMapper;
import com.ruoyi.system.mapper.information.InfoProjectSpaceDirectoryMapper;

@ExtendWith(MockitoExtension.class)
public class InfoProjectDocumentServiceImplTest
{
    @Mock
    private InfoProjectMapper projectMapper;

    @Mock
    private InfoProjectSpaceDirectoryMapper spaceDirectoryMapper;

    @Mock
    private InfoProjectDocumentMapper documentMapper;

    @InjectMocks
    private InfoProjectDocumentServiceImpl service;

    @Test
    public void shouldInsertProjectDocumentWithDefaults()
    {
        InfoProject project = new InfoProject();
        project.setProjectId(1L);
        when(projectMapper.selectInfoProjectById(1L)).thenReturn(project);

        InfoProjectSpaceDirectory directory = new InfoProjectSpaceDirectory();
        directory.setDirectoryId(101L);
        directory.setProjectId(1L);
        when(spaceDirectoryMapper.selectInfoProjectSpaceDirectoryById(101L)).thenReturn(directory);
        when(documentMapper.insertInfoProjectDocument(any(InfoProjectDocument.class))).thenReturn(1);

        InfoProjectDocument document = new InfoProjectDocument();
        document.setDirectoryId(101L);
        document.setDocumentName("AcceptanceChecklist");
        document.setDocumentType("CHECKLIST");
        document.setCreateBy("tester");

        int rows = service.insertProjectDocument(1L, document);

        assertEquals(1, rows);
        ArgumentCaptor<InfoProjectDocument> captor = ArgumentCaptor.forClass(InfoProjectDocument.class);
        verify(documentMapper).insertInfoProjectDocument(captor.capture());
        InfoProjectDocument saved = captor.getValue();
        assertEquals("V1", saved.getVersionNo());
        assertEquals("MANUAL_UPLOAD", saved.getSourceType());
        assertEquals("PENDING_CHECK", saved.getComplianceStatus());
        assertEquals("UNARCHIVED", saved.getArchiveStatus());
        assertEquals("0", saved.getDeletedFlag());
        assertEquals("tester", saved.getUploadedBy());
        assertNotNull(saved.getUploadedTime());
    }

    @Test
    public void shouldUpdateDocumentCompliance()
    {
        InfoProjectDocument current = new InfoProjectDocument();
        current.setDocumentId(1001L);
        current.setProjectId(1L);
        current.setDirectoryId(101L);
        when(documentMapper.selectInfoProjectDocumentById(1001L)).thenReturn(current);
        when(documentMapper.updateInfoProjectDocument(any(InfoProjectDocument.class))).thenReturn(1);

        int rows = service.updateDocumentCompliance(1001L, "PASSED", "checked", "reviewer");

        assertEquals(1, rows);
        ArgumentCaptor<InfoProjectDocument> captor = ArgumentCaptor.forClass(InfoProjectDocument.class);
        verify(documentMapper).updateInfoProjectDocument(captor.capture());
        assertEquals("PASSED", captor.getValue().getComplianceStatus());
        assertEquals("checked", captor.getValue().getCheckComment());
        assertEquals("reviewer", captor.getValue().getUpdateBy());
    }

    @Test
    public void shouldArchiveDocument()
    {
        InfoProjectDocument current = new InfoProjectDocument();
        current.setDocumentId(1002L);
        current.setProjectId(1L);
        current.setDirectoryId(101L);
        when(documentMapper.selectInfoProjectDocumentById(1002L)).thenReturn(current);
        when(documentMapper.updateInfoProjectDocument(any(InfoProjectDocument.class))).thenReturn(1);

        int rows = service.archiveDocument(1002L, "ARCHIVED", "archiver");

        assertEquals(1, rows);
        ArgumentCaptor<InfoProjectDocument> captor = ArgumentCaptor.forClass(InfoProjectDocument.class);
        verify(documentMapper).updateInfoProjectDocument(captor.capture());
        assertEquals("ARCHIVED", captor.getValue().getArchiveStatus());
        assertEquals("archiver", captor.getValue().getUpdateBy());
    }
}
