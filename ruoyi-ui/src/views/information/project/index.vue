<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" @tab-click="handleTabChange">
      <el-tab-pane label="项目台账" name="project">
        <el-form ref="projectQueryForm" :model="projectQuery" size="small" :inline="true" label-width="80px">
          <el-form-item label="项目名称">
            <el-input v-model="projectQuery.projectName" placeholder="请输入项目名称" clearable @keyup.enter.native="loadProjects" />
          </el-form-item>
          <el-form-item label="项目状态">
            <el-select v-model="projectQuery.projectStatus" clearable placeholder="请选择">
              <el-option v-for="item in projectStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="mini" icon="el-icon-search" @click="loadProjects">搜索</el-button>
            <el-button size="mini" icon="el-icon-refresh" @click="resetProjectQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5"><el-button type="primary" plain size="mini" icon="el-icon-plus" @click="openProjectDialog()" v-hasPermi="['information:project:add']">新增项目</el-button></el-col>
          <el-col :span="1.5"><el-button type="success" plain size="mini" icon="el-icon-edit" :disabled="projectSingle" @click="openProjectDialog(selectedProject)" v-hasPermi="['information:project:edit']">修改</el-button></el-col>
          <el-col :span="1.5"><el-button type="danger" plain size="mini" icon="el-icon-delete" :disabled="projectMultiple" @click="removeProject()" v-hasPermi="['information:project:remove']">删除</el-button></el-col>
        </el-row>

        <el-table v-loading="projectLoading" :data="projectList" @selection-change="handleProjectSelection">
          <el-table-column type="selection" width="55" />
          <el-table-column label="项目编码" prop="projectCode" min-width="120" />
          <el-table-column label="项目名称" prop="projectName" min-width="180" />
          <el-table-column label="项目类型" prop="projectType" width="120" />
          <el-table-column label="项目状态" prop="projectStatus" width="120">
            <template slot-scope="scope">
              <el-tag size="mini">{{ scope.row.projectStatus }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="项目阶段" prop="projectPhase" width="120" />
          <el-table-column label="项目经理" prop="projectManager" width="120" />
          <el-table-column label="验收状态" prop="acceptanceStatus" width="120" />
          <el-table-column label="计划开始" prop="planStartDate" width="170">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.planStartDate) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="计划结束" prop="planEndDate" width="170">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.planEndDate) }}</span>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="projectTotal > 0" :total="projectTotal" :page.sync="projectQuery.pageNum" :limit.sync="projectQuery.pageSize" @pagination="loadProjects" />
      </el-tab-pane>

      <el-tab-pane label="模板管理" name="template">
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5"><el-button type="primary" plain size="mini" icon="el-icon-plus" @click="openTemplateDialog()" v-hasPermi="['information:projectTemplate:add']">新增模板</el-button></el-col>
          <el-col :span="1.5"><el-button type="success" plain size="mini" icon="el-icon-edit" :disabled="templateSingle" @click="openTemplateDialog(selectedTemplate)" v-hasPermi="['information:projectTemplate:edit']">修改</el-button></el-col>
          <el-col :span="1.5"><el-button type="danger" plain size="mini" icon="el-icon-delete" :disabled="templateMultiple" @click="removeTemplate()" v-hasPermi="['information:projectTemplate:remove']">删除</el-button></el-col>
        </el-row>

        <el-table v-loading="templateLoading" :data="templateList" @selection-change="handleTemplateSelection">
          <el-table-column type="selection" width="55" />
          <el-table-column label="模板名称" prop="templateName" min-width="180" />
          <el-table-column label="项目类型" prop="projectType" width="120" />
          <el-table-column label="阶段名称" prop="phaseName" width="120" />
          <el-table-column label="版本号" prop="versionNo" width="100" />
          <el-table-column label="状态" prop="status" width="100" />
        </el-table>
        <pagination v-show="templateTotal > 0" :total="templateTotal" :page.sync="templateQuery.pageNum" :limit.sync="templateQuery.pageSize" @pagination="loadTemplates" />
      </el-tab-pane>

      <el-tab-pane label="验收工单" name="order">
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5"><el-button type="primary" plain size="mini" icon="el-icon-plus" @click="openOrderDialog()" v-hasPermi="['information:projectOrder:add']">发起验收</el-button></el-col>
          <el-col :span="1.5"><el-button type="warning" plain size="mini" icon="el-icon-finished" :disabled="orderSingle" @click="openApproveDialog(selectedOrder)" v-hasPermi="['information:projectOrder:approve']">审批</el-button></el-col>
          <el-col :span="1.5"><el-button type="success" plain size="mini" icon="el-icon-check" :disabled="orderSingle" @click="openExecuteDialog(selectedOrder)" v-hasPermi="['information:projectOrder:execute']">执行</el-button></el-col>
        </el-row>

        <el-table v-loading="orderLoading" :data="orderList" @selection-change="handleOrderSelection">
          <el-table-column type="selection" width="55" />
          <el-table-column label="工单号" prop="workOrderNo" min-width="180" />
          <el-table-column label="工单标题" prop="requestTitle" min-width="180" />
          <el-table-column label="项目名称" prop="projectName" min-width="160" />
          <el-table-column label="状态" prop="orderStatus" width="130">
            <template slot-scope="scope">
              <el-tag size="mini">{{ scope.row.orderStatus }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="申请人" prop="applicantName" width="120" />
          <el-table-column label="提交时间" prop="submittedTime" width="170">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.submittedTime) }}</span>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="orderTotal > 0" :total="orderTotal" :page.sync="orderQuery.pageNum" :limit.sync="orderQuery.pageSize" @pagination="loadOrders" />
      </el-tab-pane>
      <el-tab-pane label="权限配置" name="permission">
        <el-alert
          v-if="!permissionProjectId"
          title="请先在项目台账中勾选一个项目"
          type="info"
          :closable="false"
          show-icon />

        <div v-else>
          <el-row :gutter="10" class="mb8">
            <el-col :span="8">
              <span>当前项目：</span>
              <el-tag size="mini" type="success">{{ permissionProjectName || permissionProjectId }}</el-tag>
            </el-col>
            <el-col :span="16" class="text-right">
              <el-button size="mini" icon="el-icon-refresh" @click="loadPermissions">刷新</el-button>
              <el-button size="mini" icon="el-icon-s-operation" @click="useSelectedProjectForPermission">使用当前勾选项目</el-button>
              <el-button type="primary" plain size="mini" icon="el-icon-plus" @click="openPermissionDialog()" v-hasPermi="['information:project:permission']">新增授权</el-button>
              <el-button type="success" plain size="mini" icon="el-icon-edit" :disabled="permissionSingle" @click="openPermissionDialog(selectedPermission)" v-hasPermi="['information:project:permission']">修改</el-button>
              <el-button type="danger" plain size="mini" icon="el-icon-delete" :disabled="permissionSingle" @click="removePermission()" v-hasPermi="['information:project:permission']">删除</el-button>
            </el-col>
          </el-row>

          <el-row :gutter="12" class="mb8">
            <el-col :span="6"><el-card shadow="never"><div slot="header">项目级</div><div>{{ permissionMatrix.projectPermissions.length }}</div></el-card></el-col>
            <el-col :span="6"><el-card shadow="never"><div slot="header">目录级</div><div>{{ permissionMatrix.directoryPermissions.length }}</div></el-card></el-col>
            <el-col :span="6"><el-card shadow="never"><div slot="header">文档级</div><div>{{ permissionMatrix.documentPermissions.length }}</div></el-card></el-col>
            <el-col :span="6"><el-card shadow="never"><div slot="header">推荐角色</div><div>{{ permissionMatrix.defaultRoleSuggestions.length }}</div></el-card></el-col>
          </el-row>

          <el-table v-loading="permissionLoading" :data="permissionList" @selection-change="handlePermissionSelection">
            <el-table-column type="selection" width="55" />
            <el-table-column label="范围" prop="scopeType" width="120" />
            <el-table-column label="范围ID" prop="scopeId" width="120" />
            <el-table-column label="对象类型" prop="targetType" width="120" />
            <el-table-column label="对象标识" prop="targetKey" min-width="160" />
            <el-table-column label="查看" prop="canView" width="80" />
            <el-table-column label="编辑" prop="canEdit" width="80" />
            <el-table-column label="下载" prop="canDownload" width="80" />
            <el-table-column label="删除" prop="canDelete" width="80" />
            <el-table-column label="继承" prop="inheritFlag" width="80" />
            <el-table-column label="备注" prop="remark" min-width="180" />
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog :title="projectDialogTitle" :visible.sync="projectOpen" width="980px" append-to-body>
      <el-form ref="projectFormRef" :model="projectForm" :rules="projectRules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="项目编码" prop="projectCode"><el-input v-model="projectForm.projectCode" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="项目名称" prop="projectName"><el-input v-model="projectForm.projectName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="项目类型"><el-input v-model="projectForm.projectType" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="项目状态"><el-select v-model="projectForm.projectStatus"><el-option v-for="item in projectStatusOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="项目阶段"><el-input v-model="projectForm.projectPhase" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="项目经理"><el-input v-model="projectForm.projectManager" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="业主负责人"><el-input v-model="projectForm.ownerLeader" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="实施厂商"><el-input v-model="projectForm.vendorName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="厂商负责人"><el-input v-model="projectForm.vendorOwner" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="门户负责人"><el-input v-model="projectForm.portalOwner" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="资源负责人"><el-input v-model="projectForm.resourceOwner" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="数据负责人"><el-input v-model="projectForm.dataOwner" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="安全负责人"><el-input v-model="projectForm.securityOwner" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="计划开始"><el-date-picker v-model="projectForm.planStartDate" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="计划结束"><el-date-picker v-model="projectForm.planEndDate" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="目标用户"><el-input type="textarea" :rows="2" v-model="projectForm.targetUsers" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="标准目录"><el-input type="textarea" :rows="3" v-model="projectForm.directoryTemplateJson" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="自定义目录"><el-input type="textarea" :rows="3" v-model="projectForm.customDirectoryJson" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitProject">确定</el-button><el-button @click="projectOpen = false">取消</el-button></div>
    </el-dialog>

    <el-dialog :title="templateDialogTitle" :visible.sync="templateOpen" width="760px" append-to-body>
      <el-form ref="templateFormRef" :model="templateForm" :rules="templateRules" label-width="100px">
        <el-form-item label="模板名称" prop="templateName"><el-input v-model="templateForm.templateName" /></el-form-item>
        <el-form-item label="项目类型"><el-input v-model="templateForm.projectType" /></el-form-item>
        <el-form-item label="阶段名称"><el-input v-model="templateForm.phaseName" /></el-form-item>
        <el-form-item label="版本号"><el-input v-model="templateForm.versionNo" /></el-form-item>
        <el-form-item label="状态"><el-input v-model="templateForm.status" /></el-form-item>
        <el-form-item label="目录JSON"><el-input type="textarea" :rows="4" v-model="templateForm.directoryJson" /></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitTemplate">确定</el-button><el-button @click="templateOpen = false">取消</el-button></div>
    </el-dialog>

    <el-dialog title="验收工单" :visible.sync="orderOpen" width="760px" append-to-body>
      <el-form ref="orderFormRef" :model="orderForm" :rules="orderRules" label-width="110px">
        <el-form-item label="工单标题" prop="requestTitle"><el-input v-model="orderForm.requestTitle" /></el-form-item>
        <el-form-item label="关联项目ID" prop="projectId"><el-input v-model="orderForm.projectId" /></el-form-item>
        <el-form-item label="申请人"><el-input v-model="orderForm.applicantName" /></el-form-item>
        <el-form-item label="验收说明"><el-input type="textarea" :rows="4" v-model="orderForm.requestReason" /></el-form-item>
        <el-form-item label="验收资料JSON"><el-input type="textarea" :rows="4" v-model="orderForm.requestPayloadJson" /></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitOrder">确定</el-button><el-button @click="orderOpen = false">取消</el-button></div>
    </el-dialog>

    <el-dialog title="审批工单" :visible.sync="approveOpen" width="520px" append-to-body>
      <el-form label-width="100px">
        <el-form-item label="审批结果">
          <el-radio-group v-model="approveForm.approved">
            <el-radio :label="true">通过</el-radio>
            <el-radio :label="false">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见"><el-input type="textarea" :rows="3" v-model="approveForm.approvalComment" /></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitApprove">确定</el-button><el-button @click="approveOpen = false">取消</el-button></div>
    </el-dialog>

    <el-dialog title="执行工单" :visible.sync="executeOpen" width="520px" append-to-body>
      <el-form label-width="100px">
        <el-form-item label="执行人"><el-input v-model="executeForm.executorName" /></el-form-item>
        <el-form-item label="目标状态"><el-input v-model="executeForm.targetStatus" placeholder="如 ACCEPTED / COMPLETED" /></el-form-item>
        <el-form-item label="执行结果"><el-input type="textarea" :rows="3" v-model="executeForm.executionResult" /></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitExecute">确定</el-button><el-button @click="executeOpen = false">取消</el-button></div>
    </el-dialog>
    <el-dialog :title="permissionDialogTitle" :visible.sync="permissionOpen" width="720px" append-to-body>
      <el-form ref="permissionFormRef" :model="permissionForm" :rules="permissionRules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="授权范围" prop="scopeType">
              <el-select v-model="permissionForm.scopeType" placeholder="请选择">
                <el-option label="PROJECT" value="PROJECT" />
                <el-option label="DIRECTORY" value="DIRECTORY" />
                <el-option label="DOCUMENT" value="DOCUMENT" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="范围ID"><el-input v-model="permissionForm.scopeId" :disabled="permissionForm.scopeType === 'PROJECT'" placeholder="PROJECT 范围可留空" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="对象类型" prop="targetType">
              <el-select v-model="permissionForm.targetType" placeholder="请选择">
                <el-option label="USER" value="USER" />
                <el-option label="ROLE" value="ROLE" />
                <el-option label="TEAM" value="TEAM" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="对象标识" prop="targetKey"><el-input v-model="permissionForm.targetKey" placeholder="如 project_observer / u1001" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="可查看"><el-switch v-model="permissionForm.canView" active-value="1" inactive-value="0" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="可编辑"><el-switch v-model="permissionForm.canEdit" active-value="1" inactive-value="0" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="可下载"><el-switch v-model="permissionForm.canDownload" active-value="1" inactive-value="0" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="可删除"><el-switch v-model="permissionForm.canDelete" active-value="1" inactive-value="0" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="继承上级"><el-switch v-model="permissionForm.inheritFlag" active-value="1" inactive-value="0" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="备注"><el-input type="textarea" :rows="3" v-model="permissionForm.remark" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitPermission">确定</el-button><el-button @click="permissionOpen = false">取消</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import {
  addProject,
  addProjectOrder,
  addProjectPermission,
  addProjectTemplate,
  approveProjectOrder,
  delProjectPermission,
  delProject,
  delProjectTemplate,
  executeProjectOrder,
  getProject,
  getProjectPermissionMatrix,
  getProjectTemplate,
  listProject,
  listProjectOrder,
  listProjectPermission,
  listProjectTemplate,
  updateProjectPermission,
  updateProject,
  updateProjectOrder,
  updateProjectTemplate
} from '@/api/information/project'

