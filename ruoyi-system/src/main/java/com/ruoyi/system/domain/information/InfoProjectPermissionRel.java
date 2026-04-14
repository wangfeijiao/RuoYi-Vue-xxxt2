package com.ruoyi.system.domain.information;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class InfoProjectPermissionRel extends BaseEntity
{
    private Long permissionId;
    private Long projectId;
    private String projectName;
    private String scopeType;
    private Long scopeId;
    private String targetType;
    private String targetKey;
    private String canView;
    private String canEdit;
    private String canDownload;
    private String canDelete;
    private String inheritFlag;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    public Long getPermissionId() { return permissionId; }
    public void setPermissionId(Long permissionId) { this.permissionId = permissionId; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getScopeType() { return scopeType; }
    public void setScopeType(String scopeType) { this.scopeType = scopeType; }

    public Long getScopeId() { return scopeId; }
    public void setScopeId(Long scopeId) { this.scopeId = scopeId; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public String getTargetKey() { return targetKey; }
    public void setTargetKey(String targetKey) { this.targetKey = targetKey; }

    public String getCanView() { return canView; }
    public void setCanView(String canView) { this.canView = canView; }

    public String getCanEdit() { return canEdit; }
    public void setCanEdit(String canEdit) { this.canEdit = canEdit; }

    public String getCanDownload() { return canDownload; }
    public void setCanDownload(String canDownload) { this.canDownload = canDownload; }

    public String getCanDelete() { return canDelete; }
    public void setCanDelete(String canDelete) { this.canDelete = canDelete; }

    public String getInheritFlag() { return inheritFlag; }
    public void setInheritFlag(String inheritFlag) { this.inheritFlag = inheritFlag; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
}
