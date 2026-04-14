package com.ruoyi.system.mapper.information;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.information.InfoProjectSpaceDirectory;

public interface InfoProjectSpaceDirectoryMapper
{
    InfoProjectSpaceDirectory selectInfoProjectSpaceDirectoryById(Long directoryId);

    List<InfoProjectSpaceDirectory> selectInfoProjectSpaceDirectoryListByProjectId(Long projectId);

    int countByProjectId(Long projectId);

    int countRequiredDirectoryByProjectId(Long projectId);

    int countDirectoryCode(@Param("projectId") Long projectId, @Param("directoryCode") String directoryCode);

    int insertInfoProjectSpaceDirectory(InfoProjectSpaceDirectory directory);

    int updateInfoProjectSpaceDirectory(InfoProjectSpaceDirectory directory);
}
