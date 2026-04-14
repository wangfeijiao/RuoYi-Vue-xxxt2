package com.ruoyi.system.service.information.impl;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.information.InfoProject;
import com.ruoyi.system.domain.information.InfoProjectSpaceDirectory;
import com.ruoyi.system.domain.information.InfoProjectTemplate;
import com.ruoyi.system.mapper.information.InfoProjectMapper;
import com.ruoyi.system.mapper.information.InfoProjectSpaceDirectoryMapper;
import com.ruoyi.system.mapper.information.InfoProjectTemplateMapper;
import com.ruoyi.system.service.information.IInfoProjectSpaceDirectoryService;

@Service
public class InfoProjectSpaceDirectoryServiceImpl implements IInfoProjectSpaceDirectoryService
{
    private static final String DIRECTORY_TYPE_STANDARD = "STANDARD";
    private static final String DIRECTORY_TYPE_CUSTOM = "CUSTOM";
    private static final String FLAG_TRUE = "1";
    private static final String FLAG_FALSE = "0";
    private static final String STATUS_PENDING_CHECK = "PENDING_CHECK";
    private static final String STATUS_UNARCHIVED = "UNARCHIVED";
    private static final String SPACE_STATUS_INITIALIZED = "INITIALIZED";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private InfoProjectMapper projectMapper;

    @Autowired
    private InfoProjectTemplateMapper projectTemplateMapper;

    @Autowired
    private InfoProjectSpaceDirectoryMapper spaceDirectoryMapper;

