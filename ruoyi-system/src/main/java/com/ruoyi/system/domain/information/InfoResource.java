package com.ruoyi.system.domain.information;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class InfoResource extends BaseEntity
{
    private Long resourceId;
    private String resourceCode;
    private String resourceName;
    private String resourceType;
    private String resourceStatus;
    private String monitorStatus;
    private Long projectId;
    private String projectName;
    private Long ownerDeptId;
    private String ownerName;
    private String maintainerName;
    private Integer cpuCores;
    private Integer memoryGb;
    private Integer storageGb;
    private String osName;
    private String ipAddress;
    private String softwareName;
    private String softwareVersion;
    private Integer licenseCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deliveryTime;
    private String relationSummary;
    private String performanceSummary;

    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }
    public String getResourceCode() { return resourceCode; }
    public void setResourceCode(String resourceCode) { this.resourceCode = resourceCode; }
    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public String getResourceStatus() { return resourceStatus; }
    public void setResourceStatus(String resourceStatus) { this.resourceStatus = resourceStatus; }
    public String getMonitorStatus() { return monitorStatus; }
    public void setMonitorStatus(String monitorStatus) { this.monitorStatus = monitorStatus; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public Long getOwnerDeptId() { return ownerDeptId; }
    public void setOwnerDeptId(Long ownerDeptId) { this.ownerDeptId = ownerDeptId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getMaintainerName() { return maintainerName; }
    public void setMaintainerName(String maintainerName) { this.maintainerName = maintainerName; }
    public Integer getCpuCores() { return cpuCores; }
    public void setCpuCores(Integer cpuCores) { this.cpuCores = cpuCores; }
    public Integer getMemoryGb() { return memoryGb; }
    public void setMemoryGb(Integer memoryGb) { this.memoryGb = memoryGb; }
    public Integer getStorageGb() { return storageGb; }
    public void setStorageGb(Integer storageGb) { this.storageGb = storageGb; }
    public String getOsName() { return osName; }
    public void setOsName(String osName) { this.osName = osName; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getSoftwareName() { return softwareName; }
    public void setSoftwareName(String softwareName) { this.softwareName = softwareName; }
    public String getSoftwareVersion() { return softwareVersion; }
    public void setSoftwareVersion(String softwareVersion) { this.softwareVersion = softwareVersion; }
    public Integer getLicenseCount() { return licenseCount; }
    public void setLicenseCount(Integer licenseCount) { this.licenseCount = licenseCount; }
    public Date getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(Date deliveryTime) { this.deliveryTime = deliveryTime; }
    public String getRelationSummary() { return relationSummary; }
    public void setRelationSummary(String relationSummary) { this.relationSummary = relationSummary; }
    public String getPerformanceSummary() { return performanceSummary; }
    public void setPerformanceSummary(String performanceSummary) { this.performanceSummary = performanceSummary; }
}
