package com.ruoyi.system.domain.information;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class InfoWorkOrder extends BaseEntity
{
    private Long workOrderId;
    private String workOrderNo;
    private String domainType;
    private String requestType;
    private String orderStatus;
    private Long projectId;
    private String projectName;
    private Long subjectId;
    private String subjectName;
    private String subjectType;
    private Long applicantDeptId;
    private String applicantName;
    private String approverName;
    private String executorName;
    private String requestTitle;
    private String requestReason;
    private String currentSnapshotJson;
    private String requestPayloadJson;
    private String approvalComment;
    private String executionResult;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expectFinishTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submittedTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date approvedTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date executedTime;

    public Long getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(Long workOrderId) { this.workOrderId = workOrderId; }
    public String getWorkOrderNo() { return workOrderNo; }
    public void setWorkOrderNo(String workOrderNo) { this.workOrderNo = workOrderNo; }
    public String getDomainType() { return domainType; }
    public void setDomainType(String domainType) { this.domainType = domainType; }
    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getSubjectType() { return subjectType; }
    public void setSubjectType(String subjectType) { this.subjectType = subjectType; }
    public Long getApplicantDeptId() { return applicantDeptId; }
    public void setApplicantDeptId(Long applicantDeptId) { this.applicantDeptId = applicantDeptId; }
    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
    public String getApproverName() { return approverName; }
    public void setApproverName(String approverName) { this.approverName = approverName; }
    public String getExecutorName() { return executorName; }
    public void setExecutorName(String executorName) { this.executorName = executorName; }
    public String getRequestTitle() { return requestTitle; }
    public void setRequestTitle(String requestTitle) { this.requestTitle = requestTitle; }
    public String getRequestReason() { return requestReason; }
    public void setRequestReason(String requestReason) { this.requestReason = requestReason; }
    public String getCurrentSnapshotJson() { return currentSnapshotJson; }
    public void setCurrentSnapshotJson(String currentSnapshotJson) { this.currentSnapshotJson = currentSnapshotJson; }
    public String getRequestPayloadJson() { return requestPayloadJson; }
    public void setRequestPayloadJson(String requestPayloadJson) { this.requestPayloadJson = requestPayloadJson; }
    public String getApprovalComment() { return approvalComment; }
    public void setApprovalComment(String approvalComment) { this.approvalComment = approvalComment; }
    public String getExecutionResult() { return executionResult; }
    public void setExecutionResult(String executionResult) { this.executionResult = executionResult; }
    public Date getExpectFinishTime() { return expectFinishTime; }
    public void setExpectFinishTime(Date expectFinishTime) { this.expectFinishTime = expectFinishTime; }
    public Date getSubmittedTime() { return submittedTime; }
    public void setSubmittedTime(Date submittedTime) { this.submittedTime = submittedTime; }
    public Date getApprovedTime() { return approvedTime; }
    public void setApprovedTime(Date approvedTime) { this.approvedTime = approvedTime; }
    public Date getExecutedTime() { return executedTime; }
    public void setExecutedTime(Date executedTime) { this.executedTime = executedTime; }
}
