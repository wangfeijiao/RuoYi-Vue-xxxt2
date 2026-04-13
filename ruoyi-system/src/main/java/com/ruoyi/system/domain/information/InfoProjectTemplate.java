package com.ruoyi.system.domain.information;

import com.ruoyi.common.core.domain.BaseEntity;

public class InfoProjectTemplate extends BaseEntity
{
    private Long templateId;
    private String templateName;
    private String projectType;
    private String phaseName;
    private String versionNo;
    private String directoryJson;
    private String status;

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    public String getProjectType() { return projectType; }
    public void setProjectType(String projectType) { this.projectType = projectType; }
    public String getPhaseName() { return phaseName; }
    public void setPhaseName(String phaseName) { this.phaseName = phaseName; }
    public String getVersionNo() { return versionNo; }
    public void setVersionNo(String versionNo) { this.versionNo = versionNo; }
    public String getDirectoryJson() { return directoryJson; }
    public void setDirectoryJson(String directoryJson) { this.directoryJson = directoryJson; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
