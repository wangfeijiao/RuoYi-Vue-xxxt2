package com.ruoyi.system.domain.information;

import com.ruoyi.common.core.domain.BaseEntity;

public class InfoDataAsset extends BaseEntity
{
    private Long assetId;
    private String assetCode;
    private String assetName;
    private String businessDomain;
    private String assetType;
    private String assetStatus;
    private String securityLevel;
    private String sourceSystem;
    private String dataFormat;
    private String schemaDesc;
    private String dictionaryDesc;
    private String updateFrequency;
    private String dataVolume;
    private String storageLocation;
    private String shareMode;
    private String shareCondition;
    private String ownerDeptName;
    private String ownerName;
    private String contactPhone;
    private String tagNames;
    private String lineageDesc;
    private String qualityRuleDesc;

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }
    public String getAssetCode() { return assetCode; }
    public void setAssetCode(String assetCode) { this.assetCode = assetCode; }
    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }
    public String getBusinessDomain() { return businessDomain; }
    public void setBusinessDomain(String businessDomain) { this.businessDomain = businessDomain; }
    public String getAssetType() { return assetType; }
    public void setAssetType(String assetType) { this.assetType = assetType; }
    public String getAssetStatus() { return assetStatus; }
    public void setAssetStatus(String assetStatus) { this.assetStatus = assetStatus; }
    public String getSecurityLevel() { return securityLevel; }
    public void setSecurityLevel(String securityLevel) { this.securityLevel = securityLevel; }
    public String getSourceSystem() { return sourceSystem; }
    public void setSourceSystem(String sourceSystem) { this.sourceSystem = sourceSystem; }
    public String getDataFormat() { return dataFormat; }
    public void setDataFormat(String dataFormat) { this.dataFormat = dataFormat; }
    public String getSchemaDesc() { return schemaDesc; }
    public void setSchemaDesc(String schemaDesc) { this.schemaDesc = schemaDesc; }
    public String getDictionaryDesc() { return dictionaryDesc; }
    public void setDictionaryDesc(String dictionaryDesc) { this.dictionaryDesc = dictionaryDesc; }
    public String getUpdateFrequency() { return updateFrequency; }
    public void setUpdateFrequency(String updateFrequency) { this.updateFrequency = updateFrequency; }
    public String getDataVolume() { return dataVolume; }
    public void setDataVolume(String dataVolume) { this.dataVolume = dataVolume; }
    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }
    public String getShareMode() { return shareMode; }
    public void setShareMode(String shareMode) { this.shareMode = shareMode; }
    public String getShareCondition() { return shareCondition; }
    public void setShareCondition(String shareCondition) { this.shareCondition = shareCondition; }
    public String getOwnerDeptName() { return ownerDeptName; }
    public void setOwnerDeptName(String ownerDeptName) { this.ownerDeptName = ownerDeptName; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getTagNames() { return tagNames; }
    public void setTagNames(String tagNames) { this.tagNames = tagNames; }
    public String getLineageDesc() { return lineageDesc; }
    public void setLineageDesc(String lineageDesc) { this.lineageDesc = lineageDesc; }
    public String getQualityRuleDesc() { return qualityRuleDesc; }
    public void setQualityRuleDesc(String qualityRuleDesc) { this.qualityRuleDesc = qualityRuleDesc; }
}
