package com.ruoyi.system.domain.information;

public class InfoApproveBody
{
    private Boolean approved;
    private String approvalComment;

    public Boolean getApproved() { return approved; }
    public void setApproved(Boolean approved) { this.approved = approved; }
    public String getApprovalComment() { return approvalComment; }
    public void setApprovalComment(String approvalComment) { this.approvalComment = approvalComment; }
}
