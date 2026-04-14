package com.ruoyi.system.domain.information;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class InfoApplicationDependencyRel extends BaseEntity
{
    private Long dependencyId;
    private Long applicationId;
    private String dependencyType;
    private Long targetId;
    private String targetCode;
    private String targetName;
    private String targetSource;
    private String targetKey;
    private String dependencyDirection;
    private String dependencyRole;
    private String importanceLevel;
    private String statusLinkEnabled;
    private String alertLinkEnabled;
    private String dependencyStatus;
    private String runtimeStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastSyncTime;
    private String targetSnapshotJson;

    public Long getDependencyId() { return dependencyId; }
    public void setDependencyId(Long dependencyId) { this.dependencyId = dependencyId; }

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public String getDependencyType() { return dependencyType; }
    public void setDependencyType(String dependencyType) { this.dependencyType = dependencyType; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public String getTargetCode() { return targetCode; }
    public void setTargetCode(String targetCode) { this.targetCode = targetCode; }

    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }

    public String getTargetSource() { return targetSource; }
    public void setTargetSource(String targetSource) { this.targetSource = targetSource; }

    public String getTargetKey() { return targetKey; }
    public void setTargetKey(String targetKey) { this.targetKey = targetKey; }

    public String getDependencyDirection() { return dependencyDirection; }
    public void setDependencyDirection(String dependencyDirection) { this.dependencyDirection = dependencyDirection; }

    public String getDependencyRole() { return dependencyRole; }
    public void setDependencyRole(String dependencyRole) { this.dependencyRole = dependencyRole; }

    public String getImportanceLevel() { return importanceLevel; }
    public void setImportanceLevel(String importanceLevel) { this.importanceLevel = importanceLevel; }

    public String getStatusLinkEnabled() { return statusLinkEnabled; }
    public void setStatusLinkEnabled(String statusLinkEnabled) { this.statusLinkEnabled = statusLinkEnabled; }

    public String getAlertLinkEnabled() { return alertLinkEnabled; }
    public void setAlertLinkEnabled(String alertLinkEnabled) { this.alertLinkEnabled = alertLinkEnabled; }

    public String getDependencyStatus() { return dependencyStatus; }
    public void setDependencyStatus(String dependencyStatus) { this.dependencyStatus = dependencyStatus; }

    public String getRuntimeStatus() { return runtimeStatus; }
    public void setRuntimeStatus(String runtimeStatus) { this.runtimeStatus = runtimeStatus; }

    public Date getLastSyncTime() { return lastSyncTime; }
    public void setLastSyncTime(Date lastSyncTime) { this.lastSyncTime = lastSyncTime; }

    public String getTargetSnapshotJson() { return targetSnapshotJson; }
    public void setTargetSnapshotJson(String targetSnapshotJson) { this.targetSnapshotJson = targetSnapshotJson; }
}
