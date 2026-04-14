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
import com.ruoyi.system.domain.information.InfoProjectCollaborationRecord;
import com.ruoyi.system.mapper.information.InfoProjectCollaborationRecordMapper;
import com.ruoyi.system.mapper.information.InfoProjectMapper;

@ExtendWith(MockitoExtension.class)
public class InfoProjectCollaborationRecordServiceImplTest
{
    @Mock
    private InfoProjectMapper projectMapper;

    @Mock
    private InfoProjectCollaborationRecordMapper collaborationRecordMapper;

    @InjectMocks
    private InfoProjectCollaborationRecordServiceImpl service;

    @Test
    public void shouldInsertCollaborationRecordAndUpdateProjectLastCollaborationTime()
    {
        InfoProject project = new InfoProject();
        project.setProjectId(1L);
        when(projectMapper.selectInfoProjectById(1L)).thenReturn(project);
        when(collaborationRecordMapper.insertInfoProjectCollaborationRecord(any(InfoProjectCollaborationRecord.class)))
            .thenReturn(1);
        when(projectMapper.updateInfoProject(any(InfoProject.class))).thenReturn(1);

        InfoProjectCollaborationRecord record = new InfoProjectCollaborationRecord();
        record.setPhaseCode("EXECUTION");
        record.setRecordType("MATERIAL_CHECK");
        record.setRecordSummary("materials checked");
        record.setCreateBy("tester");

        int rows = service.insertProjectCollaborationRecord(1L, record);

        assertEquals(1, rows);
        ArgumentCaptor<InfoProjectCollaborationRecord> recordCaptor =
            ArgumentCaptor.forClass(InfoProjectCollaborationRecord.class);
        verify(collaborationRecordMapper).insertInfoProjectCollaborationRecord(recordCaptor.capture());
        assertNotNull(recordCaptor.getValue().getRecordTime());
        assertEquals("RECORDED", recordCaptor.getValue().getRecordStatus());

        ArgumentCaptor<InfoProject> projectCaptor = ArgumentCaptor.forClass(InfoProject.class);
        verify(projectMapper).updateInfoProject(projectCaptor.capture());
        assertNotNull(projectCaptor.getValue().getLastCollaborationTime());
    }
}
