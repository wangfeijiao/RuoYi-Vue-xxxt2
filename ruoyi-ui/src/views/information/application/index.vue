<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
      <el-form-item label="应用名称">
        <el-input
          v-model="queryParams.applicationName"
          placeholder="请输入应用名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="运行状态">
        <el-select v-model="queryParams.applicationStatus" placeholder="请选择状态" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="mini" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button size="mini" icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="mini" icon="el-icon-plus" @click="handleAdd" v-hasPermi="['information:application:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain size="mini" icon="el-icon-edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['information:application:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain size="mini" icon="el-icon-delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['information:application:remove']">删除</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="applicationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="应用编码" prop="applicationCode" min-width="120" />
      <el-table-column label="应用名称" prop="applicationName" min-width="160" />
      <el-table-column label="应用类型" prop="applicationType" width="120" />
      <el-table-column label="等保级别" prop="classificationLevel" width="120" />
      <el-table-column label="运行状态" prop="applicationStatus" width="120">
        <template slot-scope="scope">
          <el-tag size="mini">{{ scope.row.applicationStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="责任人" prop="ownerName" width="120" />
      <el-table-column label="关联项目" prop="projectName" min-width="140" />
      <el-table-column label="访问地址" prop="accessUrl" min-width="180" show-overflow-tooltip />
      <el-table-column label="操作" width="160" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button type="text" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="应用编码" prop="applicationCode"><el-input v-model="form.applicationCode" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="应用名称" prop="applicationName"><el-input v-model="form.applicationName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="应用类型"><el-input v-model="form.applicationType" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="等保级别"><el-input v-model="form.classificationLevel" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="运行状态">
              <el-select v-model="form.applicationStatus">
                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="关联项目ID"><el-input v-model="form.projectId" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="归属单位"><el-input v-model="form.ownerOrg" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="业务责任人"><el-input v-model="form.ownerName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="实施厂商"><el-input v-model="form.vendorName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="厂商负责人"><el-input v-model="form.vendorOwner" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="门户负责人"><el-input v-model="form.portalOwner" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="资源负责人"><el-input v-model="form.resourceOwner" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="数据负责人"><el-input v-model="form.dataOwner" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="安全负责人"><el-input v-model="form.securityOwner" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="访问地址"><el-input v-model="form.accessUrl" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="技术栈"><el-input v-model="form.techStack" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="资源摘要"><el-input type="textarea" :rows="2" v-model="form.resourceSummary" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="网络摘要"><el-input type="textarea" :rows="2" v-model="form.networkSummary" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="关系说明"><el-input type="textarea" :rows="3" v-model="form.relationSummary" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="open = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { addApplication, delApplication, getApplication, listApplication, updateApplication } from '@/api/information/application'

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
      title: '',
      applicationList: [],
      statusOptions: [
        { label: '建设中', value: 'BUILDING' },
        { label: '运行中', value: 'RUNNING' },
        { label: '告警中', value: 'ALERT' },
        { label: '已停用', value: 'DISABLED' }
      ],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        applicationName: undefined,
        applicationStatus: undefined
      },
      form: {},
      rules: {
        applicationCode: [{ required: true, message: '请输入应用编码', trigger: 'blur' }],
        applicationName: [{ required: true, message: '请输入应用名称', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listApplication(this.queryParams).then(response => {
        this.applicationList = response.rows
        this.total = response.total
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    reset() {
      this.form = {
        applicationId: undefined,
        applicationCode: undefined,
        applicationName: undefined,
        applicationStatus: 'BUILDING'
      }
      this.resetForm('form')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.applicationId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增应用'
    },
    handleUpdate(row) {
      this.reset()
      const applicationId = row ? row.applicationId : this.ids[0]
      getApplication(applicationId).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改应用'
      })
    },
    handleDelete(row) {
      const applicationId = row ? row.applicationId : this.ids.join(',')
      this.$modal.confirm(`确认删除应用 ${applicationId} 吗？`).then(() => {
        return delApplication(applicationId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        const action = this.form.applicationId ? updateApplication : addApplication
        action(this.form).then(() => {
          this.$modal.msgSuccess(this.form.applicationId ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    }
  }
}
</script>
