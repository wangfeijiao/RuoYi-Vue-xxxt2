package com.ruoyi.system.mapper.information;

import java.util.List;
import com.ruoyi.system.domain.information.InfoProjectDocument;

public interface InfoProjectDocumentMapper
{
    InfoProjectDocument selectInfoProjectDocumentById(Long documentId);

    List<InfoProjectDocument> selectInfoProjectDocumentListByProjectId(Long projectId);

    int countByProjectId(Long projectId);

    int countPassedComplianceByProjectId(Long projectId);

    int countArchivedByProjectId(Long projectId);

    int insertInfoProjectDocument(InfoProjectDocument document);

    int updateInfoProjectDocument(InfoProjectDocument document);
}
