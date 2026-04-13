<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" @tab-click="handleTabChange">
      <el-tab-pane label="资源台账" name="resource">
        <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
          <el-form-item label="资源编码">
            <el-input v-model="queryParams.resourceCode" placeholder="请输入资源编码" clearable @keyup.enter.native="getResourceList" />
          </el-form-item>
          <el-form-item label="资源名称">
            <el-input v-model="queryParams.resourceName" placeholder="请输入资源名称" clearable @keyup.enter.native="getResourceList" />
          </el-form-item>
          <el-form-item label="资源状态">
            <el-select v-model="queryParams.resourceStatus" placeholder="请选择" clearable>
              <el-option v-for="item in resourceStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="监控状态">
            <el-select v-model="queryParams.monitorStatus" placeholder="请选择" clearable>
              <el-option v-for="item in monitorStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="责任人">
            <el-input v-model="queryParams.ownerName" placeholder="请输入责任人" clearable @keyup.enter.native="getResourceList" />
          </el-form-item>
          <el-form-item label="维护人">
            <el-input v-model="queryParams.maintainerName" placeholder="请输入维护人" clearable @keyup.enter.native="getResourceList" />
          </el-form-item>
          <el-form-item label="IP地址">
            <el-input v-model="queryParams.ipAddress" placeholder="请输入IP地址" clearable @keyup.enter.native="getResourceList" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="mini" icon="el-icon-search" @click="getResourceList">搜索</el-button>
            <el-button size="mini" icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain size="mini" icon="el-icon-plus" @click="handleAddResource" v-hasPermi="['information:resource:add']">新增资源</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain size="mini" icon="el-icon-edit" :disabled="resourceSingle" @click="handleEditResource()" v-hasPermi="['information:resource:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain size="mini" icon="el-icon-delete" :disabled="resourceMultiple" @click="handleDeleteResource()" v-hasPermi="['information:resource:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button size="mini" plain icon="el-icon-view" :disabled="resourceSingle" @click="handleViewResource">详情</el-button>
          </el-col>
        </el-row>

        <el-table v-loading="resourceLoading" :data="resourceList" @selection-change="handleResourceSelection">
          <el-table-column type="selection" width="55" />
          <el-table-column label="资源编码" prop="resourceCode" min-width="120" />
          <el-table-column label="资源名称" prop="resourceName" min-width="160" />
          <el-table-column label="资源类型" prop="resourceType" width="120" />
          <el-table-column label="资源状态" prop="resourceStatus" width="140">
            <template slot-scope="scope">
              <el-tag size="mini">{{ formatLabel(scope.row.resourceStatus, resourceStatusOptions) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="监控状态" prop="monitorStatus" width="120">
            <template slot-scope="scope">
              <el-tag size="mini" :type="monitorTagType(scope.row.monitorStatus)">
                {{ formatLabel(scope.row.monitorStatus, monitorStatusOptions) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="项目名称" prop="projectName" min-width="140" />
          <el-table-column label="责任人" prop="ownerName" width="120" />
          <el-table-column label="维护人" prop="maintainerName" width="120" />
          <el-table-column label="IP地址" prop="ipAddress" width="140" />
          <el-table-column label="交付时间" prop="deliveryTime" width="170">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.deliveryTime) }}</span>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="resourceTotal > 0" :total="resourceTotal" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getResourceList" />
      </el-tab-pane>

      <el-tab-pane label="资源工单" name="order">
        <el-form ref="orderQueryForm" :model="orderQuery" size="small" :inline="true" label-width="80px">
          <el-form-item label="工单号">
            <el-input v-model="orderQuery.workOrderNo" placeholder="请输入工单号" clearable @keyup.enter.native="getOrderList" />
          </el-form-item>
          <el-form-item label="工单类型">
            <el-select v-model="orderQuery.requestType" placeholder="请选择" clearable>
              <el-option v-for="item in requestTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="工单状态">
            <el-select v-model="orderQuery.orderStatus" placeholder="请选择" clearable>
              <el-option v-for="item in orderStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="资源名称">
            <el-input v-model="orderQuery.subjectName" placeholder="请输入资源名称" clearable @keyup.enter.native="getOrderList" />
          </el-form-item>
          <el-form-item label="申请人">
            <el-input v-model="orderQuery.applicantName" placeholder="请输入申请人" clearable @keyup.enter.native="getOrderList" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="mini" icon="el-icon-search" @click="getOrderList">搜索</el-button>
            <el-button size="mini" icon="el-icon-refresh" @click="resetOrderQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain size="mini" icon="el-icon-plus" @click="handleAddOrder" v-hasPermi="['information:resourceOrder:add']">发起工单</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain size="mini" icon="el-icon-finished" :disabled="orderSingle" @click="handleApproveOrder" v-hasPermi="['information:resourceOrder:approve']">审批</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain size="mini" icon="el-icon-check" :disabled="orderSingle" @click="handleExecuteOrder" v-hasPermi="['information:resourceOrder:execute']">执行</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain size="mini" icon="el-icon-delete" :disabled="orderSingle" @click="handleDeleteOrder" v-hasPermi="['information:resourceOrder:remove']">删除</el-button>
          </el-col>
        </el-row>

        <el-table v-loading="orderLoading" :data="orderList" @selection-change="handleOrderSelection">
          <el-table-column type="selection" width="55" />
          <el-table-column label="工单号" prop="workOrderNo" min-width="180" />
          <el-table-column label="标题" prop="requestTitle" min-width="180" />
          <el-table-column label="资源名称" prop="subjectName" min-width="160" />
          <el-table-column label="类型" prop="requestType" width="110">
            <template slot-scope="scope">
              <span>{{ formatLabel(scope.row.requestType, requestTypeOptions) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" prop="orderStatus" width="130">
            <template slot-scope="scope">
              <el-tag size="mini">{{ formatLabel(scope.row.orderStatus, orderStatusOptions) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="申请人" prop="applicantName" width="120" />
          <el-table-column label="审批人" prop="approverName" width="120" />
          <el-table-column label="执行人" prop="executorName" width="120" />
          <el-table-column label="提交时间" prop="submittedTime" width="170">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.submittedTime) }}</span>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="orderTotal > 0" :total="orderTotal" :page.sync="orderQuery.pageNum" :limit.sync="orderQuery.pageSize" @pagination="getOrderList" />
      </el-tab-pane>
    </el-tabs>

    <el-dialog :title="resourceDialogTitle" :visible.sync="resourceOpen" width="960px" append-to-body>
      <el-form ref="resourceFormRef" :model="resourceForm" :rules="resourceRules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="资源编码" prop="resourceCode"><el-input v-model="resourceForm.resourceCode" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="资源名称" prop="resourceName"><el-input v-model="resourceForm.resourceName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="资源类型" prop="resourceType"><el-input v-model="resourceForm.resourceType" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="资源状态"><el-select v-model="resourceForm.resourceStatus"><el-option v-for="item in resourceStatusOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="监控状态"><el-select v-model="resourceForm.monitorStatus"><el-option v-for="item in monitorStatusOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="项目ID"><el-input v-model="resourceForm.projectId" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="责任人"><el-input v-model="resourceForm.ownerName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="维护人"><el-input v-model="resourceForm.maintainerName" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="CPU核数"><el-input-number v-model="resourceForm.cpuCores" :min="0" controls-position="right" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="内存(GB)"><el-input-number v-model="resourceForm.memoryGb" :min="0" controls-position="right" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="存储(GB)"><el-input-number v-model="resourceForm.storageGb" :min="0" controls-position="right" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="操作系统"><el-input v-model="resourceForm.osName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="IP地址"><el-input v-model="resourceForm.ipAddress" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="软件名称"><el-input v-model="resourceForm.softwareName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="软件版本"><el-input v-model="resourceForm.softwareVersion" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="授权数"><el-input-number v-model="resourceForm.licenseCount" :min="0" controls-position="right" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="交付时间"><el-date-picker v-model="resourceForm.deliveryTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="关联说明"><el-input type="textarea" :rows="2" v-model="resourceForm.relationSummary" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="性能说明"><el-input type="textarea" :rows="2" v-model="resourceForm.performanceSummary" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitResource">确定</el-button>
        <el-button @click="resourceOpen = false">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="资源详情" :visible.sync="detailOpen" width="760px" append-to-body>
      <el-descriptions v-if="detailForm" :column="2" border>
        <el-descriptions-item label="资源编码">{{ detailForm.resourceCode }}</el-descriptions-item>
        <el-descriptions-item label="资源名称">{{ detailForm.resourceName }}</el-descriptions-item>
        <el-descriptions-item label="资源类型">{{ detailForm.resourceType }}</el-descriptions-item>
        <el-descriptions-item label="资源状态">{{ formatLabel(detailForm.resourceStatus, resourceStatusOptions) }}</el-descriptions-item>
        <el-descriptions-item label="监控状态">{{ formatLabel(detailForm.monitorStatus, monitorStatusOptions) }}</el-descriptions-item>
        <el-descriptions-item label="项目名称">{{ detailForm.projectName }}</el-descriptions-item>
        <el-descriptions-item label="责任人">{{ detailForm.ownerName }}</el-descriptions-item>
        <el-descriptions-item label="维护人">{{ detailForm.maintainerName }}</el-descriptions-item>
        <el-descriptions-item label="CPU核数">{{ detailForm.cpuCores }}</el-descriptions-item>
        <el-descriptions-item label="内存(GB)">{{ detailForm.memoryGb }}</el-descriptions-item>
        <el-descriptions-item label="存储(GB)">{{ detailForm.storageGb }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ detailForm.ipAddress }}</el-descriptions-item>
        <el-descriptions-item label="软件名称">{{ detailForm.softwareName }}</el-descriptions-item>
        <el-descriptions-item label="软件版本">{{ detailForm.softwareVersion }}</el-descriptions-item>
        <el-descriptions-item label="授权数">{{ detailForm.licenseCount }}</el-descriptions-item>
        <el-descriptions-item label="交付时间">{{ parseTime(detailForm.deliveryTime) }}</el-descriptions-item>
        <el-descriptions-item label="关联说明" :span="2">{{ detailForm.relationSummary }}</el-descriptions-item>
        <el-descriptions-item label="性能说明" :span="2">{{ detailForm.performanceSummary }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog title="资源工单" :visible.sync="orderOpen" width="760px" append-to-body>
      <el-form ref="orderFormRef" :model="orderForm" :rules="orderRules" label-width="110px">
        <el-form-item label="工单标题" prop="requestTitle"><el-input v-model="orderForm.requestTitle" /></el-form-item>
        <el-form-item label="工单类型" prop="requestType">
          <el-select v-model="orderForm.requestType" @change="handleRequestTypeChange">
            <el-option v-for="item in requestTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联资源ID" prop="subjectId">
          <el-input v-model="orderForm.subjectId" :disabled="orderForm.requestType === 'APPLY'" placeholder="申请工单可留空，变更/回收必填" />
        </el-form-item>
        <el-form-item label="关联项目ID"><el-input v-model="orderForm.projectId" /></el-form-item>
        <el-form-item label="申请人" prop="applicantName"><el-input v-model="orderForm.applicantName" /></el-form-item>
        <el-form-item label="申请原因" prop="requestReason"><el-input type="textarea" :rows="3" v-model="orderForm.requestReason" /></el-form-item>
        <el-form-item label="申请内容JSON"><el-input type="textarea" :rows="4" v-model="orderForm.requestPayloadJson" /></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitOrder">确定</el-button>
        <el-button @click="orderOpen = false">取消</el-button>
      </div>
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
      <div slot="footer">
        <el-button type="primary" @click="submitApprove">确定</el-button>
        <el-button @click="approveOpen = false">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="执行工单" :visible.sync="executeOpen" width="520px" append-to-body>
      <el-form label-width="100px">
        <el-form-item label="执行人"><el-input v-model="executeForm.executorName" /></el-form-item>
        <el-form-item label="目标状态">
          <el-select v-model="executeForm.targetStatus">
            <el-option v-for="item in executableStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="执行结果"><el-input type="textarea" :rows="3" v-model="executeForm.executionResult" /></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitExecute">确定</el-button>
        <el-button @click="executeOpen = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  addResource,
  addResourceOrder,
  approveResourceOrder,
  delResource,
  delResourceOrder,
  executeResourceOrder,
  getResource,
  listResource,
  listResourceOrder,
  updateResource,
  updateResourceOrder
} from '@/api/information/resource'

export default {
  name: 'InformationResource',
  data() {
    return {
      activeTab: 'resource',
      resourceLoading: false,
      orderLoading: false,
      resourceList: [],
      orderList: [],
      resourceTotal: 0,
      orderTotal: 0,
      resourceIds: [],
      selectedResource: null,
      selectedOrder: null,
      resourceSingle: true,
      resourceMultiple: true,
      orderSingle: true,
      resourceOpen: false,
      detailOpen: false,
      orderOpen: false,
      approveOpen: false,
      executeOpen: false,
      resourceDialogTitle: '',
      detailForm: null,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        resourceCode: undefined,
        resourceName: undefined,
        resourceStatus: undefined,
        monitorStatus: undefined,
        ownerName: undefined,
        maintainerName: undefined,
        ipAddress: undefined
      },
      orderQuery: {
        pageNum: 1,
        pageSize: 10,
        workOrderNo: undefined,
        requestType: undefined,
        orderStatus: undefined,
        subjectName: undefined,
        applicantName: undefined
      },
      resourceStatusOptions: [
        { label: '空闲', value: 'IDLE' },
        { label: '待交付', value: 'PENDING_DELIVERY' },
        { label: '在用', value: 'IN_USE' },
        { label: '变更中', value: 'CHANGING' },
        { label: '待回收', value: 'PENDING_RECYCLE' },
        { label: '已回收', value: 'RECYCLED' }
      ],
      monitorStatusOptions: [
        { label: '正常', value: 'NORMAL' },
        { label: '告警', value: 'WARNING' },
        { label: '严重', value: 'CRITICAL' },
        { label: '未知', value: 'UNKNOWN' }
      ],
      requestTypeOptions: [
        { label: '资源申请', value: 'APPLY' },
        { label: '资源变更', value: 'CHANGE' },
        { label: '资源回收', value: 'RECYCLE' }
      ],
      orderStatusOptions: [
        { label: '草稿', value: 'DRAFT' },
        { label: '待审批', value: 'PENDING' },
        { label: '已驳回', value: 'REJECTED' },
        { label: '待执行', value: 'PENDING_EXECUTION' },
        { label: '已完成', value: 'COMPLETED' }
      ],
      executableStatusOptions: [
        { label: '在用', value: 'IN_USE' },
        { label: '空闲', value: 'IDLE' },
        { label: '已回收', value: 'RECYCLED' }
      ],
      resourceForm: {},
      orderForm: {},
      approveForm: { approved: true, approvalComment: '' },
      executeForm: { executorName: '', targetStatus: 'IN_USE', executionResult: '' },
      resourceRules: {
        resourceCode: [{ required: true, message: '请输入资源编码', trigger: 'blur' }],
        resourceName: [{ required: true, message: '请输入资源名称', trigger: 'blur' }],
        resourceType: [{ required: true, message: '请输入资源类型', trigger: 'blur' }]
      },
      orderRules: {
        requestTitle: [{ required: true, message: '请输入工单标题', trigger: 'blur' }],
        requestType: [{ required: true, message: '请选择工单类型', trigger: 'change' }],
        applicantName: [{ required: true, message: '请输入申请人', trigger: 'blur' }],
        requestReason: [{ required: true, message: '请输入申请原因', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getResourceList()
    this.getOrderList()
  },
  methods: {
    handleTabChange() {
      if (this.activeTab === 'resource') {
        this.getResourceList()
      } else {
        this.getOrderList()
      }
    },
    formatLabel(value, options) {
      const target = options.find(item => item.value === value)
      return target ? target.label : value
    },
    monitorTagType(value) {
      if (value === 'CRITICAL') return 'danger'
      if (value === 'WARNING') return 'warning'
      if (value === 'NORMAL') return 'success'
      return 'info'
    },
    getResourceList() {
      this.resourceLoading = true
      listResource(this.queryParams).then(response => {
        this.resourceList = response.rows
        this.resourceTotal = response.total
        this.resourceLoading = false
      }).catch(() => {
        this.resourceLoading = false
      })
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.getResourceList()
    },
    handleResourceSelection(selection) {
      this.resourceIds = selection.map(item => item.resourceId)
      this.selectedResource = selection[0]
      this.resourceSingle = selection.length !== 1
      this.resourceMultiple = !selection.length
    },
    resetResourceForm() {
      this.resourceForm = {
        resourceId: undefined,
        resourceStatus: 'IDLE',
        monitorStatus: 'UNKNOWN'
      }
      this.resetForm('resourceFormRef')
    },
    handleAddResource() {
      this.resetResourceForm()
      this.resourceDialogTitle = '新增资源'
      this.resourceOpen = true
    },
    handleEditResource(row) {
      this.resetResourceForm()
      const resourceId = row ? row.resourceId : this.resourceIds[0]
      getResource(resourceId).then(response => {
        this.resourceForm = response.data
        this.resourceDialogTitle = '修改资源'
        this.resourceOpen = true
      })
    },
    handleViewResource() {
      if (!this.selectedResource) {
        return
      }
      getResource(this.selectedResource.resourceId).then(response => {
        this.detailForm = response.data
        this.detailOpen = true
      })
    },
    submitResource() {
      this.$refs.resourceFormRef.validate(valid => {
        if (!valid) {
          return
        }
        const action = this.resourceForm.resourceId ? updateResource : addResource
        action(this.resourceForm).then(() => {
          this.$modal.msgSuccess(this.resourceForm.resourceId ? '修改成功' : '新增成功')
          this.resourceOpen = false
          this.getResourceList()
        })
      })
    },
    handleDeleteResource(row) {
      const resourceId = row ? row.resourceId : this.resourceIds.join(',')
      this.$modal.confirm(`确认删除资源 ${resourceId} 吗？`).then(() => {
        return delResource(resourceId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getResourceList()
      }).catch(() => {})
    },
    getOrderList() {
      this.orderLoading = true
      listResourceOrder(this.orderQuery).then(response => {
        this.orderList = response.rows
        this.orderTotal = response.total
        this.orderLoading = false
      }).catch(() => {
        this.orderLoading = false
      })
    },
    resetOrderQuery() {
      this.resetForm('orderQueryForm')
      this.getOrderList()
    },
    handleOrderSelection(selection) {
      this.selectedOrder = selection[0]
      this.orderSingle = selection.length !== 1
    },
    resetOrderForm() {
      this.orderForm = {
        workOrderId: undefined,
        requestType: 'APPLY',
        orderStatus: 'PENDING',
        subjectId: undefined,
        projectId: undefined
      }
      this.resetForm('orderFormRef')
    },
    handleRequestTypeChange(value) {
      if (value === 'APPLY') {
        this.orderForm.subjectId = undefined
        this.orderForm.subjectName = undefined
      }
    },
    handleAddOrder() {
      this.resetOrderForm()
      if (this.selectedResource && this.selectedResource.resourceId) {
        this.orderForm.subjectId = this.selectedResource.resourceId
        this.orderForm.subjectName = this.selectedResource.resourceName
        this.orderForm.projectId = this.selectedResource.projectId
      }
      this.orderOpen = true
    },
    submitOrder() {
      this.$refs.orderFormRef.validate(valid => {
        if (!valid) {
          return
        }
        if (this.orderForm.requestType !== 'APPLY' && !this.orderForm.subjectId) {
          this.$modal.msgError('变更或回收工单必须关联资源ID')
          return
        }
        if (this.orderForm.requestType === 'APPLY' && !this.orderForm.projectId) {
          this.$modal.msgError('资源申请工单必须关联项目ID')
          return
        }
        const action = this.orderForm.workOrderId ? updateResourceOrder : addResourceOrder
        action(this.orderForm).then(() => {
          this.$modal.msgSuccess('提交成功')
          this.orderOpen = false
          this.getOrderList()
        })
      })
    },
    handleApproveOrder() {
      if (!this.selectedOrder) {
        return
      }
      this.approveForm = { approved: true, approvalComment: '' }
      this.approveOpen = true
    },
    submitApprove() {
      approveResourceOrder(this.selectedOrder.workOrderId, this.approveForm).then(() => {
        this.$modal.msgSuccess('审批成功')
        this.approveOpen = false
        this.getOrderList()
        this.getResourceList()
      })
    },
    handleExecuteOrder() {
      if (!this.selectedOrder) {
        return
      }
      this.executeForm = {
        executorName: '',
        targetStatus: this.selectedOrder.requestType === 'RECYCLE' ? 'RECYCLED' : 'IN_USE',
        executionResult: ''
      }
      this.executeOpen = true
    },
    submitExecute() {
      executeResourceOrder(this.selectedOrder.workOrderId, this.executeForm).then(() => {
        this.$modal.msgSuccess('执行成功')
        this.executeOpen = false
        this.getOrderList()
        this.getResourceList()
      })
    },
    handleDeleteOrder() {
      if (!this.selectedOrder) {
        return
      }
      this.$modal.confirm(`确认删除工单 ${this.selectedOrder.workOrderNo} 吗？`).then(() => {
        return delResourceOrder(this.selectedOrder.workOrderId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getOrderList()
      }).catch(() => {})
    }
  }
}
</script>
