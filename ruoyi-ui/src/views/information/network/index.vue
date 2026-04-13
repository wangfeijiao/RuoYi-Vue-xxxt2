<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" @tab-click="handleTabChange">
      <el-tab-pane label="网络资源" name="network">
        <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
          <el-form-item label="资源名称">
            <el-input v-model="queryParams.networkName" placeholder="请输入网络资源名称" clearable @keyup.enter.native="getNetworkList" />
          </el-form-item>
          <el-form-item label="资源状态">
            <el-select v-model="queryParams.resourceStatus" placeholder="请选择" clearable>
              <el-option v-for="item in resourceStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="mini" icon="el-icon-search" @click="getNetworkList">搜索</el-button>
            <el-button size="mini" icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5"><el-button type="primary" plain size="mini" icon="el-icon-plus" @click="handleAddNetwork" v-hasPermi="['information:network:add']">新增网络资源</el-button></el-col>
          <el-col :span="1.5"><el-button type="success" plain size="mini" icon="el-icon-edit" :disabled="networkSingle" @click="handleEditNetwork()" v-hasPermi="['information:network:edit']">修改</el-button></el-col>
          <el-col :span="1.5"><el-button type="danger" plain size="mini" icon="el-icon-delete" :disabled="networkMultiple" @click="handleDeleteNetwork()" v-hasPermi="['information:network:remove']">删除</el-button></el-col>
        </el-row>

        <el-table v-loading="networkLoading" :data="networkList" @selection-change="handleNetworkSelection">
          <el-table-column type="selection" width="55" />
          <el-table-column label="资源编码" prop="networkCode" min-width="120" />
          <el-table-column label="资源名称" prop="networkName" min-width="170" />
          <el-table-column label="资源类型" prop="resourceType" width="120" />
          <el-table-column label="资源状态" prop="resourceStatus" width="120">
            <template slot-scope="scope">
              <el-tag size="mini">{{ scope.row.resourceStatus }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="安全状态" prop="securityStatus" width="120" />
          <el-table-column label="IP网段" prop="ipSegment" width="140" />
          <el-table-column label="VLAN" prop="vlanNo" width="100" />
          <el-table-column label="设备名称" prop="deviceName" min-width="140" />
          <el-table-column label="责任人" prop="ownerName" width="120" />
        </el-table>
        <pagination v-show="networkTotal > 0" :total="networkTotal" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getNetworkList" />
      </el-tab-pane>

      <el-tab-pane label="网络工单" name="order">
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5"><el-button type="primary" plain size="mini" icon="el-icon-plus" @click="handleAddOrder" v-hasPermi="['information:networkOrder:add']">发起工单</el-button></el-col>
          <el-col :span="1.5"><el-button type="warning" plain size="mini" icon="el-icon-finished" :disabled="orderSingle" @click="handleApproveOrder" v-hasPermi="['information:networkOrder:approve']">审批</el-button></el-col>
          <el-col :span="1.5"><el-button type="success" plain size="mini" icon="el-icon-check" :disabled="orderSingle" @click="handleExecuteOrder" v-hasPermi="['information:networkOrder:execute']">执行</el-button></el-col>
        </el-row>

        <el-table v-loading="orderLoading" :data="orderList" @selection-change="handleOrderSelection">
          <el-table-column type="selection" width="55" />
          <el-table-column label="工单号" prop="workOrderNo" min-width="180" />
          <el-table-column label="标题" prop="requestTitle" min-width="180" />
          <el-table-column label="网络资源" prop="subjectName" min-width="160" />
          <el-table-column label="类型" prop="requestType" width="110" />
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
        <pagination v-show="orderTotal > 0" :total="orderTotal" :page.sync="orderQuery.pageNum" :limit.sync="orderQuery.pageSize" @pagination="getOrderList" />
      </el-tab-pane>
    </el-tabs>

    <el-dialog :title="networkDialogTitle" :visible.sync="networkOpen" width="960px" append-to-body>
      <el-form ref="networkFormRef" :model="networkForm" :rules="networkRules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="资源编码" prop="networkCode"><el-input v-model="networkForm.networkCode" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="资源名称" prop="networkName"><el-input v-model="networkForm.networkName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="资源类型"><el-input v-model="networkForm.resourceType" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="资源状态"><el-select v-model="networkForm.resourceStatus"><el-option v-for="item in resourceStatusOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="安全状态"><el-input v-model="networkForm.securityStatus" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="项目ID"><el-input v-model="networkForm.projectId" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="责任人"><el-input v-model="networkForm.ownerName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="IP网段"><el-input v-model="networkForm.ipSegment" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="IP数量"><el-input-number v-model="networkForm.ipCount" :min="0" controls-position="right" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="VLAN"><el-input v-model="networkForm.vlanNo" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="带宽(Mbps)"><el-input-number v-model="networkForm.bandwidthMbps" :min="0" controls-position="right" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="设备名称"><el-input v-model="networkForm.deviceName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="设备类型"><el-input v-model="networkForm.deviceType" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="策略摘要"><el-input type="textarea" :rows="2" v-model="networkForm.ruleSummary" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="访问策略"><el-input type="textarea" :rows="2" v-model="networkForm.accessPolicy" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="拓扑文档"><el-input v-model="networkForm.topologyDoc" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="分配文档"><el-input v-model="networkForm.allocationDoc" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="监控摘要"><el-input type="textarea" :rows="2" v-model="networkForm.monitorSummary" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitNetwork">确定</el-button><el-button @click="networkOpen = false">取消</el-button></div>
    </el-dialog>

    <el-dialog title="网络工单" :visible.sync="orderOpen" width="760px" append-to-body>
      <el-form ref="orderFormRef" :model="orderForm" :rules="orderRules" label-width="110px">
        <el-form-item label="工单标题" prop="requestTitle"><el-input v-model="orderForm.requestTitle" /></el-form-item>
        <el-form-item label="关联资源ID" prop="subjectId"><el-input v-model="orderForm.subjectId" /></el-form-item>
        <el-form-item label="关联项目ID"><el-input v-model="orderForm.projectId" /></el-form-item>
        <el-form-item label="工单类型">
          <el-select v-model="orderForm.requestType">
            <el-option v-for="item in requestTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请人"><el-input v-model="orderForm.applicantName" /></el-form-item>
        <el-form-item label="申请原因"><el-input type="textarea" :rows="3" v-model="orderForm.requestReason" /></el-form-item>
        <el-form-item label="申请内容JSON"><el-input type="textarea" :rows="4" v-model="orderForm.requestPayloadJson" /></el-form-item>
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
        <el-form-item label="目标状态"><el-input v-model="executeForm.targetStatus" placeholder="如 IN_USE / RECYCLED" /></el-form-item>
        <el-form-item label="执行结果"><el-input type="textarea" :rows="3" v-model="executeForm.executionResult" /></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitExecute">确定</el-button><el-button @click="executeOpen = false">取消</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import {
  addNetwork,
  addNetworkOrder,
  approveNetworkOrder,
  delNetwork,
  executeNetworkOrder,
  getNetwork,
  listNetwork,
  listNetworkOrder,
  updateNetwork,
  updateNetworkOrder
} from '@/api/information/network'