    @Override
    public List<InfoProjectSpaceDirectory> selectInfoProjectSpaceDirectoryList(Long projectId)
    {
        ensureProjectExists(projectId);
        return spaceDirectoryMapper.selectInfoProjectSpaceDirectoryListByProjectId(projectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int initProjectSpace(Long projectId, String operator)
    {
        InfoProject project = ensureProjectExists(projectId);
        if (spaceDirectoryMapper.countByProjectId(projectId) > 0)
        {
            throw new ServiceException("Project space already initialized");
        }

        JsonNode rootNodes = loadDirectorySnapshot(project);
        if (rootNodes == null || !rootNodes.isArray() || rootNodes.size() == 0)
        {
            throw new ServiceException("Project template snapshot is empty");
        }

        int insertedRows = 0;
        int sortOrder = 0;
        Iterator<JsonNode> iterator = rootNodes.elements();
        while (iterator.hasNext())
        {
            JsonNode node = iterator.next();
            insertedRows += insertDirectoryNode(project, 0L, null, 1, node, operator, sortOrder++);
        }

        InfoProject updateProject = new InfoProject();
        updateProject.setProjectId(projectId);
        updateProject.setSpaceInitStatus(SPACE_STATUS_INITIALIZED);
        updateProject.setUpdateBy(operator);
        projectMapper.updateInfoProject(updateProject);
        return insertedRows;
    }

    @Override
    public int insertCustomDirectory(Long projectId, InfoProjectSpaceDirectory directory)
    {
        InfoProject project = ensureProjectExists(projectId);
        if (directory == null)
        {
            throw new ServiceException("Directory payload cannot be null");
        }
        if (StringUtils.isEmpty(directory.getDirectoryName()))
        {
            throw new ServiceException("Directory name cannot be empty");
        }

        String directoryCode = StringUtils.isEmpty(directory.getDirectoryCode()) ? generateCustomCode() : directory.getDirectoryCode();
        if (spaceDirectoryMapper.countDirectoryCode(projectId, directoryCode) > 0)
        {
            throw new ServiceException("Directory code already exists in project");
        }

        Long parentId = directory.getParentId() == null ? 0L : directory.getParentId();
        int directoryLevel = 1;
        String parentPath = null;
        if (parentId > 0)
        {
            InfoProjectSpaceDirectory parent = spaceDirectoryMapper.selectInfoProjectSpaceDirectoryById(parentId);
            if (parent == null || !projectId.equals(parent.getProjectId()))
            {
                throw new ServiceException("Parent directory does not belong to project");
            }
            directoryLevel = parent.getDirectoryLevel() == null ? 1 : parent.getDirectoryLevel() + 1;
            parentPath = parent.getFullPath();
        }

        directory.setProjectId(projectId);
        directory.setParentId(parentId);
        directory.setTemplateId(project.getTemplateId());
        directory.setDirectoryCode(directoryCode);
        directory.setDirectoryType(DIRECTORY_TYPE_CUSTOM);
        directory.setDirectoryLevel(directoryLevel);
        directory.setFullPath(buildFullPath(parentPath, directory.getDirectoryName()));
        directory.setRequiredFlag(defaultFlag(directory.getRequiredFlag(), FLAG_FALSE));
        directory.setComplianceStatus(defaultString(directory.getComplianceStatus(), STATUS_PENDING_CHECK));
        directory.setArchiveStatus(defaultString(directory.getArchiveStatus(), STATUS_UNARCHIVED));
        if (directory.getSortOrder() == null)
        {
            directory.setSortOrder(0);
        }
        return spaceDirectoryMapper.insertInfoProjectSpaceDirectory(directory);
    }

    private JsonNode loadDirectorySnapshot(InfoProject project)
    {
        String snapshot = project.getDirectoryTemplateJson();
        if (StringUtils.isEmpty(snapshot) && project.getTemplateId() != null)
        {
            InfoProjectTemplate template = projectTemplateMapper.selectInfoProjectTemplateById(project.getTemplateId());
            if (template != null)
            {
                snapshot = template.getDirectoryJson();
            }
        }
        if (StringUtils.isEmpty(snapshot))
        {
            return null;
        }
        try
        {
            return OBJECT_MAPPER.readTree(snapshot);
        }
        catch (Exception ex)
        {
            throw new ServiceException("Invalid project template snapshot JSON");
        }
    }

    private int insertDirectoryNode(InfoProject project, Long parentId, String parentPath, int level, JsonNode node,
        String operator, int sortOrder)
    {
        String directoryName = text(node, "directoryName", "name");
        if (StringUtils.isEmpty(directoryName))
        {
            throw new ServiceException("Directory name in template snapshot cannot be empty");
        }

        String directoryCode = text(node, "directoryCode", "code");
        if (StringUtils.isEmpty(directoryCode))
        {
            directoryCode = "AUTO-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        }
        if (spaceDirectoryMapper.countDirectoryCode(project.getProjectId(), directoryCode) > 0)
        {
            throw new ServiceException("Duplicate directory code in project snapshot: " + directoryCode);
        }

        InfoProjectSpaceDirectory directory = new InfoProjectSpaceDirectory();
        directory.setProjectId(project.getProjectId());
        directory.setParentId(parentId);
        directory.setTemplateId(project.getTemplateId());
        directory.setDirectoryCode(directoryCode);
        directory.setDirectoryName(directoryName);
        directory.setDirectoryLevel(level);
        directory.setDirectoryType(DIRECTORY_TYPE_STANDARD);
        directory.setFullPath(buildFullPath(parentPath, directoryName));
        directory.setRequiredFlag(parseRequiredFlag(node.get("requiredFlag")));
        directory.setComplianceStatus(STATUS_PENDING_CHECK);
        directory.setArchiveStatus(STATUS_UNARCHIVED);
        directory.setSortOrder(sortOrderValue(node, sortOrder));
        directory.setCreateBy(operator);
        spaceDirectoryMapper.insertInfoProjectSpaceDirectory(directory);
        if (directory.getDirectoryId() == null)
        {
            throw new ServiceException("Failed to generate directory id");
        }

        int insertedRows = 1;
        JsonNode children = node.get("children");
        if (children != null && children.isArray())
        {
            int childSort = 0;
            Iterator<JsonNode> childIterator = children.elements();
            while (childIterator.hasNext())
            {
                insertedRows += insertDirectoryNode(project, directory.getDirectoryId(), directory.getFullPath(), level + 1,
                    childIterator.next(), operator, childSort++);
            }
        }
        return insertedRows;
    }

    private int sortOrderValue(JsonNode node, int fallback)
    {
        JsonNode sortNode = node.get("sortOrder");
        if (sortNode == null || !sortNode.isNumber())
        {
            return fallback;
        }
        return sortNode.asInt();
    }

    private String parseRequiredFlag(JsonNode node)
    {
        if (node == null || node.isNull())
        {
            return FLAG_FALSE;
        }
        if (node.isBoolean())
        {
            return node.asBoolean() ? FLAG_TRUE : FLAG_FALSE;
        }
        String value = node.asText();
        if (FLAG_TRUE.equals(value) || "true".equalsIgnoreCase(value) || "Y".equalsIgnoreCase(value))
        {
            return FLAG_TRUE;
        }
        return FLAG_FALSE;
    }

    private String text(JsonNode node, String key1, String key2)
    {
        if (node == null)
        {
            return null;
        }
        JsonNode value = node.get(key1);
        if (value != null && !value.isNull() && StringUtils.isNotEmpty(value.asText()))
        {
            return value.asText();
        }
        value = node.get(key2);
        if (value != null && !value.isNull() && StringUtils.isNotEmpty(value.asText()))
        {
            return value.asText();
        }
        return null;
    }

    private String buildFullPath(String parentPath, String directoryName)
    {
        if (StringUtils.isEmpty(parentPath))
        {
            return "/" + directoryName;
        }
        if (parentPath.endsWith("/"))
        {
            return parentPath + directoryName;
        }
        return parentPath + "/" + directoryName;
    }

    private String defaultString(String value, String defaultValue)
    {
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }

    private String defaultFlag(String value, String defaultValue)
    {
        if (FLAG_TRUE.equals(value) || FLAG_FALSE.equals(value))
        {
            return value;
        }
        return defaultValue;
    }

    private String generateCustomCode()
    {
        return "CUSTOM-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
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
}
