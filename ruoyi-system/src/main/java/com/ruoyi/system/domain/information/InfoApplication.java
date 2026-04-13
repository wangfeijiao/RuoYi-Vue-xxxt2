package com.ruoyi.system.domain.information;

import com.ruoyi.common.core.domain.BaseEntity;

public class InfoApplication extends BaseEntity
{
    private Long applicationId;
    private String applicationCode;
    private String applicationName;
    private String applicationType;
    private String classificationLevel;
    private String applicationStatus;
    private Long projectId;
    private String projectName;
    private String ownerOrg;
    private String ownerName;
    private String vendorName;
    private String vendorOwner;
    private String portalOwner;
    private String resourceOwner;
    private String dataOwner;
    private String securityOwner;
    private String accessUrl;
    private String techStack;
    private String resourceSummary;
    private String networkSummary;
    private String relationSummary;

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
    public String getApplicationCode() { return applicationCode; }
    public void setApplicationCode(String applicationCode) { this.applicationCode = applicationCode; }
    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String applicationName) { this.applicationName = applicationName; }
    public String getApplicationType() { return applicationType; }
    public void setApplicationType(String applicationType) { this.applicationType = applicationType; }
    public String getClassificationLevel() { return classificationLevel; }
    public void setClassificationLevel(String classificationLevel) { this.classificationLevel = classificationLevel; }
    public String getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getOwnerOrg() { return ownerOrg; }
    public void setOwnerOrg(String ownerOrg) { this.ownerOrg = ownerOrg; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
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
    public String getAccessUrl() { return accessUrl; }
    public void setAccessUrl(String accessUrl) { this.accessUrl = accessUrl; }
    public String getTechStack() { return techStack; }
    public void setTechStack(String techStack) { this.techStack = techStack; }
    public String getResourceSummary() { return resourceSummary; }
    public void setResourceSummary(String resourceSummary) { this.resourceSummary = resourceSummary; }
    public String getNetworkSummary() { return networkSummary; }
    public void setNetworkSummary(String networkSummary) { this.networkSummary = networkSummary; }
    public String getRelationSummary() { return relationSummary; }
    public void setRelationSummary(String relationSummary) { this.relationSummary = relationSummary; }
}
