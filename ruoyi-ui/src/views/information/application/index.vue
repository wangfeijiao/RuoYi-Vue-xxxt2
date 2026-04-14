<template>
  <div class="app-container">
    <el-row :gutter="16" class="stats-row">
      <el-col v-for="card in statCards" :key="card.label" :xs="24" :sm="12" :lg="4">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-label">{{ card.label }}</div>
          <div class="stats-value">{{ card.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
      <el-form-item label="应用编码"><el-input v-model="queryParams.applicationCode" placeholder="请输入应用编码" clearable @keyup.enter.native="handleQuery" /></el-form-item>
      <el-form-item label="应用名称"><el-input v-model="queryParams.applicationName" placeholder="请输入应用名称" clearable @keyup.enter.native="handleQuery" /></el-form-item>
      <el-form-item label="建设来源">
        <el-select v-model="queryParams.constructionSource" placeholder="请选择" clearable>
          <el-option v-for="item in constructionSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="业务状态">
        <el-select v-model="queryParams.applicationStatus" placeholder="请选择" clearable>
          <el-option v-for="item in appStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="联动状态">
        <el-select v-model="queryParams.linkageStatus" placeholder="请选择" clearable>
          <el-option v-for="item in linkageStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="责任人"><el-input v-model="queryParams.ownerName" placeholder="请输入责任人" clearable @keyup.enter.native="handleQuery" /></el-form-item>
      <el-form-item>
        <el-button type="primary" size="mini" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button size="mini" icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain size="mini" icon="el-icon-plus" @click="handleAdd" v-hasPermi="['information:application:add']">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain size="mini" icon="el-icon-edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['information:application:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain size="mini" icon="el-icon-delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['information:application:remove']">删除</el-button></el-col>
      <el-col :span="1.5"><el-button size="mini" plain icon="el-icon-view" :disabled="single" @click="openDetail()">详情</el-button></el-col>
      <el-col :span="1.5"><el-button type="warning" plain size="mini" icon="el-icon-refresh-right" :disabled="single" @click="recalculateSelected()" v-hasPermi="['information:application:edit']">重算状态</el-button></el-col>
    </el-row>

    <el-table v-loading="loading" :data="applicationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="应用编码" prop="applicationCode" min-width="130" />
      <el-table-column label="应用名称" prop="applicationName" min-width="180" />
      <el-table-column label="应用类型" prop="applicationType" width="120" />
      <el-table-column label="建设来源" prop="constructionSource" width="120">
        <template slot-scope="scope">{{ formatLabel(scope.row.constructionSource, constructionSourceOptions) }}</template>
      </el-table-column>
      <el-table-column label="业务状态" prop="applicationStatus" width="120">
        <template slot-scope="scope"><el-tag size="mini" :type="appStatusTagType(scope.row.applicationStatus)">{{ formatLabel(scope.row.applicationStatus, appStatusOptions) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="联动状态" prop="linkageStatus" width="120">
        <template slot-scope="scope"><el-tag size="mini" :type="linkageTagType(scope.row.linkageStatus)">{{ formatLabel(scope.row.linkageStatus, linkageStatusOptions) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="开放告警" prop="openAlertCount" width="100" />
      <el-table-column label="责任人" prop="ownerName" width="120" />
      <el-table-column label="关联项目" prop="projectName" min-width="160" />
      <el-table-column label="访问地址" prop="accessUrl" min-width="220" show-overflow-tooltip />
      <el-table-column label="操作" width="240" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="openDetail(scope.row)">详情</el-button>
          <el-button type="text" size="mini" @click="handleUpdate(scope.row)" v-hasPermi="['information:application:edit']">修改</el-button>
          <el-button type="text" size="mini" @click="recalculateStatus(scope.row)" v-hasPermi="['information:application:edit']">重算</el-button>
          <el-button type="text" size="mini" @click="handleDelete(scope.row)" v-hasPermi="['information:application:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="dialogTitle" :visible.sync="open" width="980px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="应用编码" prop="applicationCode"><el-input v-model="form.applicationCode" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="应用名称" prop="applicationName"><el-input v-model="form.applicationName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="应用类型"><el-input v-model="form.applicationType" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="等保级别"><el-input v-model="form.classificationLevel" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="建设来源"><el-select v-model="form.constructionSource"><el-option v-for="item in constructionSourceOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="业务状态"><el-select v-model="form.applicationStatus"><el-option v-for="item in appStatusOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="主关联项目"><el-select v-model="form.projectId" filterable clearable><el-option v-for="item in projectOptions" :key="item.projectId" :label="formatProjectOption(item)" :value="item.projectId" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="归属单位"><el-input v-model="form.ownerOrg" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="业务负责人"><el-input v-model="form.ownerName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="运维负责人"><el-input v-model="form.opsOwner" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="厂商名称"><el-input v-model="form.vendorName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="厂商负责人"><el-input v-model="form.vendorOwner" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="门户负责人"><el-input v-model="form.portalOwner" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="资源负责人"><el-input v-model="form.resourceOwner" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="数据负责人"><el-input v-model="form.dataOwner" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="安全负责人"><el-input v-model="form.securityOwner" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="访问地址"><el-input v-model="form.accessUrl" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="技术栈"><el-input v-model="form.techStack" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="资源摘要"><el-input v-model="form.resourceSummary" type="textarea" :rows="2" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="网络摘要"><el-input v-model="form.networkSummary" type="textarea" :rows="2" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="关系说明"><el-input v-model="form.relationSummary" type="textarea" :rows="3" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitForm">确定</el-button><el-button @click="open = false">取消</el-button></div>
    </el-dialog>

    <el-dialog title="应用详情" :visible.sync="detailOpen" width="1120px" append-to-body>
      <el-tabs v-model="detailTab">
        <el-tab-pane label="基础信息" name="basic">
          <el-descriptions v-if="detail.application" :column="2" border>
            <el-descriptions-item label="应用编码">{{ detail.application.applicationCode || '-' }}</el-descriptions-item>
            <el-descriptions-item label="应用名称">{{ detail.application.applicationName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="应用类型">{{ detail.application.applicationType || '-' }}</el-descriptions-item>
            <el-descriptions-item label="建设来源">{{ formatLabel(detail.application.constructionSource, constructionSourceOptions) }}</el-descriptions-item>
            <el-descriptions-item label="业务状态">{{ formatLabel(detail.application.applicationStatus, appStatusOptions) }}</el-descriptions-item>
            <el-descriptions-item label="联动状态">{{ formatLabel(detail.application.linkageStatus, linkageStatusOptions) }}</el-descriptions-item>
            <el-descriptions-item label="主关联项目">{{ detail.application.projectName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="业务负责人">{{ detail.application.ownerName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="运维负责人">{{ detail.application.opsOwner || '-' }}</el-descriptions-item>
            <el-descriptions-item label="最后告警时间">{{ parseTime(detail.application.lastAlertTime) || '-' }}</el-descriptions-item>
            <el-descriptions-item label="访问地址" :span="2">{{ detail.application.accessUrl || '-' }}</el-descriptions-item>
            <el-descriptions-item label="技术栈" :span="2">{{ detail.application.techStack || '-' }}</el-descriptions-item>
            <el-descriptions-item label="资源摘要" :span="2">{{ detail.application.resourceSummary || '-' }}</el-descriptions-item>
            <el-descriptions-item label="网络摘要" :span="2">{{ detail.application.networkSummary || '-' }}</el-descriptions-item>
            <el-descriptions-item label="关系说明" :span="2">{{ detail.application.relationSummary || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <el-tab-pane label="项目关联" name="projects">
          <div class="tab-toolbar">
            <el-select v-model="projectRelationForm.projectId" filterable clearable placeholder="选择项目" class="toolbar-select">
              <el-option v-for="item in projectOptions" :key="item.projectId" :label="formatProjectOption(item)" :value="item.projectId" />
            </el-select>
            <el-select v-model="projectRelationForm.relationType" placeholder="关联类型" class="toolbar-select">
              <el-option label="主关联" value="PRIMARY" />
              <el-option label="关联项目" value="RELATED" />
              <el-option label="历史项目" value="HISTORICAL" />
            </el-select>
            <el-button type="primary" size="mini" @click="submitProjectRelation" v-hasPermi="['information:application:edit']">新增关联</el-button>
          </div>
          <el-table :data="detail.projectRelations" size="mini">
            <el-table-column label="项目编码" prop="projectCode" min-width="120" />
            <el-table-column label="项目名称" prop="projectName" min-width="160" />
            <el-table-column label="关系类型" prop="relationType" width="120" />
            <el-table-column label="是否有效" prop="activeFlag" width="100">
              <template slot-scope="scope">{{ scope.row.activeFlag === '1' ? '是' : '否' }}</template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template slot-scope="scope">
                <el-button type="text" size="mini" @click="removeProjectRelation(scope.row)" v-hasPermi="['information:application:edit']">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="依赖关系" name="dependencies">
          <div class="tab-toolbar">
            <el-button type="primary" size="mini" @click="openDependencyDialog()" v-hasPermi="['information:application:edit']">新增依赖</el-button>
          </div>
          <el-table :data="detail.dependencies" size="mini">
            <el-table-column label="依赖类型" prop="dependencyType" width="110" />
            <el-table-column label="目标编码" prop="targetCode" min-width="120" />
            <el-table-column label="目标名称" prop="targetName" min-width="180" />
            <el-table-column label="重要级别" prop="importanceLevel" width="110" />
            <el-table-column label="当前状态" prop="runtimeStatus" width="110">
              <template slot-scope="scope"><el-tag size="mini" :type="linkageTagType(scope.row.runtimeStatus)">{{ scope.row.runtimeStatus || '-' }}</el-tag></template>
            </el-table-column>
            <el-table-column label="状态联动" prop="statusLinkEnabled" width="100">
              <template slot-scope="scope">{{ scope.row.statusLinkEnabled === '1' ? '开启' : '关闭' }}</template>
            </el-table-column>
            <el-table-column label="告警联动" prop="alertLinkEnabled" width="100">
              <template slot-scope="scope">{{ scope.row.alertLinkEnabled === '1' ? '开启' : '关闭' }}</template>
            </el-table-column>
            <el-table-column label="操作" width="140" fixed="right">
              <template slot-scope="scope">
                <el-button type="text" size="mini" @click="openDependencyDialog(scope.row)" v-hasPermi="['information:application:edit']">编辑</el-button>
                <el-button type="text" size="mini" @click="removeDependency(scope.row)" v-hasPermi="['information:application:edit']">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="状态概览" name="overview">
          <el-descriptions v-if="detail.statusOverview" :column="4" border class="overview-box">
            <el-descriptions-item label="关联项目">{{ detail.statusOverview.projectCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="依赖数量">{{ detail.statusOverview.dependencyCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="开放告警">{{ detail.statusOverview.openAlertCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="异常依赖">{{ detail.statusOverview.abnormalDependencyCount || 0 }}</el-descriptions-item>
          </el-descriptions>
          <el-table :data="detail.statusOverview.dependencySummary || []" size="mini" class="overview-table">
            <el-table-column label="依赖类型" prop="dependencyType" min-width="120" />
            <el-table-column label="总数" prop="totalCount" width="100" />
            <el-table-column label="异常数" prop="abnormalCount" width="100" />
            <el-table-column label="状态联动" prop="statusLinkEnabledCount" width="100" />
            <el-table-column label="告警联动" prop="alertLinkEnabledCount" width="100" />
          </el-table>
          <el-table :data="detail.statusOverview.runtimeStatusList || []" size="mini" class="overview-table">
            <el-table-column label="依赖类型" prop="dependencyType" width="110" />
            <el-table-column label="目标名称" prop="targetName" min-width="180" />
            <el-table-column label="运行状态" prop="runtimeStatus" width="110">
              <template slot-scope="scope"><el-tag size="mini" :type="linkageTagType(scope.row.runtimeStatus)">{{ scope.row.runtimeStatus || '-' }}</el-tag></template>
            </el-table-column>
            <el-table-column label="目标状态" prop="targetStatus" min-width="150" />
            <el-table-column label="重要级别" prop="importanceLevel" width="110" />
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="告警事件" name="alerts">
          <el-table :data="detail.alerts" size="mini">
            <el-table-column label="事件标题" prop="eventTitle" min-width="200" />
            <el-table-column label="级别" prop="eventLevel" width="100" />
            <el-table-column label="状态" prop="eventStatus" width="110" />
            <el-table-column label="发生时间" prop="eventTime" width="170"><template slot-scope="scope"><span>{{ parseTime(scope.row.eventTime) }}</span></template></el-table-column>
            <el-table-column label="影响说明" prop="impactSummary" min-width="220" show-overflow-tooltip />
            <el-table-column label="操作" width="140" fixed="right">
              <template slot-scope="scope">
                <el-button type="text" size="mini" :disabled="scope.row.eventStatus === 'RESOLVED'" @click="ackAlert(scope.row)" v-hasPermi="['information:application:edit']">确认</el-button>
                <el-button type="text" size="mini" :disabled="scope.row.eventStatus === 'RESOLVED'" @click="resolveAlert(scope.row)" v-hasPermi="['information:application:edit']">恢复</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="通知记录" name="notices">
          <el-table :data="detail.notices" size="mini">
            <el-table-column label="接收人" prop="receiverName" width="120" />
            <el-table-column label="角色" prop="receiverRole" width="110" />
            <el-table-column label="渠道" prop="channelType" width="100" />
            <el-table-column label="发送状态" prop="sendStatus" width="100" />
            <el-table-column label="业务状态" prop="bizStatus" width="110" />
            <el-table-column label="发送时间" prop="sentTime" width="170"><template slot-scope="scope"><span>{{ parseTime(scope.row.sentTime) }}</span></template></el-table-column>
            <el-table-column label="内容摘要" prop="contentSummary" min-width="220" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <el-dialog :title="dependencyDialogTitle" :visible.sync="dependencyOpen" width="760px" append-to-body>
      <el-form ref="dependencyFormRef" :model="dependencyForm" :rules="dependencyRules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="依赖类型" prop="dependencyType"><el-select v-model="dependencyForm.dependencyType" @change="handleDependencyTypeChange"><el-option v-for="item in dependencyTypeOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="目标来源"><el-select v-model="dependencyForm.targetSource"><el-option label="内部" value="INTERNAL" /><el-option label="外部" value="EXTERNAL" /><el-option label="手工" value="MANUAL" /></el-select></el-form-item></el-col>
          <el-col v-if="currentTargetOptions.length" :span="12"><el-form-item label="选择目标"><el-select v-model="dependencyForm.targetId" filterable clearable @change="handleDependencyTargetChange"><el-option v-for="item in currentTargetOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="目标编码"><el-input v-model="dependencyForm.targetCode" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="目标名称" prop="targetName"><el-input v-model="dependencyForm.targetName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="目标键"><el-input v-model="dependencyForm.targetKey" placeholder="外部/手工依赖可填写" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="依赖方向"><el-select v-model="dependencyForm.dependencyDirection"><el-option label="上游" value="UPSTREAM" /><el-option label="下游" value="DOWNSTREAM" /><el-option label="双向" value="BIDIRECTIONAL" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="依赖角色"><el-select v-model="dependencyForm.dependencyRole"><el-option label="运行" value="RUNTIME" /><el-option label="数据" value="DATA" /><el-option label="访问" value="ACCESS" /><el-option label="网络" value="NETWORK" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="重要级别"><el-select v-model="dependencyForm.importanceLevel"><el-option label="低" value="LOW" /><el-option label="中" value="MEDIUM" /><el-option label="高" value="HIGH" /><el-option label="关键" value="CRITICAL" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="人工状态"><el-select v-model="dependencyForm.dependencyStatus"><el-option label="未知" value="UNKNOWN" /><el-option label="正常" value="NORMAL" /><el-option label="风险" value="RISK" /><el-option label="阻断" value="BLOCKED" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态联动"><el-switch v-model="dependencyForm.statusLinkEnabled" :active-value="'1'" :inactive-value="'0'" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="告警联动"><el-switch v-model="dependencyForm.alertLinkEnabled" :active-value="'1'" :inactive-value="'0'" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="备注"><el-input v-model="dependencyForm.remark" type="textarea" :rows="3" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer"><el-button type="primary" @click="submitDependency">确定</el-button><el-button @click="dependencyOpen = false">取消</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import {
  ackApplicationAlert,
  addApplication,
  addApplicationDependency,
  addApplicationProject,
  delApplication,
  delApplicationDependency,
  delApplicationProject,
  getApplication,
  getApplicationDetail,
  getApplicationStatisticsOverview,
  getApplicationStatusOverview,
  listApplication,
  listApplicationAlerts,
  listApplicationDependencies,
  listApplicationNotices,
  listApplicationProjects,
  recalculateApplicationStatus,
  resolveApplicationAlert,
  updateApplication,
  updateApplicationDependency
} from '@/api/information/application'
import { listNetwork } from '@/api/information/network'
import { listProject } from '@/api/information/project'
import { listResource } from '@/api/information/resource'

export default {
  name: 'InformationApplication',
  data() {
    return {
      loading: false,
      total: 0,
      ids: [],
      single: true,
      multiple: true,
      open: false,
      detailOpen: false,
      dependencyOpen: false,
      dialogTitle: '',
      dependencyDialogTitle: '',
      detailTab: 'basic',
      applicationList: [],
      projectOptions: [],
      resourceOptions: [],
      networkOptions: [],
      appOptions: [],
      statistics: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        applicationCode: undefined,
        applicationName: undefined,
        constructionSource: undefined,
        applicationStatus: undefined,
        linkageStatus: undefined,
        ownerName: undefined
      },
      form: {},
      detail: {
        application: null,
        projectRelations: [],
        dependencies: [],
        statusOverview: {},
        alerts: [],
        notices: []
      },
      projectRelationForm: {
        projectId: undefined,
        relationType: 'RELATED'
      },
      dependencyForm: {},
      constructionSourceOptions: [
        { label: '自建', value: 'SELF_BUILT' },
        { label: '采购', value: 'PURCHASED' },
        { label: '租赁', value: 'LEASED' },
        { label: '共建', value: 'CO_BUILT' }
      ],
      appStatusOptions: [
        { label: '建设中', value: 'BUILDING' },
        { label: '运行中', value: 'RUNNING' },
        { label: '告警中', value: 'ALERT' },
        { label: '已停用', value: 'DISABLED' }
      ],
      linkageStatusOptions: [
        { label: '未知', value: 'UNKNOWN' },
        { label: '正常', value: 'NORMAL' },
        { label: '风险', value: 'RISK' },
        { label: '阻断', value: 'BLOCKED' }
      ],
      dependencyTypeOptions: [
        { label: '资源', value: 'RESOURCE' },
        { label: '项目', value: 'PROJECT' },
        { label: '应用', value: 'APPLICATION' },
        { label: '网络', value: 'NETWORK' },
        { label: '其他', value: 'OTHER' }
      ],
      rules: {
        applicationCode: [{ required: true, message: '请输入应用编码', trigger: 'blur' }],
        applicationName: [{ required: true, message: '请输入应用名称', trigger: 'blur' }]
      },
      dependencyRules: {
        dependencyType: [{ required: true, message: '请选择依赖类型', trigger: 'change' }],
        targetName: [{ required: true, message: '请输入目标名称', trigger: 'blur' }]
      }
    }
  },
  computed: {
    statCards() {
      return [
        { label: '应用总数', value: this.statistics.totalCount || 0 },
        { label: '运行中', value: this.statistics.runningCount || 0 },
        { label: '建设中', value: this.statistics.buildCount || 0 },
        { label: '告警应用', value: this.statistics.alertCount || 0 },
        { label: '联动正常', value: this.statistics.normalLinkageCount || 0 },
        { label: '联动风险', value: this.statistics.riskLinkageCount || 0 }
      ]
    },
    currentTargetOptions() {
      if (this.dependencyForm.dependencyType === 'RESOURCE') {
        return this.resourceOptions.map(item => ({ value: item.resourceId, label: `${item.resourceCode} - ${item.resourceName}`, code: item.resourceCode, name: item.resourceName }))
      }
      if (this.dependencyForm.dependencyType === 'PROJECT') {
        return this.projectOptions.map(item => ({ value: item.projectId, label: this.formatProjectOption(item), code: item.projectCode, name: item.projectName }))
      }
      if (this.dependencyForm.dependencyType === 'APPLICATION') {
        return this.appOptions.map(item => ({ value: item.applicationId, label: `${item.applicationCode} - ${item.applicationName}`, code: item.applicationCode, name: item.applicationName }))
      }
      if (this.dependencyForm.dependencyType === 'NETWORK') {
        return this.networkOptions.map(item => ({ value: item.networkId, label: `${item.networkCode} - ${item.networkName}`, code: item.networkCode, name: item.networkName }))
      }
      return []
    }
  },
  created() {
    this.reset()
    this.resetDependencyForm()
    this.loadOptions()
    this.getList()
    this.getStatistics()
  },
  methods: {
    formatLabel(value, options) {
      const target = options.find(item => item.value === value)
      return target ? target.label : (value || '-')
    },
    formatProjectOption(item) {
      return item.projectCode ? `${item.projectCode} - ${item.projectName}` : item.projectName
    },
    appStatusTagType(value) {
      if (value === 'ALERT') return 'danger'
      if (value === 'RUNNING') return 'success'
      if (value === 'BUILDING') return 'warning'
      return 'info'
    },
    linkageTagType(value) {
      if (value === 'BLOCKED') return 'danger'
      if (value === 'RISK') return 'warning'
      if (value === 'NORMAL') return 'success'
      return 'info'
    },
    getList() {
      this.loading = true
      listApplication(this.queryParams).then(response => {
        this.applicationList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    getStatistics() {
      getApplicationStatisticsOverview(this.queryParams).then(response => {
        this.statistics = response.data || {}
      })
    },
    loadOptions() {
      listProject({ pageNum: 1, pageSize: 200 }).then(response => {
        this.projectOptions = response.rows || []
      })
      listResource({ pageNum: 1, pageSize: 200 }).then(response => {
        this.resourceOptions = response.rows || []
      })
      listNetwork({ pageNum: 1, pageSize: 200 }).then(response => {
        this.networkOptions = response.rows || []
      })
      listApplication({ pageNum: 1, pageSize: 200 }).then(response => {
        this.appOptions = response.rows || []
      })
    },
    emptyDetail() {
      return {
        application: null,
        projectRelations: [],
        dependencies: [],
        statusOverview: {},
        alerts: [],
        notices: []
      }
    },
    resetDetail() {
      this.detail = this.emptyDetail()
      this.projectRelationForm = { projectId: undefined, relationType: 'RELATED' }
    },
    reset() {
      this.form = {
        applicationId: undefined,
        applicationCode: undefined,
        applicationName: undefined,
        applicationType: undefined,
        classificationLevel: undefined,
        constructionSource: 'SELF_BUILT',
        applicationStatus: 'BUILDING',
        projectId: undefined,
        ownerOrg: undefined,
        ownerName: undefined,
        opsOwner: undefined,
        vendorName: undefined,
        vendorOwner: undefined,
        portalOwner: undefined,
        resourceOwner: undefined,
        dataOwner: undefined,
        securityOwner: undefined,
        accessUrl: undefined,
        techStack: undefined,
        resourceSummary: undefined,
        networkSummary: undefined,
        relationSummary: undefined
      }
      this.resetForm('form')
    },
    resetDependencyForm() {
      this.dependencyForm = {
        dependencyId: undefined,
        dependencyType: 'RESOURCE',
        targetSource: 'INTERNAL',
        targetId: undefined,
        targetCode: undefined,
        targetName: undefined,
        targetKey: undefined,
        dependencyDirection: 'UPSTREAM',
        dependencyRole: 'RUNTIME',
        importanceLevel: 'MEDIUM',
        dependencyStatus: 'UNKNOWN',
        statusLinkEnabled: '1',
        alertLinkEnabled: '1',
        remark: undefined
      }
      this.resetForm('dependencyFormRef')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
      this.getStatistics()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams.pageNum = 1
      this.queryParams.pageSize = 10
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.applicationId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.dialogTitle = '新增应用'
      this.open = true
    },
    handleUpdate(row) {
      this.reset()
      const applicationId = row ? row.applicationId : this.ids[0]
      getApplication(applicationId).then(response => {
        this.form = Object.assign({}, this.form, response.data)
        this.dialogTitle = '修改应用'
        this.open = true
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const action = this.form.applicationId ? updateApplication : addApplication
        action(this.form).then(() => {
          this.$modal.msgSuccess(this.form.applicationId ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
          this.getStatistics()
          this.loadOptions()
        })
      })
    },
    handleDelete(row) {
      const applicationId = row ? row.applicationId : this.ids.join(',')
      if (!applicationId) return
      this.$modal.confirm(`确认删除应用 ${applicationId} 吗？`).then(() => {
        return delApplication(applicationId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
        this.getStatistics()
        this.loadOptions()
      }).catch(() => {})
    },
    openDetail(row) {
      const applicationId = row ? row.applicationId : this.ids[0]
      if (!applicationId) return
      this.detailOpen = true
      this.detailTab = 'basic'
      this.loadDetail(applicationId)
    },
    loadDetail(applicationId) {
      this.resetDetail()
      Promise.all([
        getApplicationDetail(applicationId),
        listApplicationProjects(applicationId),
        listApplicationDependencies(applicationId),
        getApplicationStatusOverview(applicationId),
        listApplicationAlerts(applicationId),
        listApplicationNotices(applicationId)
      ]).then(([detailResp, projectResp, dependencyResp, statusResp, alertResp, noticeResp]) => {
        const detailData = detailResp.data || {}
        this.detail.application = detailData.application || null
        this.detail.projectRelations = projectResp.data || detailData.projectRelations || []
        this.detail.dependencies = dependencyResp.data || detailData.dependencyRelations || []
        this.detail.statusOverview = statusResp.data || detailData.statusOverview || {}
        this.detail.alerts = alertResp.data || detailData.alerts || []
        this.detail.notices = noticeResp.data || detailData.notices || []
        this.projectRelationForm = { projectId: undefined, relationType: 'RELATED' }
      }).catch(() => {
        this.resetDetail()
        this.$modal.msgError('应用详情加载失败，请稍后重试')
      })
    },
    recalculateSelected() {
      const row = this.applicationList.find(item => item.applicationId === this.ids[0])
      this.recalculateStatus(row)
    },
    recalculateStatus(row) {
      if (!row) return
      recalculateApplicationStatus(row.applicationId).then(() => {
        this.$modal.msgSuccess('状态重算成功')
        this.getList()
        this.getStatistics()
        if (this.detailOpen && this.detail.application && this.detail.application.applicationId === row.applicationId) {
          this.loadDetail(row.applicationId)
        }
      })
    },
    submitProjectRelation() {
      if (!this.detail.application || !this.projectRelationForm.projectId) {
        this.$modal.msgError('请选择项目')
        return
      }
      addApplicationProject(this.detail.application.applicationId, this.projectRelationForm).then(() => {
        this.$modal.msgSuccess('项目关联已新增')
        this.projectRelationForm = { projectId: undefined, relationType: 'RELATED' }
        this.loadDetail(this.detail.application.applicationId)
        this.getList()
      })
    },
    removeProjectRelation(row) {
      this.$modal.confirm(`确认移除项目 ${row.projectName} 吗？`).then(() => {
        return delApplicationProject(this.detail.application.applicationId, row.relId)
      }).then(() => {
        this.$modal.msgSuccess('移除成功')
        this.loadDetail(this.detail.application.applicationId)
        this.getList()
      }).catch(() => {})
    },
    openDependencyDialog(row) {
      this.resetDependencyForm()
      if (row && row.dependencyId) {
        this.dependencyForm = Object.assign({}, this.dependencyForm, row)
        this.dependencyDialogTitle = '编辑依赖'
      } else {
        this.dependencyDialogTitle = '新增依赖'
      }
      this.dependencyOpen = true
    },
    handleDependencyTypeChange() {
      this.dependencyForm.targetId = undefined
      this.dependencyForm.targetCode = undefined
      this.dependencyForm.targetName = undefined
      this.dependencyForm.targetKey = undefined
    },
    handleDependencyTargetChange(value) {
      const target = this.currentTargetOptions.find(item => item.value === value)
      if (!target) return
      this.dependencyForm.targetCode = target.code
      this.dependencyForm.targetName = target.name
    },
    submitDependency() {
      if (!this.detail.application) return
      this.$refs.dependencyFormRef.validate(valid => {
        if (!valid) return
        const applicationId = this.detail.application.applicationId
        const request = this.dependencyForm.dependencyId
          ? updateApplicationDependency(applicationId, this.dependencyForm.dependencyId, this.dependencyForm)
          : addApplicationDependency(applicationId, this.dependencyForm)
        request.then(() => {
          this.$modal.msgSuccess(this.dependencyForm.dependencyId ? '依赖已更新' : '依赖已新增')
          this.dependencyOpen = false
          this.loadDetail(applicationId)
          this.getList()
          this.getStatistics()
        })
      })
    },
    removeDependency(row) {
      this.$modal.confirm(`确认删除依赖 ${row.targetName} 吗？`).then(() => {
        return delApplicationDependency(this.detail.application.applicationId, row.dependencyId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.loadDetail(this.detail.application.applicationId)
        this.getList()
        this.getStatistics()
      }).catch(() => {})
    },
    ackAlert(row) {
      this.$modal.confirm(`确认告警 "${row.eventTitle}" 已进入处理吗？`).then(() => {
        return ackApplicationAlert(row.alertId, {})
      }).then(() => {
        this.$modal.msgSuccess('告警已确认')
        this.loadDetail(this.detail.application.applicationId)
        this.getList()
        this.getStatistics()
      }).catch(() => {})
    },
    resolveAlert(row) {
      this.$modal.confirm(`确认将告警 "${row.eventTitle}" 标记为已恢复吗？`).then(() => {
        return resolveApplicationAlert(row.alertId, {})
      }).then(() => {
        this.$modal.msgSuccess('告警已恢复')
        this.loadDetail(this.detail.application.applicationId)
        this.getList()
        this.getStatistics()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.stats-row {
  margin-bottom: 16px;
}

.stats-card {
  margin-bottom: 16px;
}

.stats-label {
  color: #909399;
  font-size: 13px;
}

.stats-value {
  margin-top: 10px;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.tab-toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.toolbar-select {
  width: 220px;
}

.overview-box {
  margin-bottom: 12px;
}

.overview-table {
  margin-top: 12px;
}
</style>
