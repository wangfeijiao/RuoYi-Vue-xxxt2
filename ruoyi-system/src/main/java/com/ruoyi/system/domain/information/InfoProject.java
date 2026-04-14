package com.ruoyi.system.domain.information;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class InfoProject extends BaseEntity
{
    private Long projectId;
    private String projectCode;
    private String projectName;
    private String projectType;
    private String projectStatus;
    private String projectPhase;
    private Long ownerDeptId;
    private String projectManager;
    private String ownerLeader;
    private String vendorName;
    private String vendorOwner;
    private String portalOwner;
    private String resourceOwner;
    private String dataOwner;
    private String securityOwner;
    private String targetUsers;
    private String directoryTemplateJson;
    private String customDirectoryJson;
    private String acceptanceStatus;
    private Long templateId;
    private String templateVersionNo;
    private String projectObjective;
    private String resourceRequirementSummary;
    private String businessOwner;
    private String technicalOwner;
    private String spaceInitStatus;
    private String archiveStatus;
    private Long currentAcceptanceOrderId;
    private BigDecimal documentCompletionRate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastCollaborationTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date planStartDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date planEndDate;

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getProjectType() { return projectType; }
    public void setProjectType(String projectType) { this.projectType = projectType; }
    public String getProjectStatus() { return projectStatus; }
    public void setProjectStatus(String projectStatus) { this.projectStatus = projectStatus; }
    public String getProjectPhase() { return projectPhase; }
    public void setProjectPhase(String projectPhase) { this.projectPhase = projectPhase; }
    public Long getOwnerDeptId() { return ownerDeptId; }
    public void setOwnerDeptId(Long ownerDeptId) { this.ownerDeptId = ownerDeptId; }
    public String getProjectManager() { return projectManager; }
    public void setProjectManager(String projectManager) { this.projectManager = projectManager; }
    public String getOwnerLeader() { return ownerLeader; }
    public void setOwnerLeader(String ownerLeader) { this.ownerLeader = ownerLeader; }
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public String getVendorOwner() { return vendorOwner; }
    public void setVendorOwner(String vendorOwner) { this.vendorOwner = vendorOwner; }
    public String getPortalOwner() { return portalOwner; }
    public void setPortalOwner(String portalOwner) { this.portalOwner = portalOwner; }
    public String getResourceOwner() { return resourceOwner; }
    public void setResourceOwner(String resourceOwner) { this.resourceOwner = resourceOwner; }
    public String getDataOwner() { return dataOwner; }
    public void setDataOwner(String dataOwner) { this.dataOwner = dataOwner; }
    public String getSecurityOwner() { return securityOwner; }
    public void setSecurityOwner(String securityOwner) { this.securityOwner = securityOwner; }
    public String getTargetUsers() { return targetUsers; }
    public void setTargetUsers(String targetUsers) { this.targetUsers = targetUsers; }
    public String getDirectoryTemplateJson() { return directoryTemplateJson; }
    public void setDirectoryTemplateJson(String directoryTemplateJson) { this.directoryTemplateJson = directoryTemplateJson; }
    public String getCustomDirectoryJson() { return customDirectoryJson; }
    public void setCustomDirectoryJson(String customDirectoryJson) { this.customDirectoryJson = customDirectoryJson; }
    public String getAcceptanceStatus() { return acceptanceStatus; }
    public void setAcceptanceStatus(String acceptanceStatus) { this.acceptanceStatus = acceptanceStatus; }
    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    public String getTemplateVersionNo() { return templateVersionNo; }
    public void setTemplateVersionNo(String templateVersionNo) { this.templateVersionNo = templateVersionNo; }
    public String getProjectObjective() { return projectObjective; }
    public void setProjectObjective(String projectObjective) { this.projectObjective = projectObjective; }
    public String getResourceRequirementSummary() { return resourceRequirementSummary; }
    public void setResourceRequirementSummary(String resourceRequirementSummary) { this.resourceRequirementSummary = resourceRequirementSummary; }
    public String getBusinessOwner() { return businessOwner; }
    public void setBusinessOwner(String businessOwner) { this.businessOwner = businessOwner; }
    public String getTechnicalOwner() { return technicalOwner; }
    public void setTechnicalOwner(String technicalOwner) { this.technicalOwner = technicalOwner; }
    public String getSpaceInitStatus() { return spaceInitStatus; }
    public void setSpaceInitStatus(String spaceInitStatus) { this.spaceInitStatus = spaceInitStatus; }
    public String getArchiveStatus() { return archiveStatus; }
    public void setArchiveStatus(String archiveStatus) { this.archiveStatus = archiveStatus; }
    public Long getCurrentAcceptanceOrderId() { return currentAcceptanceOrderId; }
    public void setCurrentAcceptanceOrderId(Long currentAcceptanceOrderId) { this.currentAcceptanceOrderId = currentAcceptanceOrderId; }
    public BigDecimal getDocumentCompletionRate() { return documentCompletionRate; }
    public void setDocumentCompletionRate(BigDecimal documentCompletionRate) { this.documentCompletionRate = documentCompletionRate; }
    public Date getLastCollaborationTime() { return lastCollaborationTime; }
    public void setLastCollaborationTime(Date lastCollaborationTime) { this.lastCollaborationTime = lastCollaborationTime; }
    public Date getPlanStartDate() { return planStartDate; }
    public void setPlanStartDate(Date planStartDate) { this.planStartDate = planStartDate; }
    public Date getPlanEndDate() { return planEndDate; }
    public void setPlanEndDate(Date planEndDate) { this.planEndDate = planEndDate; }
}
