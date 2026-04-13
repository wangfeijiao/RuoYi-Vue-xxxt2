<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" @tab-click="handleTabChange">
      <el-tab-pane label="资源台账" name="resource">
        <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
          <el-form-item label="资源编码">
            <el-input
              v-model="queryParams.resourceCode"
              placeholder="请输入资源编码"
              clearable
              @keyup.enter.native="getResourceList"
            />
          </el-form-item>
          <el-form-item label="资源名称">
            <el-input
              v-model="queryParams.resourceName"
              placeholder="请输入资源名称"
              clearable
              @keyup.enter.native="getResourceList"
            />
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
            <el-input
              v-model="queryParams.ownerName"
              placeholder="请输入责任人"
              clearable
              @keyup.enter.native="getResourceList"
            />
          </el-form-item>
          <el-form-item label="维护人">
            <el-input
              v-model="queryParams.maintainerName"
              placeholder="请输入维护人"
              clearable
              @keyup.enter.native="getResourceList"
            />
          </el-form-item>
          <el-form-item label="IP 地址">
            <el-input
              v-model="queryParams.ipAddress"
              placeholder="请输入 IP 地址"
              clearable
              @keyup.enter.native="getResourceList"
            />
          </el-form-item>
          <el-form-item label="交付时间">
            <el-date-picker
              v-model="deliveryRange"
              type="datetimerange"
              value-format="yyyy-MM-dd HH:mm:ss"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
            />
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
            <el-button size="mini" plain icon="el-icon-view" :disabled="resourceSingle" @click="handleViewResource()">详情</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain size="mini" icon="el-icon-plus" :disabled="resourceSingle" @click="handleAddOrder(selectedResource)" v-hasPermi="['information:resourceOrder:add']">发起变更</el-button>
          </el-col>
        </el-row>

        <el-table v-loading="resourceLoading" :data="resourceList" @selection-change="handleResourceSelection">
          <el-table-column type="selection" width="55" />
          <el-table-column label="资源编码" prop="resourceCode" min-width="120" />
          <el-table-column label="资源名称" prop="resourceName" min-width="160" />
          <el-table-column label="资源类型" prop="resourceType" width="120" />
          <el-table-column label="资源状态" prop="resourceStatus" width="130">
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
          <el-table-column label="所属项目" prop="projectName" min-width="160" />
          <el-table-column label="责任人" prop="ownerName" width="120" />
          <el-table-column label="维护人" prop="maintainerName" width="120" />
          <el-table-column label="IP 地址" prop="ipAddress" width="140" />
          <el-table-column label="交付时间" prop="deliveryTime" width="170">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.deliveryTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template slot-scope="scope">
              <el-button size="mini" type="text" @click="handleViewResource(scope.row)">详情</el-button>
              <el-button size="mini" type="text" @click="handleEditResource(scope.row)" v-hasPermi="['information:resource:edit']">修改</el-button>
              <el-button size="mini" type="text" @click="handleAddOrder(scope.row)" v-hasPermi="['information:resourceOrder:add']">发起工单</el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="resourceTotal > 0" :total="resourceTotal" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getResourceList" />
      </el-tab-pane>

      <el-tab-pane label="资源工单" name="order">
        <el-form ref="orderQueryForm" :model="orderQuery" size="small" :inline="true" label-width="80px">
          <el-form-item label="工单号">
            <el-input
              v-model="orderQuery.workOrderNo"
              placeholder="请输入工单号"
              clearable
              @keyup.enter.native="getOrderList"
            />
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
            <el-input
              v-model="orderQuery.subjectName"
              placeholder="请输入资源名称"
              clearable
              @keyup.enter.native="getOrderList"
            />
          </el-form-item>
          <el-form-item label="申请人">
            <el-input
              v-model="orderQuery.applicantName"
              placeholder="请输入申请人"
              clearable
              @keyup.enter.native="getOrderList"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="mini" icon="el-icon-search" @click="getOrderList">搜索</el-button>
            <el-button size="mini" icon="el-icon-refresh" @click="resetOrderQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain size="mini" icon="el-icon-plus" @click="handleAddOrder()" v-hasPermi="['information:resourceOrder:add']">发起工单</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain size="mini" icon="el-icon-finished" :disabled="!canApproveSelected" @click="handleApproveOrder()" v-hasPermi="['information:resourceOrder:approve']">审批</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain size="mini" icon="el-icon-check" :disabled="!canExecuteSelected" @click="handleExecuteOrder()" v-hasPermi="['information:resourceOrder:execute']">执行</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain size="mini" icon="el-icon-delete" :disabled="!canDeleteSelected" @click="handleDeleteOrder()" v-hasPermi="['information:resourceOrder:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button size="mini" plain icon="el-icon-view" :disabled="orderSingle" @click="handleViewOrder()">详情</el-button>
          </el-col>
        </el-row>

        <el-table v-loading="orderLoading" :data="orderList" @selection-change="handleOrderSelection">
          <el-table-column type="selection" width="55" />
          <el-table-column label="工单号" prop="workOrderNo" min-width="180" />
          <el-table-column label="工单标题" prop="requestTitle" min-width="180" />
          <el-table-column label="资源名称" prop="subjectName" min-width="160" />
          <el-table-column label="所属项目" prop="projectName" min-width="160" />
          <el-table-column label="工单类型" prop="requestType" width="110">
            <template slot-scope="scope">
              <span>{{ formatLabel(scope.row.requestType, requestTypeOptions) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" prop="orderStatus" width="130">
            <template slot-scope="scope">
              <el-tag size="mini" :type="orderTagType(scope.row.orderStatus)">
                {{ formatLabel(scope.row.orderStatus, orderStatusOptions) }}
              </el-tag>
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
          <el-table-column label="操作" width="240" fixed="right">
            <template slot-scope="scope">
              <el-button size="mini" type="text" @click="handleViewOrder(scope.row)">详情</el-button>
              <el-button size="mini" type="text" :disabled="!isOrderApprovable(scope.row)" @click="handleApproveOrder(scope.row)" v-hasPermi="['information:resourceOrder:approve']">审批</el-button>
              <el-button size="mini" type="text" :disabled="!isOrderExecutable(scope.row)" @click="handleExecuteOrder(scope.row)" v-hasPermi="['information:resourceOrder:execute']">执行</el-button>
              <el-button size="mini" type="text" :disabled="!isOrderRemovable(scope.row)" @click="handleDeleteOrder(scope.row)" v-hasPermi="['information:resourceOrder:remove']">删除</el-button>
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
          <el-col :span="12"><el-form-item label="资源状态"><el-select v-model="resourceForm.resourceStatus" placeholder="请选择"><el-option v-for="item in resourceStatusOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="监控状态"><el-select v-model="resourceForm.monitorStatus" placeholder="请选择"><el-option v-for="item in monitorStatusOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="所属项目">
              <el-select v-model="resourceForm.projectId" filterable clearable placeholder="请选择项目" @visible-change="handleProjectSelectOpen">
                <el-option v-for="item in projectOptions" :key="item.projectId" :label="formatProjectOption(item)" :value="item.projectId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="责任人"><el-input v-model="resourceForm.ownerName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="维护人"><el-input v-model="resourceForm.maintainerName" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="CPU 核数"><el-input-number v-model="resourceForm.cpuCores" :min="0" controls-position="right" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="内存(GB)"><el-input-number v-model="resourceForm.memoryGb" :min="0" controls-position="right" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="存储(GB)"><el-input-number v-model="resourceForm.storageGb" :min="0" controls-position="right" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="操作系统"><el-input v-model="resourceForm.osName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="IP 地址"><el-input v-model="resourceForm.ipAddress" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="软件名称"><el-input v-model="resourceForm.softwareName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="软件版本"><el-input v-model="resourceForm.softwareVersion" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="授权数"><el-input-number v-model="resourceForm.licenseCount" :min="0" controls-position="right" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="交付时间"><el-date-picker v-model="resourceForm.deliveryTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择交付时间" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="关联说明"><el-input v-model="resourceForm.relationSummary" type="textarea" :rows="2" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="性能说明"><el-input v-model="resourceForm.performanceSummary" type="textarea" :rows="2" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitResource">确定</el-button>
        <el-button @click="resourceOpen = false">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="资源详情" :visible.sync="detailOpen" width="820px" append-to-body>
      <el-descriptions v-if="detailForm" :column="2" border>
        <el-descriptions-item label="资源编码">{{ detailForm.resourceCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="资源名称">{{ detailForm.resourceName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="资源类型">{{ detailForm.resourceType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="资源状态">{{ formatLabel(detailForm.resourceStatus, resourceStatusOptions) }}</el-descriptions-item>
        <el-descriptions-item label="监控状态">{{ formatLabel(detailForm.monitorStatus, monitorStatusOptions) }}</el-descriptions-item>
        <el-descriptions-item label="所属项目">{{ detailForm.projectName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="责任人">{{ detailForm.ownerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="维护人">{{ detailForm.maintainerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="CPU 核数">{{ detailForm.cpuCores || 0 }}</el-descriptions-item>
        <el-descriptions-item label="内存(GB)">{{ detailForm.memoryGb || 0 }}</el-descriptions-item>
        <el-descriptions-item label="存储(GB)">{{ detailForm.storageGb || 0 }}</el-descriptions-item>
        <el-descriptions-item label="IP 地址">{{ detailForm.ipAddress || '-' }}</el-descriptions-item>
        <el-descriptions-item label="软件名称">{{ detailForm.softwareName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="软件版本">{{ detailForm.softwareVersion || '-' }}</el-descriptions-item>
        <el-descriptions-item label="授权数">{{ detailForm.licenseCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="交付时间">{{ parseTime(detailForm.deliveryTime) || '-' }}</el-descriptions-item>
        <el-descriptions-item label="关联说明" :span="2">{{ detailForm.relationSummary || '-' }}</el-descriptions-item>
        <el-descriptions-item label="性能说明" :span="2">{{ detailForm.performanceSummary || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog :title="orderDialogTitle" :visible.sync="orderOpen" width="820px" append-to-body>
      <el-form ref="orderFormRef" :model="orderForm" :rules="orderRules" label-width="110px">
        <el-alert :title="requestTypeHint" type="info" :closable="false" show-icon class="order-tip" />
        <el-form-item label="工单标题" prop="requestTitle"><el-input v-model="orderForm.requestTitle" /></el-form-item>
        <el-form-item label="工单类型" prop="requestType">
          <el-select v-model="orderForm.requestType" @change="handleRequestTypeChange">
            <el-option v-for="item in requestTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联资源">
          <el-select v-model="orderForm.subjectId" filterable clearable placeholder="请选择资源" :disabled="orderForm.requestType === 'APPLY'" @visible-change="handleResourceSelectOpen" @change="handleSubjectChange">
            <el-option v-for="item in resourceOptions" :key="item.resourceId" :label="formatResourceOption(item)" :value="item.resourceId" />
          </el-select>
          <div class="form-helper">申请工单无需绑定现有资源，变更与回收工单必须选择具体资源。</div>
        </el-form-item>
        <el-form-item label="所属项目">
          <el-select v-model="orderForm.projectId" filterable clearable placeholder="请选择项目" :disabled="orderForm.requestType !== 'APPLY'" @visible-change="handleProjectSelectOpen">
            <el-option v-for="item in projectOptions" :key="item.projectId" :label="formatProjectOption(item)" :value="item.projectId" />
          </el-select>
          <div class="form-helper">资源申请工单必须关联项目；变更与回收工单会自动带出资源所属项目。</div>
        </el-form-item>
        <el-form-item label="申请人" prop="applicantName"><el-input v-model="orderForm.applicantName" /></el-form-item>
        <el-form-item label="期望完成时间"><el-date-picker v-model="orderForm.expectFinishTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择期望完成时间" /></el-form-item>
        <el-form-item label="申请原因" prop="requestReason"><el-input v-model="orderForm.requestReason" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="载荷 JSON"><el-input v-model="orderForm.requestPayloadJson" type="textarea" :rows="4" placeholder="可选，填写结构化需求载荷" /></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitOrder">确定</el-button>
        <el-button @click="orderOpen = false">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="工单详情" :visible.sync="orderDetailOpen" width="900px" append-to-body>
      <div v-if="orderDetailForm">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="工单号">{{ orderDetailForm.workOrderNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="工单标题">{{ orderDetailForm.requestTitle || '-' }}</el-descriptions-item>
          <el-descriptions-item label="工单类型">{{ formatLabel(orderDetailForm.requestType, requestTypeOptions) }}</el-descriptions-item>
          <el-descriptions-item label="工单状态">{{ formatLabel(orderDetailForm.orderStatus, orderStatusOptions) }}</el-descriptions-item>
          <el-descriptions-item label="所属项目">{{ orderDetailForm.projectName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="关联资源">{{ orderDetailForm.subjectName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="申请人">{{ orderDetailForm.applicantName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审批人">{{ orderDetailForm.approverName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="执行人">{{ orderDetailForm.executorName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="期望完成时间">{{ parseTime(orderDetailForm.expectFinishTime) || '-' }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ parseTime(orderDetailForm.submittedTime) || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审批时间">{{ parseTime(orderDetailForm.approvedTime) || '-' }}</el-descriptions-item>
          <el-descriptions-item label="执行时间">{{ parseTime(orderDetailForm.executedTime) || '-' }}</el-descriptions-item>
          <el-descriptions-item label="申请原因" :span="2">{{ orderDetailForm.requestReason || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审批意见" :span="2">{{ orderDetailForm.approvalComment || '-' }}</el-descriptions-item>
          <el-descriptions-item label="执行结果" :span="2">{{ orderDetailForm.executionResult || '-' }}</el-descriptions-item>
          <el-descriptions-item label="当前快照" :span="2"><pre class="json-block">{{ formatJson(orderDetailForm.currentSnapshotJson) }}</pre></el-descriptions-item>
          <el-descriptions-item label="请求载荷" :span="2"><pre class="json-block">{{ formatJson(orderDetailForm.requestPayloadJson) }}</pre></el-descriptions-item>
        </el-descriptions>
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
        <el-form-item label="审批意见"><el-input v-model="approveForm.approvalComment" type="textarea" :rows="3" /></el-form-item>
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
            <el-option v-for="item in currentExecutableStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="执行结果"><el-input v-model="executeForm.executionResult" type="textarea" :rows="3" /></el-form-item>
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
  getResourceOrder,
  listResource,
  listResourceOrder,
  updateResource
} from '@/api/information/resource'
import { listProject } from '@/api/information/project'

export default {
  name: 'InformationResource',
  data() {
    return {
      activeTab: 'resource',
      resourceLoading: false,
      orderLoading: false,
      resourceList: [],
      orderList: [],
      projectOptions: [],
      resourceOptions: [],
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
      orderDetailOpen: false,
      approveOpen: false,
      executeOpen: false,
      resourceDialogTitle: '',
      orderDialogTitle: '发起资源工单',
      detailForm: null,
      orderDetailForm: null,
      deliveryRange: [],
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
        { label: '预警', value: 'WARNING' },
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
      executableStatusMap: {
        APPLY: [
          { label: '在用', value: 'IN_USE' },
          { label: '空闲', value: 'IDLE' }
        ],
        CHANGE: [
          { label: '在用', value: 'IN_USE' },
          { label: '空闲', value: 'IDLE' }
        ],
        RECYCLE: [
          { label: '已回收', value: 'RECYCLED' }
        ]
      },
      resourceForm: {},
      orderForm: {},
      approveForm: {
        approved: true,
        approvalComment: ''
      },
      executeForm: {
        executorName: '',
        targetStatus: 'IN_USE',
        executionResult: ''
      },
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
  computed: {
    canApproveSelected() {
      return !!(this.selectedOrder && this.isOrderApprovable(this.selectedOrder))
    },
    canExecuteSelected() {
      return !!(this.selectedOrder && this.isOrderExecutable(this.selectedOrder))
    },
    canDeleteSelected() {
      return !!(this.selectedOrder && this.isOrderRemovable(this.selectedOrder))
    },
    requestTypeHint() {
      const hintMap = {
        APPLY: '资源申请工单用于向项目申请新增资源，必须关联所属项目。',
        CHANGE: '资源变更工单用于调整现有资源配置，必须选择已有资源。',
        RECYCLE: '资源回收工单用于释放存量资源，必须选择待回收资源。'
      }
      return hintMap[this.orderForm.requestType] || hintMap.APPLY
    },
    currentExecutableStatusOptions() {
      const requestType = (this.selectedOrder && this.selectedOrder.requestType) || 'APPLY'
      return this.executableStatusMap[requestType] || this.executableStatusMap.APPLY
    }
  },
  created() {
    this.loadProjectOptions()
    this.loadResourceOptions()
    this.getResourceList()
    this.getOrderList()
  },
  methods: {
    handleTabChange() {
      if (this.activeTab === 'resource') {
        this.getResourceList()
        return
      }
      this.getOrderList()
    },
    formatLabel(value, options) {
      const target = options.find(item => item.value === value)
      return target ? target.label : (value || '-')
    },
    formatProjectOption(item) {
      return item.projectCode ? `${item.projectCode} - ${item.projectName}` : item.projectName
    },
    formatResourceOption(item) {
      const status = this.formatLabel(item.resourceStatus, this.resourceStatusOptions)
      return `${item.resourceCode} - ${item.resourceName} (${status})`
    },
    formatJson(value) {
      if (!value) {
        return '-'
      }
      try {
        return JSON.stringify(JSON.parse(value), null, 2)
      } catch (error) {
        return value
      }
    },
    monitorTagType(value) {
      if (value === 'CRITICAL') return 'danger'
      if (value === 'WARNING') return 'warning'
      if (value === 'NORMAL') return 'success'
      return 'info'
    },
    orderTagType(value) {
      if (value === 'PENDING') return 'warning'
      if (value === 'PENDING_EXECUTION') return 'primary'
      if (value === 'COMPLETED') return 'success'
      if (value === 'REJECTED') return 'danger'
      return 'info'
    },
    isOrderApprovable(row) {
      return row && row.orderStatus === 'PENDING'
    },
    isOrderExecutable(row) {
      return row && row.orderStatus === 'PENDING_EXECUTION'
    },
    isOrderRemovable(row) {
      return row && ['DRAFT', 'PENDING'].includes(row.orderStatus)
    },
    getResourceList() {
      this.resourceLoading = true
      const params = { ...this.queryParams }
      if (this.deliveryRange && this.deliveryRange.length === 2) {
        params.params = {
          beginDeliveryTime: this.deliveryRange[0],
          endDeliveryTime: this.deliveryRange[1]
        }
      }
      listResource(params).then(response => {
        this.resourceList = response.rows || []
        this.resourceTotal = response.total || 0
        this.resourceLoading = false
      }).catch(() => {
        this.resourceLoading = false
      })
    },
    resetQuery() {
      this.deliveryRange = []
      this.resetForm('queryForm')
      this.queryParams.pageNum = 1
      this.getResourceList()
    },
    handleResourceSelection(selection) {
      this.resourceIds = selection.map(item => item.resourceId)
      this.selectedResource = selection.length ? selection[0] : null
      this.resourceSingle = selection.length !== 1
      this.resourceMultiple = !selection.length
    },
    resetResourceForm() {
      this.resourceForm = {
        resourceId: undefined,
        resourceCode: undefined,
        resourceName: undefined,
        resourceType: undefined,
        resourceStatus: 'IDLE',
        monitorStatus: 'UNKNOWN',
        projectId: undefined,
        ownerName: undefined,
        maintainerName: undefined,
        cpuCores: 0,
        memoryGb: 0,
        storageGb: 0,
        osName: undefined,
        ipAddress: undefined,
        softwareName: undefined,
        softwareVersion: undefined,
        licenseCount: 0,
        deliveryTime: undefined,
        relationSummary: undefined,
        performanceSummary: undefined
      }
      this.resetForm('resourceFormRef')
    },
    handleProjectSelectOpen(visible) {
      if (visible && !this.projectOptions.length) {
        this.loadProjectOptions()
      }
    },
    handleResourceSelectOpen(visible) {
      if (visible && !this.resourceOptions.length) {
        this.loadResourceOptions()
      }
    },
    loadProjectOptions() {
      listProject({ pageNum: 1, pageSize: 200 }).then(response => {
        this.projectOptions = response.rows || []
      })
    },
    loadResourceOptions() {
      listResource({ pageNum: 1, pageSize: 200 }).then(response => {
        this.resourceOptions = response.rows || []
      })
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
        this.resourceForm = Object.assign({}, this.resourceForm, response.data)
        this.resourceDialogTitle = '修改资源'
        this.resourceOpen = true
      })
    },
    handleViewResource(row) {
      const target = row || this.selectedResource
      if (!target) {
        return
      }
      getResource(target.resourceId).then(response => {
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
          this.loadResourceOptions()
        })
      })
    },
    handleDeleteResource(row) {
      const resourceId = row ? row.resourceId : this.resourceIds.join(',')
      if (!resourceId) {
        return
      }
      this.$modal.confirm(`确认删除资源 ${resourceId} 吗？`).then(() => {
        return delResource(resourceId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getResourceList()
        this.loadResourceOptions()
      }).catch(() => {})
    },
    getOrderList() {
      this.orderLoading = true
      listResourceOrder(this.orderQuery).then(response => {
        this.orderList = response.rows || []
        this.orderTotal = response.total || 0
        this.orderLoading = false
      }).catch(() => {
        this.orderLoading = false
      })
    },
    resetOrderQuery() {
      this.resetForm('orderQueryForm')
      this.orderQuery.pageNum = 1
      this.getOrderList()
    },
    handleOrderSelection(selection) {
      this.selectedOrder = selection.length ? selection[0] : null
      this.orderSingle = selection.length !== 1
    },
    resetOrderForm() {
      this.orderForm = {
        workOrderId: undefined,
        requestType: 'APPLY',
        orderStatus: 'PENDING',
        projectId: undefined,
        subjectId: undefined,
        subjectName: undefined,
        applicantName: undefined,
        requestTitle: undefined,
        requestReason: undefined,
        requestPayloadJson: undefined,
        expectFinishTime: undefined
      }
      this.resetForm('orderFormRef')
    },
    handleRequestTypeChange(value) {
      if (value === 'APPLY') {
        this.orderForm.subjectId = undefined
        this.orderForm.subjectName = undefined
        return
      }
      if (!this.orderForm.subjectId) {
        this.orderForm.projectId = undefined
      }
    },
    fillOrderByResource(row) {
      this.orderForm.subjectId = row.resourceId
      this.orderForm.subjectName = row.resourceName
      this.orderForm.projectId = row.projectId
    },
    handleSubjectChange(resourceId) {
      if (!resourceId) {
        this.orderForm.subjectName = undefined
        if (this.orderForm.requestType !== 'APPLY') {
          this.orderForm.projectId = undefined
        }
        return
      }
      const target = this.resourceOptions.find(item => item.resourceId === resourceId)
      if (target) {
        this.orderForm.subjectName = target.resourceName
        this.orderForm.projectId = target.projectId
      }
    },
    handleAddOrder(row) {
      this.resetOrderForm()
      if (row && row.resourceId) {
        this.orderForm.requestType = 'CHANGE'
        this.fillOrderByResource(row)
        this.orderForm.requestTitle = `资源变更申请 - ${row.resourceName}`
      }
      this.orderDialogTitle = '发起资源工单'
      this.orderOpen = true
    },
    validateJsonField(content, fieldName) {
      if (!content) {
        return true
      }
      try {
        JSON.parse(content)
        return true
      } catch (error) {
        this.$modal.msgError(`${fieldName} 不是合法的 JSON`)
        return false
      }
    },
    submitOrder() {
      this.$refs.orderFormRef.validate(valid => {
        if (!valid) {
          return
        }
        if (this.orderForm.requestType !== 'APPLY' && !this.orderForm.subjectId) {
          this.$modal.msgError('变更或回收工单必须选择关联资源')
          return
        }
        if (this.orderForm.requestType === 'APPLY' && !this.orderForm.projectId) {
          this.$modal.msgError('资源申请工单必须关联所属项目')
          return
        }
        if (!this.validateJsonField(this.orderForm.requestPayloadJson, '载荷 JSON')) {
          return
        }
        addResourceOrder(this.orderForm).then(() => {
          this.$modal.msgSuccess('提交成功')
          this.orderOpen = false
          this.getOrderList()
          this.getResourceList()
        })
      })
    },
    handleViewOrder(row) {
      const target = row || this.selectedOrder
      if (!target) {
        return
      }
      getResourceOrder(target.workOrderId).then(response => {
        this.orderDetailForm = response.data
        this.orderDetailOpen = true
      })
    },
    handleApproveOrder(row) {
      const target = row || this.selectedOrder
      if (!this.isOrderApprovable(target)) {
        return
      }
      this.selectedOrder = target
      this.approveForm = {
        approved: true,
        approvalComment: ''
      }
      this.approveOpen = true
    },
    submitApprove() {
      if (!this.approveForm.approved && !this.approveForm.approvalComment) {
        this.$modal.msgError('驳回时请填写审批意见')
        return
      }
      approveResourceOrder(this.selectedOrder.workOrderId, this.approveForm).then(() => {
        this.$modal.msgSuccess('审批成功')
        this.approveOpen = false
        this.getOrderList()
        this.getResourceList()
      })
    },
    getExecuteDefaultStatus(order) {
      if (!order) {
        return 'IN_USE'
      }
      return order.requestType === 'RECYCLE' ? 'RECYCLED' : 'IN_USE'
    },
    handleExecuteOrder(row) {
      const target = row || this.selectedOrder
      if (!this.isOrderExecutable(target)) {
        return
      }
      this.selectedOrder = target
      this.executeForm = {
        executorName: '',
        targetStatus: this.getExecuteDefaultStatus(target),
        executionResult: ''
      }
      this.executeOpen = true
    },
    submitExecute() {
      if (!this.executeForm.executorName) {
        this.$modal.msgError('请填写执行人')
        return
      }
      if (!this.executeForm.executionResult) {
        this.$modal.msgError('请填写执行结果')
        return
      }
      executeResourceOrder(this.selectedOrder.workOrderId, this.executeForm).then(() => {
        this.$modal.msgSuccess('执行成功')
        this.executeOpen = false
        this.getOrderList()
        this.getResourceList()
        this.loadResourceOptions()
      })
    },
    handleDeleteOrder(row) {
      const target = row || this.selectedOrder
      if (!this.isOrderRemovable(target)) {
        return
      }
      this.$modal.confirm(`确认删除工单 ${target.workOrderNo} 吗？`).then(() => {
        return delResourceOrder(target.workOrderId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getOrderList()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.order-tip {
  margin-bottom: 18px;
}

.form-helper {
  font-size: 12px;
  color: #909399;
  line-height: 20px;
}

.json-block {
  margin: 0;
  padding: 10px 12px;
  background: #f5f7fa;
  border-radius: 4px;
  color: #606266;
  font-size: 12px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
