package com.ruoyi.system.domain.information;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class InfoProjectDocument extends BaseEntity
{
    private Long documentId;
    private Long projectId;
    private String projectName;
    private Long directoryId;
    private String directoryName;
    private String documentName;
    private String documentType;
    private String storageKey;
    private String versionNo;
    private String sourceType;
    private String uploadedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadedTime;
    private Long fileSize;
    private String checksum;
    private String complianceStatus;
    private String archiveStatus;
    private String checkComment;
    private String deletedFlag;

    public Long getDocumentId()
    {
        return documentId;
    }

    public void setDocumentId(Long documentId)
    {
        this.documentId = documentId;
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

    public Long getDirectoryId()
    {
        return directoryId;
    }

    public void setDirectoryId(Long directoryId)
    {
        this.directoryId = directoryId;
    }

    public String getDirectoryName()
    {
        return directoryName;
    }

    public void setDirectoryName(String directoryName)
    {
        this.directoryName = directoryName;
    }

    public String getDocumentName()
    {
        return documentName;
    }

    public void setDocumentName(String documentName)
    {
        this.documentName = documentName;
    }

    public String getDocumentType()
    {
        return documentType;
    }

    public void setDocumentType(String documentType)
    {
        this.documentType = documentType;
    }

    public String getStorageKey()
    {
        return storageKey;
    }

    public void setStorageKey(String storageKey)
    {
        this.storageKey = storageKey;
    }

    public String getVersionNo()
    {
        return versionNo;
    }

    public void setVersionNo(String versionNo)
    {
        this.versionNo = versionNo;
    }

    public String getSourceType()
    {
        return sourceType;
    }

    public void setSourceType(String sourceType)
    {
        this.sourceType = sourceType;
    }

    public String getUploadedBy()
    {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy)
    {
        this.uploadedBy = uploadedBy;
    }

    public Date getUploadedTime()
    {
        return uploadedTime;
    }

    public void setUploadedTime(Date uploadedTime)
    {
        this.uploadedTime = uploadedTime;
    }

    public Long getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public String getChecksum()
    {
        return checksum;
    }

    public void setChecksum(String checksum)
    {
        this.checksum = checksum;
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

    public String getCheckComment()
    {
        return checkComment;
    }

    public void setCheckComment(String checkComment)
    {
        this.checkComment = checkComment;
    }

    public String getDeletedFlag()
    {
        return deletedFlag;
    }

    public void setDeletedFlag(String deletedFlag)
    {
        this.deletedFlag = deletedFlag;
    }
}
