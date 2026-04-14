package com.ruoyi.system.service.information.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ruoyi.system.domain.information.InfoApplication;
import com.ruoyi.system.domain.information.InfoApplicationAlertEvent;
import com.ruoyi.system.domain.information.InfoApplicationAlertHandleBody;
import com.ruoyi.system.domain.information.InfoApplicationDependencyRel;
import com.ruoyi.system.mapper.information.InfoApplicationMapper;

@ExtendWith(MockitoExtension.class)
public class InfoApplicationServiceImplTest
{
    @Mock
    private InfoApplicationMapper applicationMapper;

    @InjectMocks
    private InfoApplicationServiceImpl service;

    @Test
    public void shouldPreserveProtectedFieldsAndClearPrimaryProjectWhenProjectIdRemoved()
    {
        Date lastAlertTime = new Date();
        InfoApplication current = new InfoApplication();
        current.setApplicationId(1L);
        current.setApplicationCode("APP-001");
        current.setApplicationName("Application");
        current.setProjectId(100L);
        current.setDeletedFlag("0");
        current.setLinkageStatus("BLOCKED");
        current.setLastAlertTime(lastAlertTime);

        InfoApplication update = new InfoApplication();
        update.setApplicationId(1L);
        update.setApplicationCode("APP-001");
        update.setApplicationName("Application");
        update.setProjectId(null);
        update.setDeletedFlag("1");
        update.setLinkageStatus("NORMAL");
        update.setLastAlertTime(new Date(lastAlertTime.getTime() + 1000));
        update.setUpdateBy("tester");

        when(applicationMapper.selectInfoApplicationById(1L)).thenReturn(current);
        when(applicationMapper.countInfoApplicationCode("APP-001", 1L)).thenReturn(0);
        when(applicationMapper.updateInfoApplication(any(InfoApplication.class))).thenReturn(1);

        int rows = service.updateInfoApplication(update);

        assertEquals(1, rows);
        ArgumentCaptor<InfoApplication> captor = ArgumentCaptor.forClass(InfoApplication.class);
        verify(applicationMapper).updateInfoApplication(captor.capture());
        InfoApplication persisted = captor.getValue();
        assertNull(persisted.getProjectId());
        assertEquals("0", persisted.getDeletedFlag());
        assertEquals("BLOCKED", persisted.getLinkageStatus());
        assertEquals(lastAlertTime, persisted.getLastAlertTime());
        verify(applicationMapper).clearPrimaryInfoApplicationProjectRel(1L, "tester");
        verify(applicationMapper).updateInfoApplicationProjectId(1L, null, "tester");
        verify(applicationMapper, never()).selectInfoApplicationProjectRelByProject(eq(1L), any());
    }

    @Test
    public void shouldIgnoreDisabledDependenciesWhenCalculatingLinkage()
    {
        InfoApplication current = new InfoApplication();
        current.setApplicationId(1L);
        current.setApplicationName("Application");
        current.setApplicationStatus("RUNNING");
        current.setLinkageStatus("UNKNOWN");

        Map<String, Object> disabledBlocked = runtimeRow("0", "BLOCKED");
        Map<String, Object> enabledNormal = runtimeRow("1", "NORMAL");

        when(applicationMapper.selectInfoApplicationById(1L)).thenReturn(current);
        when(applicationMapper.selectInfoApplicationRuntimeStatusList(1L))
            .thenReturn(Arrays.asList(disabledBlocked, enabledNormal));
        when(applicationMapper.countOpenInfoApplicationAlertByAppId(1L)).thenReturn(0);
        when(applicationMapper.selectLatestInfoApplicationAlertByAppId(1L)).thenReturn(null);
        when(applicationMapper.countInfoApplicationProjectRel(1L)).thenReturn(0);
        when(applicationMapper.countInfoApplicationDependencyRel(1L)).thenReturn(2);
        when(applicationMapper.selectInfoApplicationDependencyTypeSummary(1L)).thenReturn(Collections.emptyList());
        when(applicationMapper.updateInfoApplicationLinkageStatus(1L, "NORMAL", null, "tester")).thenReturn(1);

        Map<String, Object> overview = service.selectInfoApplicationStatusOverview(1L);
        int rows = service.recalculateInfoApplicationStatus(1L, "tester");

        assertEquals(0L, overview.get("abnormalDependencyCount"));
        assertEquals(1, rows);
        verify(applicationMapper).updateInfoApplicationLinkageStatus(1L, "NORMAL", null, "tester");
    }

