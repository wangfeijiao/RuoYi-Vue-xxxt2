<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" @tab-click="handleTabChange">
      <el-tab-pane label="数据资产" name="asset">
        <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
          <el-form-item label="资产名称">
            <el-input v-model="queryParams.assetName" placeholder="请输入资产名称" clearable @keyup.enter.native="getAssetList" />
          </el-form-item>
          <el-form-item label="资产状态">
            <el-select v-model="queryParams.assetStatus" placeholder="请选择" clearable>
              <el-option v-for="item in assetStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="mini" icon="el-icon-search" @click="getAssetList">搜索</el-button>
            <el-button size="mini" icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5"><el-button type="primary" plain size="mini" icon="el-icon-plus" @click="handleAddAsset" v-hasPermi="['information:dataAsset:add']">新增资产</el-button></el-col>
          <el-col :span="1.5"><el-button type="success" plain size="mini" icon="el-icon-edit" :disabled="assetSingle" @click="handleEditAsset()" v-hasPermi="['information:dataAsset:edit']">修改</el-button></el-col>
          <el-col :span="1.5"><el-button type="danger" plain size="mini" icon="el-icon-delete" :disabled="assetMultiple" @click="handleDeleteAsset()" v-hasPermi="['information:dataAsset:remove']">删除</el-button></el-col>
        </el-row>

        <el-table v-loading="assetLoading" :data="assetList" @selection-change="handleAssetSelection">
          <el-table-column type="selection" width="55" />
          <el-table-column label="资产编码" prop="assetCode" min-width="120" />
          <el-table-column label="资产名称" prop="assetName" min-width="180" />
          <el-table-column label="业务域" prop="businessDomain" width="120" />
          <el-table-column label="资产类型" prop="assetType" width="120" />
          <el-table-column label="资产状态" prop="assetStatus" width="120">
            <template slot-scope="scope">
              <el-tag size="mini">{{ scope.row.assetStatus }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="安全等级" prop="securityLevel" width="120" />
          <el-table-column label="来源系统" prop="sourceSystem" min-width="140" />
          <el-table-column label="归属人" prop="ownerName" width="120" />
          <el-table-column label="操作" width="150" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="handleShowVersions(scope.row)">版本</el-button>
              <el-button type="text" size="mini" @click="handleEditAsset(scope.row)">修改</el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="assetTotal > 0" :total="assetTotal" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getAssetList" />
      </el-tab-pane>

      <el-tab-pane label="资产工单" name="order">
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5"><el-button type="primary" plain size="mini" icon="el-icon-plus" @click="handleAddOrder" v-hasPermi="['information:dataOrder:add']">发起工单</el-button></el-col>
          <el-col :span="1.5"><el-button type="warning" plain size="mini" icon="el-icon-finished" :disabled="orderSingle" @click="handleApproveOrder" v-hasPermi="['information:dataOrder:approve']">审批</el-button></el-col>
          <el-col :span="1.5"><el-button type="success" plain size="mini" icon="el-icon-check" :disabled="orderSingle" @click="handleExecuteOrder" v-hasPermi="['information:dataOrder:execute']">执行</el-button></el-col>
        </el-row>

        <el-table v-loading="orderLoading" :data="orderList" @selection-change="handleOrderSelection">
          <el-table-column type="selection" width="55" />
          <el-table-column label="工单号" prop="workOrderNo" min-width="180" />
          <el-table-column label="标题" prop="requestTitle" min-width="180" />
          <el-table-column label="资产名称" prop="subjectName" min-width="160" />
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

    <el-dialog :title="assetDialogTitle" :visible.sync="assetOpen" width="960px" append-to-body>
      <el-form ref="assetFormRef" :model="assetForm" :rules="assetRules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="资产编码" prop="assetCode"><el-input v-model="assetForm.assetCode" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="资产名称" prop="assetName"><el-input v-model="assetForm.assetName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="业务域"><el-input v-model="assetForm.businessDomain" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="资产类型"><el-input v-model="assetForm.assetType" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="资产状态"><el-select v-model="assetForm.assetStatus"><el-option v-for="item in assetStatusOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="安全等级"><el-input v-model="assetForm.securityLevel" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="来源系统"><el-input v-model="assetForm.sourceSystem" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="数据格式"><el-input v-model="assetForm.dataFormat" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="更新频率"><el-input v-model="assetForm.updateFrequency" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="数据量"><el-input v-model="assetForm.dataVolume" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="存储位置"><el-input v-model="assetForm.storageLocation" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="共享方式"><el-input v-model="assetForm.shareMode" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="共享条件"><el-input v-model="assetForm.shareCondition" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="归属部门"><el-input v-model="assetForm.ownerDeptName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="归属人"><el-input v-model="assetForm.ownerName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="联系电话"><el-input v-model="assetForm.contactPhone" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="标签"><el-input v-model="assetForm.tagNames" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="模式说明"><el-input type="textarea" :rows="2" v-model="assetForm.schemaDesc" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="数据字典"><el-input type="textarea" :rows="2" v-model="assetForm.dictionaryDesc" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="血缘说明"><el-input type="textarea" :rows="2" v-model="assetForm.lineageDesc" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="质量规则"><el-input type="textarea" :rows="2" v-model="assetForm.qualityRuleDesc" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitAsset">确定</el-button><el-button @click="assetOpen = false">取消</el-button></div>
    </el-dialog>

    <el-dialog title="资产工单" :visible.sync="orderOpen" width="760px" append-to-body>
      <el-form ref="orderFormRef" :model="orderForm" :rules="orderRules" label-width="110px">
        <el-form-item label="工单标题" prop="requestTitle"><el-input v-model="orderForm.requestTitle" /></el-form-item>
        <el-form-item label="关联资产ID" prop="subjectId"><el-input v-model="orderForm.subjectId" /></el-form-item>
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

    <el-dialog title="版本记录" :visible.sync="versionOpen" width="900px" append-to-body>
      <el-table :data="versionList" size="mini">
        <el-table-column label="版本号" prop="versionNo" width="120" />
        <el-table-column label="变更类型" prop="changeType" width="120" />
        <el-table-column label="变更人" prop="changedBy" width="120" />
        <el-table-column label="变更时间" prop="changedTime" width="170">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.changedTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="变更原因" prop="changeReason" min-width="180" show-overflow-tooltip />
        <el-table-column label="快照" prop="snapshotJson" min-width="260" show-overflow-tooltip />
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import {
  addDataAsset,
  addDataOrder,
  approveDataOrder,
  delDataAsset,
  executeDataOrder,
  getDataAsset,
  listDataAsset,
  listDataOrder,
  listDataVersions,
  updateDataAsset,
  updateDataOrder
} from '@/api/information/dataAsset'

export default {
  name: 'InformationDataAsset',
  data() {
    return {
      activeTab: 'asset',
      assetLoading: false,
      orderLoading: false,
      assetList: [],
      orderList: [],
      versionList: [],
      assetTotal: 0,
      orderTotal: 0,
      assetIds: [],
      selectedAsset: null,
      selectedOrder: null,
      assetSingle: true,
      assetMultiple: true,
      orderSingle: true,
      assetOpen: false,
      orderOpen: false,
      approveOpen: false,
      executeOpen: false,
      versionOpen: false,
      assetDialogTitle: '',
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        assetName: undefined,
        assetStatus: undefined
      },
      orderQuery: {
        pageNum: 1,
        pageSize: 10
      },
      assetStatusOptions: [
        { label: '草稿', value: 'DRAFT' },
        { label: '在用', value: 'IN_USE' },
        { label: '共享中', value: 'SHARED' },
        { label: '已回收', value: 'RECYCLED' }
      ],
      requestTypeOptions: [
        { label: '申请', value: 'APPLY' },
        { label: '变更', value: 'CHANGE' },
        { label: '使用', value: 'USE' },
        { label: '回收', value: 'RECYCLE' }
      ],
      assetForm: {},
      orderForm: {},
      approveForm: { approved: true, approvalComment: '' },
      executeForm: { executorName: '', targetStatus: 'IN_USE', executionResult: '' },
      assetRules: {
        assetCode: [{ required: true, message: '请输入资产编码', trigger: 'blur' }],
        assetName: [{ required: true, message: '请输入资产名称', trigger: 'blur' }]
      },
      orderRules: {
        requestTitle: [{ required: true, message: '请输入工单标题', trigger: 'blur' }],
        subjectId: [{ required: true, message: '请输入关联资产ID', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getAssetList()
    this.getOrderList()
  },
  methods: {
    handleTabChange() {
      if (this.activeTab === 'asset') {
        this.getAssetList()
      } else {
        this.getOrderList()
      }
    },
    getAssetList() {
      this.assetLoading = true
      listDataAsset(this.queryParams).then(response => {
        this.assetList = response.rows
        this.assetTotal = response.total
        this.assetLoading = false
      }).catch(() => {
        this.assetLoading = false
      })
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.getAssetList()
    },
    handleAssetSelection(selection) {
      this.assetIds = selection.map(item => item.assetId)
      this.selectedAsset = selection[0]
      this.assetSingle = selection.length !== 1
      this.assetMultiple = !selection.length
    },
    resetAssetForm() {
      this.assetForm = {
        assetId: undefined,
        assetStatus: 'DRAFT'
      }
      this.resetForm('assetFormRef')
    },
    handleAddAsset() {
      this.resetAssetForm()
      this.assetDialogTitle = '新增资产'
      this.assetOpen = true
    },
    handleEditAsset(row) {
      this.resetAssetForm()
      const assetId = row ? row.assetId : this.assetIds[0]
      getDataAsset(assetId).then(response => {
        this.assetForm = response.data
        this.assetDialogTitle = '修改资产'
        this.assetOpen = true
      })
    },
    submitAsset() {
      this.$refs.assetFormRef.validate(valid => {
        if (!valid) {
          return
        }
        const action = this.assetForm.assetId ? updateDataAsset : addDataAsset
        action(this.assetForm).then(() => {
          this.$modal.msgSuccess(this.assetForm.assetId ? '修改成功' : '新增成功')
          this.assetOpen = false
          this.getAssetList()
        })
      })
    },
    handleDeleteAsset(row) {
      const assetId = row ? row.assetId : this.assetIds.join(',')
      this.$modal.confirm(`确认删除资产 ${assetId} 吗？`).then(() => {
        return delDataAsset(assetId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getAssetList()
      }).catch(() => {})
    },
    handleShowVersions(row) {
      listDataVersions(row.assetId).then(response => {
        this.versionList = response.data || []
        this.versionOpen = true
      })
    },
    getOrderList() {
      this.orderLoading = true
      listDataOrder(this.orderQuery).then(response => {
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
        requestType: 'USE',
        orderStatus: 'PENDING'
      }
      this.resetForm('orderFormRef')
    },
    handleAddOrder() {
      this.resetOrderForm()
      if (this.selectedAsset && this.selectedAsset.assetId) {
        this.orderForm.subjectId = this.selectedAsset.assetId
        this.orderForm.subjectName = this.selectedAsset.assetName
      }
      this.orderOpen = true
    },
    submitOrder() {
      this.$refs.orderFormRef.validate(valid => {
        if (!valid) {
          return
        }
        const action = this.orderForm.workOrderId ? updateDataOrder : addDataOrder
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
      approveDataOrder(this.selectedOrder.workOrderId, this.approveForm).then(() => {
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
      executeDataOrder(this.selectedOrder.workOrderId, this.executeForm).then(() => {
        this.$modal.msgSuccess('执行成功')
        this.executeOpen = false
        this.getOrderList()
        this.getAssetList()
      })
    }
  }
}
</script>
