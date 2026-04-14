package com.ruoyi.system.service.information;

import java.util.List;
import com.ruoyi.system.domain.information.InfoProjectDocument;

public interface IInfoProjectDocumentService
{
    List<InfoProjectDocument> selectInfoProjectDocumentList(Long projectId);

    int insertProjectDocument(Long projectId, InfoProjectDocument document);

    int updateDocumentCompliance(Long documentId, String complianceStatus, String checkComment, String updateBy);

    int archiveDocument(Long documentId, String archiveStatus, String updateBy);
}
