package com.ruoyi.system.domain.information;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class InfoDataAssetVersion
{
    private Long versionId;
    private Long assetId;
    private String versionNo;
    private String changeType;
    private String snapshotJson;
    private String changeReason;
    private String changedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date changedTime;

    public Long getVersionId() { return versionId; }
    public void setVersionId(Long versionId) { this.versionId = versionId; }
    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }
    public String getVersionNo() { return versionNo; }
    public void setVersionNo(String versionNo) { this.versionNo = versionNo; }
    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }
    public String getSnapshotJson() { return snapshotJson; }
    public void setSnapshotJson(String snapshotJson) { this.snapshotJson = snapshotJson; }
    public String getChangeReason() { return changeReason; }
    public void setChangeReason(String changeReason) { this.changeReason = changeReason; }
    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
    public Date getChangedTime() { return changedTime; }
    public void setChangedTime(Date changedTime) { this.changedTime = changedTime; }
}