export default {
  name: 'InformationProject',
  data() {
    return {
      activeTab: 'project',
      projectLoading: false,
      templateLoading: false,
      orderLoading: false,
      permissionLoading: false,
      projectList: [],
      templateList: [],
      orderList: [],
      permissionList: [],
      projectTotal: 0,
      templateTotal: 0,
      orderTotal: 0,
      projectIds: [],
      templateIds: [],
      selectedProject: null,
      selectedTemplate: null,
      selectedOrder: null,
      selectedPermission: null,
      projectSingle: true,
      projectMultiple: true,
      templateSingle: true,
      templateMultiple: true,
      orderSingle: true,
      permissionSingle: true,
      projectOpen: false,
      templateOpen: false,
      orderOpen: false,
      approveOpen: false,
      executeOpen: false,
      permissionOpen: false,
      projectDialogTitle: '',
      templateDialogTitle: '',
      permissionDialogTitle: '',
      permissionProjectId: undefined,
      permissionProjectName: '',
      permissionMatrix: {
        projectPermissions: [],
        directoryPermissions: [],
        documentPermissions: [],
        defaultRoleSuggestions: []
      },
      projectStatusOptions: [
        { label: '草稿', value: 'DRAFT' },
        { label: '执行中', value: 'RUNNING' },
        { label: '待验收', value: 'PENDING_ACCEPTANCE' },
        { label: '已完成', value: 'COMPLETED' }
      ],
      projectQuery: { pageNum: 1, pageSize: 10, projectName: undefined, projectStatus: undefined },
      templateQuery: { pageNum: 1, pageSize: 10 },
      orderQuery: { pageNum: 1, pageSize: 10 },
      permissionQuery: {},
      projectForm: {},
      templateForm: {},
      orderForm: {},
      permissionForm: {},
      approveForm: { approved: true, approvalComment: '' },
      executeForm: { executorName: '', targetStatus: 'ACCEPTED', executionResult: '' },
      projectRules: {
        projectCode: [{ required: true, message: '请输入项目编码', trigger: 'blur' }],
        projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }]
      },
      templateRules: {
        templateName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }]
      },
      orderRules: {
        requestTitle: [{ required: true, message: '请输入工单标题', trigger: 'blur' }],
        projectId: [{ required: true, message: '请输入关联项目ID', trigger: 'blur' }]
      },
      permissionRules: {
        scopeType: [{ required: true, message: '请选择授权范围', trigger: 'change' }],
        targetType: [{ required: true, message: '请选择对象类型', trigger: 'change' }],
        targetKey: [{ required: true, message: '请输入对象标识', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.loadProjects()
    this.loadTemplates()
    this.loadOrders()
  },
  methods: {
    handleTabChange() {
      if (this.activeTab === 'project') {
        this.loadProjects()
      } else if (this.activeTab === 'template') {
        this.loadTemplates()
      } else if (this.activeTab === 'permission') {
        this.loadPermissions()
      } else {
        this.loadOrders()
      }
    },
    loadProjects() {
      this.projectLoading = true
      listProject(this.projectQuery).then(response => {
        this.projectList = response.rows
        this.projectTotal = response.total
        this.projectLoading = false
      }).catch(() => {
        this.projectLoading = false
      })
    },
    resetProjectQuery() {
      this.resetForm('projectQueryForm')
      this.loadProjects()
    },
    handleProjectSelection(selection) {
      this.projectIds = selection.map(item => item.projectId)
      this.selectedProject = selection[0]
      this.projectSingle = selection.length !== 1
      this.projectMultiple = !selection.length
      if (selection.length === 1) {
        this.permissionProjectId = selection[0].projectId
        this.permissionProjectName = selection[0].projectName
        if (this.activeTab === 'permission') {
          this.loadPermissions()
        }
      }
    },
    resetProjectForm() {
      this.projectForm = {
        projectId: undefined,
        projectStatus: 'DRAFT',
        acceptanceStatus: 'PENDING',
        spaceInitStatus: 'PENDING',
        archiveStatus: 'UNARCHIVED',
        documentCompletionRate: 0
      }
      this.resetForm('projectFormRef')
    },
    openProjectDialog(row) {
      this.resetProjectForm()
      if (row && row.projectId) {
        getProject(row.projectId).then(response => {
          this.projectForm = response.data
          this.projectDialogTitle = '修改项目'
          this.projectOpen = true
        })
        return
      }
      this.projectDialogTitle = '新增项目'
      this.projectOpen = true
    },
    submitProject() {
      this.$refs.projectFormRef.validate(valid => {
        if (!valid) {
          return
        }
        const action = this.projectForm.projectId ? updateProject : addProject
        action(this.projectForm).then(() => {
          this.$modal.msgSuccess(this.projectForm.projectId ? '修改成功' : '新增成功')
          this.projectOpen = false
          this.loadProjects()
        })
      })
    },
    removeProject(row) {
      const projectId = row ? row.projectId : this.projectIds.join(',')
      this.$modal.confirm(`确认删除项目 ${projectId} 吗？`).then(() => {
        return delProject(projectId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.loadProjects()
      }).catch(() => {})
    },
    loadTemplates() {
      this.templateLoading = true
      listProjectTemplate(this.templateQuery).then(response => {
        this.templateList = response.rows
        this.templateTotal = response.total
        this.templateLoading = false
      }).catch(() => {
        this.templateLoading = false
      })
    },
    handleTemplateSelection(selection) {
      this.templateIds = selection.map(item => item.templateId)
      this.selectedTemplate = selection[0]
      this.templateSingle = selection.length !== 1
      this.templateMultiple = !selection.length
    },
    resetTemplateForm() {
      this.templateForm = {
        templateId: undefined,
        status: 'ACTIVE'
      }
      this.resetForm('templateFormRef')
    },
    openTemplateDialog(row) {
      this.resetTemplateForm()
      if (row && row.templateId) {
        getProjectTemplate(row.templateId).then(response => {
          this.templateForm = response.data
          this.templateDialogTitle = '修改模板'
          this.templateOpen = true
        })
        return
      }
      this.templateDialogTitle = '新增模板'
      this.templateOpen = true
    },
    submitTemplate() {
      this.$refs.templateFormRef.validate(valid => {
        if (!valid) {
          return
        }
        const action = this.templateForm.templateId ? updateProjectTemplate : addProjectTemplate
        action(this.templateForm).then(() => {
          this.$modal.msgSuccess(this.templateForm.templateId ? '修改成功' : '新增成功')
          this.templateOpen = false
          this.loadTemplates()
        })
      })
    },
    removeTemplate(row) {
      const templateId = row ? row.templateId : this.templateIds.join(',')
      this.$modal.confirm(`确认删除模板 ${templateId} 吗？`).then(() => {
        return delProjectTemplate(templateId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.loadTemplates()
      }).catch(() => {})
    },
    loadOrders() {
      this.orderLoading = true
      listProjectOrder(this.orderQuery).then(response => {
        this.orderList = response.rows
        this.orderTotal = response.total
        this.orderLoading = false
      }).catch(() => {
        this.orderLoading = false
      })
    },
    handleOrderSelection(selection) {
      this.selectedOrder = selection[0]
      this.orderSingle = selection.length !== 1
    },
    resetOrderForm() {
      this.orderForm = {
        workOrderId: undefined,
        requestType: 'ACCEPTANCE',
        orderStatus: 'PENDING'
      }
      this.resetForm('orderFormRef')
    },
    openOrderDialog() {
      this.resetOrderForm()
      if (this.selectedProject && this.selectedProject.projectId) {
        this.orderForm.projectId = this.selectedProject.projectId
      }
      this.orderOpen = true
    },
    submitOrder() {
      this.$refs.orderFormRef.validate(valid => {
        if (!valid) {
          return
        }
        const action = this.orderForm.workOrderId ? updateProjectOrder : addProjectOrder
        action(this.orderForm).then(() => {
          this.$modal.msgSuccess('提交成功')
          this.orderOpen = false
          this.loadOrders()
        })
      })
    },
    openApproveDialog(row) {
      if (!row) {
        return
      }
      this.selectedOrder = row
      this.approveForm = { approved: true, approvalComment: '' }
      this.approveOpen = true
    },
    submitApprove() {
      approveProjectOrder(this.selectedOrder.workOrderId, this.approveForm).then(() => {
        this.$modal.msgSuccess('审批成功')
        this.approveOpen = false
        this.loadOrders()
      })
    },
    openExecuteDialog(row) {
      if (!row) {
        return
      }
      this.selectedOrder = row
      this.executeForm = { executorName: '', targetStatus: 'ACCEPTED', executionResult: '' }
      this.executeOpen = true
    },
    submitExecute() {
      executeProjectOrder(this.selectedOrder.workOrderId, this.executeForm).then(() => {
        this.$modal.msgSuccess('执行成功')
        this.executeOpen = false
        this.loadOrders()
        this.loadProjects()
      })
    },
    useSelectedProjectForPermission() {
      if (!this.selectedProject || !this.selectedProject.projectId) {
        this.$modal.msgWarning('请先在项目台账中勾选一个项目')
        return
      }
      this.permissionProjectId = this.selectedProject.projectId
      this.permissionProjectName = this.selectedProject.projectName
      this.loadPermissions()
    },
    loadPermissions() {
      if (!this.permissionProjectId) {
        this.permissionList = []
        this.permissionMatrix = {
          projectPermissions: [],
          directoryPermissions: [],
          documentPermissions: [],
          defaultRoleSuggestions: []
        }
        return
      }
      this.permissionLoading = true
      Promise.all([
        listProjectPermission(this.permissionProjectId, this.permissionQuery),
        getProjectPermissionMatrix(this.permissionProjectId)
      ]).then(([listResp, matrixResp]) => {
        this.permissionList = listResp.data || []
        this.permissionMatrix = matrixResp.data || {
          projectPermissions: [],
          directoryPermissions: [],
          documentPermissions: [],
          defaultRoleSuggestions: []
        }
        this.permissionLoading = false
      }).catch(() => {
        this.permissionLoading = false
      })
    },
    handlePermissionSelection(selection) {
      this.selectedPermission = selection[0]
      this.permissionSingle = selection.length !== 1
    },
    resetPermissionForm() {
      this.permissionForm = {
        permissionId: undefined,
        scopeType: 'PROJECT',
        scopeId: undefined,
        targetType: 'ROLE',
        targetKey: '',
        canView: '1',
        canEdit: '0',
        canDownload: '1',
        canDelete: '0',
        inheritFlag: '1',
        remark: ''
      }
      this.resetForm('permissionFormRef')
    },
    openPermissionDialog(row) {
      if (!this.permissionProjectId) {
        this.$modal.msgWarning('请先选择项目')
        return
      }
      this.resetPermissionForm()
      if (row && row.permissionId) {
        this.permissionForm = Object.assign({}, row)
        this.permissionDialogTitle = '修改授权'
      } else {
        this.permissionDialogTitle = '新增授权'
      }
      this.permissionOpen = true
    },
    submitPermission() {
      this.$refs.permissionFormRef.validate(valid => {
        if (!valid) {
          return
        }
        const payload = Object.assign({}, this.permissionForm)
        if (payload.scopeType === 'PROJECT') {
          payload.scopeId = undefined
        }
        const request = payload.permissionId
          ? updateProjectPermission(this.permissionProjectId, payload.permissionId, payload)
          : addProjectPermission(this.permissionProjectId, payload)
        Promise.resolve(request).then(() => {
          this.$modal.msgSuccess(payload.permissionId ? '修改成功' : '新增成功')
          this.permissionOpen = false
          this.loadPermissions()
        })
      })
    },
    removePermission(row) {
      const target = row || this.selectedPermission
      if (!target || !target.permissionId) {
        this.$modal.msgWarning('请选择要删除的授权')
        return
      }
      this.$modal.confirm(`确认删除授权对象 ${target.targetKey} 吗？`).then(() => {
        return delProjectPermission(this.permissionProjectId, target.permissionId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.loadPermissions()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.text-right {
  text-align: right;
}
</style>
