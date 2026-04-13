package com.ruoyi.system.domain.information;

import com.ruoyi.common.core.domain.BaseEntity;

public class InfoNetworkResource extends BaseEntity
{
    private Long networkId;
    private String networkCode;
    private String networkName;
    private String resourceType;
    private String resourceStatus;
    private String securityStatus;
    private Long projectId;
    private String projectName;
    private String ownerName;
    private String ipSegment;
    private Integer ipCount;
    private String vlanNo;
    private Integer bandwidthMbps;
    private String deviceName;
    private String deviceType;
    private String ruleSummary;
    private String accessPolicy;
    private String topologyDoc;
    private String allocationDoc;
    private String monitorSummary;

    public Long getNetworkId() { return networkId; }
    public void setNetworkId(Long networkId) { this.networkId = networkId; }
    public String getNetworkCode() { return networkCode; }
    public void setNetworkCode(String networkCode) { this.networkCode = networkCode; }
    public String getNetworkName() { return networkName; }
    public void setNetworkName(String networkName) { this.networkName = networkName; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public String getResourceStatus() { return resourceStatus; }
    public void setResourceStatus(String resourceStatus) { this.resourceStatus = resourceStatus; }
    public String getSecurityStatus() { return securityStatus; }
    public void setSecurityStatus(String securityStatus) { this.securityStatus = securityStatus; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getIpSegment() { return ipSegment; }
    public void setIpSegment(String ipSegment) { this.ipSegment = ipSegment; }
    public Integer getIpCount() { return ipCount; }
    public void setIpCount(Integer ipCount) { this.ipCount = ipCount; }
    public String getVlanNo() { return vlanNo; }
    public void setVlanNo(String vlanNo) { this.vlanNo = vlanNo; }
    public Integer getBandwidthMbps() { return bandwidthMbps; }
    public void setBandwidthMbps(Integer bandwidthMbps) { this.bandwidthMbps = bandwidthMbps; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    public String getRuleSummary() { return ruleSummary; }
    public void setRuleSummary(String ruleSummary) { this.ruleSummary = ruleSummary; }
    public String getAccessPolicy() { return accessPolicy; }
    public void setAccessPolicy(String accessPolicy) { this.accessPolicy = accessPolicy; }
    public String getTopologyDoc() { return topologyDoc; }
    public void setTopologyDoc(String topologyDoc) { this.topologyDoc = topologyDoc; }
    public String getAllocationDoc() { return allocationDoc; }
    public void setAllocationDoc(String allocationDoc) { this.allocationDoc = allocationDoc; }
    public String getMonitorSummary() { return monitorSummary; }
    public void setMonitorSummary(String monitorSummary) { this.monitorSummary = monitorSummary; }
}
