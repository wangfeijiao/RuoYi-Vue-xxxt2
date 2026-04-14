package com.ruoyi.system.service.information.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.information.InfoProject;
import com.ruoyi.system.domain.information.InfoProjectDocument;
import com.ruoyi.system.domain.information.InfoProjectSpaceDirectory;
import com.ruoyi.system.mapper.information.InfoProjectDocumentMapper;
import com.ruoyi.system.mapper.information.InfoProjectMapper;
import com.ruoyi.system.mapper.information.InfoProjectSpaceDirectoryMapper;
import com.ruoyi.system.service.information.IInfoProjectDocumentService;

@Service
public class InfoProjectDocumentServiceImpl implements IInfoProjectDocumentService
{
    private static final String SOURCE_MANUAL_UPLOAD = "MANUAL_UPLOAD";
    private static final String VERSION_DEFAULT = "V1";
    private static final String STATUS_PENDING_CHECK = "PENDING_CHECK";
    private static final String STATUS_ARCHIVED = "ARCHIVED";
    private static final String STATUS_UNARCHIVED = "UNARCHIVED";
    private static final String FLAG_NOT_DELETED = "0";

    private static final Set<String> VALID_COMPLIANCE_STATUS = new HashSet<>(
        Arrays.asList("PENDING_CHECK", "PASSED", "REJECTED"));

    private static final Set<String> VALID_ARCHIVE_STATUS = new HashSet<>(
        Arrays.asList(STATUS_ARCHIVED, STATUS_UNARCHIVED));

    @Autowired
    private InfoProjectMapper projectMapper;

    @Autowired
    private InfoProjectSpaceDirectoryMapper spaceDirectoryMapper;

    @Autowired
    private InfoProjectDocumentMapper documentMapper;

    @Override
    public List<InfoProjectDocument> selectInfoProjectDocumentList(Long projectId)
    {
        ensureProjectExists(projectId);
        return documentMapper.selectInfoProjectDocumentListByProjectId(projectId);
    }

    @Override
    public int insertProjectDocument(Long projectId, InfoProjectDocument document)
    {
        ensureProjectExists(projectId);
        if (document == null)
        {
            throw new ServiceException("Document payload cannot be null");
        }
        if (document.getDirectoryId() == null)
        {
            throw new ServiceException("Directory id cannot be null");
        }
        if (StringUtils.isEmpty(document.getDocumentName()))
        {
            throw new ServiceException("Document name cannot be empty");
        }
        if (StringUtils.isEmpty(document.getDocumentType()))
        {
            throw new ServiceException("Document type cannot be empty");
        }

        InfoProjectSpaceDirectory directory = spaceDirectoryMapper.selectInfoProjectSpaceDirectoryById(document.getDirectoryId());
        if (directory == null || !projectId.equals(directory.getProjectId()))
        {
            throw new ServiceException("Directory does not belong to project");
        }

        document.setProjectId(projectId);
        document.setVersionNo(defaultString(document.getVersionNo(), VERSION_DEFAULT));
        document.setSourceType(defaultString(document.getSourceType(), SOURCE_MANUAL_UPLOAD));
        document.setUploadedBy(resolveUploadedBy(document));
        document.setUploadedTime(document.getUploadedTime() == null ? new Date() : document.getUploadedTime());
        document.setComplianceStatus(defaultString(document.getComplianceStatus(), STATUS_PENDING_CHECK));
        document.setArchiveStatus(defaultString(document.getArchiveStatus(), STATUS_UNARCHIVED));
        document.setDeletedFlag(defaultString(document.getDeletedFlag(), FLAG_NOT_DELETED));
        return documentMapper.insertInfoProjectDocument(document);
    }

    @Override
    public int updateDocumentCompliance(Long documentId, String complianceStatus, String checkComment, String updateBy)
    {
        InfoProjectDocument current = requireDocument(documentId);
        String targetComplianceStatus = defaultString(complianceStatus, STATUS_PENDING_CHECK);
        if (!VALID_COMPLIANCE_STATUS.contains(targetComplianceStatus))
        {
            throw new ServiceException("Invalid compliance status");
        }
        current.setComplianceStatus(targetComplianceStatus);
        current.setCheckComment(checkComment);
        current.setUpdateBy(updateBy);
        return documentMapper.updateInfoProjectDocument(current);
    }

    @Override
    public int archiveDocument(Long documentId, String archiveStatus, String updateBy)
    {
        InfoProjectDocument current = requireDocument(documentId);
        String targetArchiveStatus = defaultString(archiveStatus, STATUS_ARCHIVED);
        if (!VALID_ARCHIVE_STATUS.contains(targetArchiveStatus))
        {
            throw new ServiceException("Invalid archive status");
        }
        current.setArchiveStatus(targetArchiveStatus);
        current.setUpdateBy(updateBy);
        return documentMapper.updateInfoProjectDocument(current);
    }

    private String resolveUploadedBy(InfoProjectDocument document)
    {
        if (StringUtils.isNotEmpty(document.getUploadedBy()))
        {
            return document.getUploadedBy();
        }
        if (StringUtils.isNotEmpty(document.getCreateBy()))
        {
            return document.getCreateBy();
        }
        throw new ServiceException("uploadedBy cannot be empty");
    }

    private InfoProjectDocument requireDocument(Long documentId)
    {
        if (documentId == null)
        {
            throw new ServiceException("Document id cannot be null");
        }
        InfoProjectDocument current = documentMapper.selectInfoProjectDocumentById(documentId);
        if (current == null)
        {
            throw new ServiceException("Document does not exist");
        }
        return current;
    }

    private InfoProject ensureProjectExists(Long projectId)
    {
        if (projectId == null)
        {
            throw new ServiceException("Project id cannot be null");
        }
        InfoProject project = projectMapper.selectInfoProjectById(projectId);
        if (project == null)
        {
            throw new ServiceException("Project does not exist");
        }
        return project;
    }

    private String defaultString(String value, String defaultValue)
    {
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }
}
