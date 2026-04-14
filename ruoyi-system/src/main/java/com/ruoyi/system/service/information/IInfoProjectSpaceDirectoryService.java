package com.ruoyi.system.service.information;

import java.util.List;
import com.ruoyi.system.domain.information.InfoProjectSpaceDirectory;

public interface IInfoProjectSpaceDirectoryService
{
    List<InfoProjectSpaceDirectory> selectInfoProjectSpaceDirectoryList(Long projectId);

    int initProjectSpace(Long projectId, String operator);

    int insertCustomDirectory(Long projectId, InfoProjectSpaceDirectory directory);
}
