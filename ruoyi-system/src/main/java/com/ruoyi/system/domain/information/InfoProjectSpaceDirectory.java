package com.ruoyi.system.domain.information;

import com.ruoyi.common.core.domain.BaseEntity;

public class InfoProjectSpaceDirectory extends BaseEntity
{
    private Long directoryId;
    private Long projectId;
    private String projectName;
    private Long parentId;
    private Long templateId;
    private String directoryCode;
    private String directoryName;
    private Integer directoryLevel;
    private String directoryType;
    private String fullPath;
    private String requiredFlag;
    private String complianceStatus;
    private String archiveStatus;
    private Integer sortOrder;

    public Long getDirectoryId()
    {
        return directoryId;
    }

    public void setDirectoryId(Long directoryId)
    {
        this.directoryId = directoryId;
    }

    public Long getProjectId()
    {
        return projectId;
    }

    public void setProjectId(Long projectId)
    {
        this.projectId = projectId;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public Long getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(Long templateId)
    {
        this.templateId = templateId;
    }

    public String getDirectoryCode()
    {
        return directoryCode;
    }

    public void setDirectoryCode(String directoryCode)
    {
        this.directoryCode = directoryCode;
    }

    public String getDirectoryName()
    {
        return directoryName;
    }

    public void setDirectoryName(String directoryName)
    {
        this.directoryName = directoryName;
    }

    public Integer getDirectoryLevel()
    {
        return directoryLevel;
    }

    public void setDirectoryLevel(Integer directoryLevel)
    {
        this.directoryLevel = directoryLevel;
    }

    public String getDirectoryType()
    {
        return directoryType;
    }

    public void setDirectoryType(String directoryType)
    {
        this.directoryType = directoryType;
    }

    public String getFullPath()
    {
        return fullPath;
    }

    public void setFullPath(String fullPath)
    {
        this.fullPath = fullPath;
    }

    public String getRequiredFlag()
    {
        return requiredFlag;
    }

    public void setRequiredFlag(String requiredFlag)
    {
        this.requiredFlag = requiredFlag;
    }

    public String getComplianceStatus()
    {
        return complianceStatus;
    }

    public void setComplianceStatus(String complianceStatus)
    {
        this.complianceStatus = complianceStatus;
    }

    public String getArchiveStatus()
    {
        return archiveStatus;
    }

    public void setArchiveStatus(String archiveStatus)
    {
        this.archiveStatus = archiveStatus;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }
}
