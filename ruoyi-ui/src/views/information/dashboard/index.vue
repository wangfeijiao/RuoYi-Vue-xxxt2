<template>
  <div class="app-container">
    <el-row :gutter="16" class="kpi-row">
      <el-col
        v-for="item in cards"
        :key="item.label"
        :xs="24"
        :sm="12"
        :md="8"
        :lg="4"
      >
        <el-card shadow="hover" class="kpi-card">
          <div class="label">{{ item.label }}</div>
          <div class="value">{{ item.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never">
          <div slot="header">待办概览</div>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="项目待验收">{{ overview.projectPendingAcceptance }}</el-descriptions-item>
            <el-descriptions-item label="数据待处理">{{ overview.pendingDataOrders }}</el-descriptions-item>
            <el-descriptions-item label="待执行工单">{{ overview.pendingExecutionOrders }}</el-descriptions-item>
            <el-descriptions-item label="全部待办">{{ overview.todoOrders }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never">
          <div slot="header">最近工单</div>
          <el-table :data="overview.recentOrders || []" size="mini">
            <el-table-column label="工单号" prop="workOrderNo" min-width="180" />
            <el-table-column label="业务域" prop="domainType" width="110" />
            <el-table-column label="状态" prop="orderStatus" width="130">
              <template slot-scope="scope">
                <el-tag size="mini">{{ scope.row.orderStatus }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="提交时间" prop="submittedTime" width="170">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.submittedTime) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getOverview } from '@/api/information/dashboard'

export default {
  name: 'InformationDashboard',
  data() {
    return {
      overview: {
        projectTotal: 0,
        projectPendingAcceptance: 0,
        resourceTotal: 0,
        resourceInUse: 0,
        applicationTotal: 0,
        applicationAlertCount: 0,
        dataAssetTotal: 0,
        pendingDataOrders: 0,
        networkTotal: 0,
        pendingExecutionOrders: 0,
        todoOrders: 0,
        recentOrders: []
      }
    }
  },
  computed: {
    cards() {
      return [
        { label: '项目总数', value: this.overview.projectTotal },
        { label: '资源在用', value: `${this.overview.resourceInUse}/${this.overview.resourceTotal}` },
        { label: '应用总数', value: this.overview.applicationTotal },
        { label: '应用告警', value: this.overview.applicationAlertCount },
        { label: '数据资产', value: this.overview.dataAssetTotal },
        { label: '网络资源', value: this.overview.networkTotal }
      ]
    }
  },
  created() {
    this.loadOverview()
  },
  methods: {
    loadOverview() {
      getOverview().then(response => {
        this.overview = response.data || this.overview
      })
    }
  }
}
</script>

<style scoped>
.kpi-row {
  margin-bottom: 16px;
}

.kpi-card {
  margin-bottom: 16px;
}

.kpi-card .label {
  color: #909399;
  font-size: 13px;
}

.kpi-card .value {
  margin-top: 10px;
  font-size: 26px;
  font-weight: 600;
  color: #303133;
}
</style>