export default {
  name: 'InformationNetwork',
  data() {
    return {
      activeTab: 'network',
      networkLoading: false,
      orderLoading: false,
      networkList: [],
      orderList: [],
      networkTotal: 0,
      orderTotal: 0,
      networkIds: [],
      selectedNetwork: null,
      selectedOrder: null,
      networkSingle: true,
      networkMultiple: true,
      orderSingle: true,
      networkOpen: false,
      orderOpen: false,
      approveOpen: false,
      executeOpen: false,
      networkDialogTitle: '',
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        networkName: undefined,
        resourceStatus: undefined
      },
      orderQuery: {
        pageNum: 1,
        pageSize: 10
      },
      resourceStatusOptions: [
        { label: '待分配', value: 'PENDING' },
        { label: '在用', value: 'IN_USE' },
        { label: '待回收', value: 'PENDING_RECYCLE' },
        { label: '已回收', value: 'RECYCLED' }
      ],
      requestTypeOptions: [
        { label: '申请', value: 'APPLY' },
        { label: '变更', value: 'CHANGE' },
        { label: '交付', value: 'DELIVER' },
        { label: '回收', value: 'RECYCLE' }
      ],
      networkForm: {},
      orderForm: {},
      approveForm: { approved: true, approvalComment: '' },
      executeForm: { executorName: '', targetStatus: 'IN_USE', executionResult: '' },
      networkRules: {
        networkCode: [{ required: true, message: '请输入资源编码', trigger: 'blur' }],
        networkName: [{ required: true, message: '请输入资源名称', trigger: 'blur' }]
      },
      orderRules: {
        requestTitle: [{ required: true, message: '请输入工单标题', trigger: 'blur' }],
        subjectId: [{ required: true, message: '请输入关联资源ID', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getNetworkList()
    this.getOrderList()
  },
  methods: {
    handleTabChange() {
      if (this.activeTab === 'network') {
        this.getNetworkList()
      } else {
        this.getOrderList()
      }
    },
    getNetworkList() {
      this.networkLoading = true
      listNetwork(this.queryParams).then(response => {
        this.networkList = response.rows
        this.networkTotal = response.total
        this.networkLoading = false
      }).catch(() => {
        this.networkLoading = false
      })
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.getNetworkList()
    },
    handleNetworkSelection(selection) {
      this.networkIds = selection.map(item => item.networkId)
      this.selectedNetwork = selection[0]
      this.networkSingle = selection.length !== 1
      this.networkMultiple = !selection.length
    },
    resetNetworkForm() {
      this.networkForm = {
        networkId: undefined,
        resourceStatus: 'PENDING'
      }
      this.resetForm('networkFormRef')
    },
    handleAddNetwork() {
      this.resetNetworkForm()
      this.networkDialogTitle = '新增网络资源'
      this.networkOpen = true
    },
    handleEditNetwork(row) {
      this.resetNetworkForm()
      const networkId = row ? row.networkId : this.networkIds[0]
      getNetwork(networkId).then(response => {
        this.networkForm = response.data
        this.networkDialogTitle = '修改网络资源'
        this.networkOpen = true
      })
    },
    submitNetwork() {
      this.$refs.networkFormRef.validate(valid => {
        if (!valid) {
          return
        }
        const action = this.networkForm.networkId ? updateNetwork : addNetwork
        action(this.networkForm).then(() => {
          this.$modal.msgSuccess(this.networkForm.networkId ? '修改成功' : '新增成功')
          this.networkOpen = false
          this.getNetworkList()
        })
      })
    },
    handleDeleteNetwork(row) {
      const networkId = row ? row.networkId : this.networkIds.join(',')
      this.$modal.confirm(`确认删除网络资源 ${networkId} 吗？`).then(() => {
        return delNetwork(networkId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getNetworkList()
      }).catch(() => {})
    },
    getOrderList() {
      this.orderLoading = true
      listNetworkOrder(this.orderQuery).then(response => {
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
        requestType: 'APPLY',
        orderStatus: 'PENDING'
      }
      this.resetForm('orderFormRef')
    },
    handleAddOrder() {
      this.resetOrderForm()
      if (this.selectedNetwork && this.selectedNetwork.networkId) {
        this.orderForm.subjectId = this.selectedNetwork.networkId
        this.orderForm.subjectName = this.selectedNetwork.networkName
        this.orderForm.projectId = this.selectedNetwork.projectId
      }
      this.orderOpen = true
    },
    submitOrder() {
      this.$refs.orderFormRef.validate(valid => {
        if (!valid) {
          return
        }
        const action = this.orderForm.workOrderId ? updateNetworkOrder : addNetworkOrder
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
      approveNetworkOrder(this.selectedOrder.workOrderId, this.approveForm).then(() => {
        this.$modal.msgSuccess('审批成功')
        this.approveOpen = false
        this.getOrderList()
      })
    },
    handleExecuteOrder() {
      if (!this.selectedOrder) {
        return
      }
      this.executeForm = { executorName: '', targetStatus: 'IN_USE', executionResult: '' }
      this.executeOpen = true
    },
    submitExecute() {
      executeNetworkOrder(this.selectedOrder.workOrderId, this.executeForm).then(() => {
        this.$modal.msgSuccess('执行成功')
        this.executeOpen = false
        this.getOrderList()
        this.getNetworkList()
      })
    }
  }
}
</script>
