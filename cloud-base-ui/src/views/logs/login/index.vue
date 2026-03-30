<template>
  <div class="log-container">
    <el-card>
      <template #header>
        <span>登录日志</span>
      </template>
      <el-form :model="queryParams" inline>
        <el-form-item label="登录账号">
          <el-input v-model="queryParams.username" placeholder="请输入账号" clearable />
        </el-form-item>
        <el-form-item label="IP地址">
          <el-input v-model="queryParams.ip" placeholder="请输入IP地址" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="成功" :value="0" />
            <el-option label="失败" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="登录账号" />
        <el-table-column prop="ip" label="IP地址" width="140" />
        <el-table-column prop="location" label="登录地点" />
        <el-table-column prop="userAgent" label="用户代理" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">
              {{ row.status === 0 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="errorMsg" label="提示消息" show-overflow-tooltip />
        <el-table-column prop="createTime" label="登录时间" width="180" />
      </el-table>
      <el-pagination
        style="margin-top: 20px; text-align: right"
        v-model:current-page="queryParams.current"
        v-model:page-size="queryParams.size"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @change="fetchData"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getLoginLogList } from '@/api/log'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const total = ref(0)
const loading = ref(false)

const queryParams = reactive({
  current: 1,
  size: 10,
  username: '',
  ip: '',
  status: null
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getLoginLogList(queryParams)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.current = 1
  fetchData()
}

const handleReset = () => {
  queryParams.username = ''
  queryParams.ip = ''
  queryParams.status = null
  handleQuery()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.log-container {
}
</style>