    @Test
    public void shouldSanitizeDependencyAndAlertSensitivePayloads()
    {
        InfoApplication current = new InfoApplication();
        current.setApplicationId(1L);

        InfoApplicationDependencyRel dependency = new InfoApplicationDependencyRel();
        dependency.setDependencyId(10L);
        dependency.setApplicationId(1L);
        dependency.setTargetName("Resource A");
        dependency.setRuntimeStatus("NORMAL");
        dependency.setTargetSnapshotJson("{\"secret\":true}");

        InfoApplicationAlertEvent alert = new InfoApplicationAlertEvent();
        alert.setAlertId(20L);
        alert.setApplicationId(1L);
        alert.setEventTitle("CPU Warning");
        alert.setPayloadJson("{\"token\":\"abc\"}");

        when(applicationMapper.selectInfoApplicationById(1L)).thenReturn(current);
        when(applicationMapper.selectInfoApplicationDependencyRelList(any(InfoApplicationDependencyRel.class)))
            .thenReturn(Collections.singletonList(dependency));
        when(applicationMapper.selectInfoApplicationAlertEventList(any(InfoApplicationAlertEvent.class)))
            .thenReturn(Collections.singletonList(alert));

        List<InfoApplicationDependencyRel> dependencies =
            service.selectInfoApplicationDependencyRelList(1L, new InfoApplicationDependencyRel());
        List<InfoApplicationAlertEvent> alerts =
            service.selectInfoApplicationAlertEventList(1L, new InfoApplicationAlertEvent());

        assertEquals(1, dependencies.size());
        assertEquals("Resource A", dependencies.get(0).getTargetName());
        assertNull(dependencies.get(0).getTargetSnapshotJson());

        assertEquals(1, alerts.size());
        assertEquals("CPU Warning", alerts.get(0).getEventTitle());
        assertNull(alerts.get(0).getPayloadJson());
    }

    @Test
    public void shouldUseOperatorAsAlertHandlerDuringAck()
    {
        InfoApplicationAlertEvent current = new InfoApplicationAlertEvent();
        current.setAlertId(99L);
        current.setApplicationId(1L);
        current.setEventStatus("OPEN");

        InfoApplicationAlertHandleBody body = new InfoApplicationAlertHandleBody();
        body.setHandlerName("spoofed-user");
        body.setRemark("handled");

        when(applicationMapper.selectInfoApplicationAlertEventById(99L)).thenReturn(current);
        when(applicationMapper.selectInfoApplicationById(1L)).thenReturn(new InfoApplication());
        when(applicationMapper.selectInfoApplicationRuntimeStatusList(1L)).thenReturn(Collections.emptyList());
        when(applicationMapper.countOpenInfoApplicationAlertByAppId(1L)).thenReturn(0);
        when(applicationMapper.selectLatestInfoApplicationAlertByAppId(1L)).thenReturn(null);
        when(applicationMapper.updateInfoApplicationAlertEvent(any(InfoApplicationAlertEvent.class))).thenReturn(1);
        when(applicationMapper.updateInfoApplicationLinkageStatus(1L, "UNKNOWN", null, "operator")).thenReturn(1);

        service.ackInfoApplicationAlert(99L, "operator", body);

        ArgumentCaptor<InfoApplicationAlertEvent> captor = ArgumentCaptor.forClass(InfoApplicationAlertEvent.class);
        verify(applicationMapper).updateInfoApplicationAlertEvent(captor.capture());
        assertEquals("operator", captor.getValue().getHandlerName());
        assertEquals("handled", captor.getValue().getRemark());
    }

    private Map<String, Object> runtimeRow(String statusLinkEnabled, String runtimeStatus)
    {
        Map<String, Object> row = new HashMap<>();
        row.put("statusLinkEnabled", statusLinkEnabled);
        row.put("runtimeStatus", runtimeStatus);
        return row;
    }
}
