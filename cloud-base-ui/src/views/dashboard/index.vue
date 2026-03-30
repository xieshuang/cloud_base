<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon class="stat-icon" color="#409EFF"><User /></el-icon>
            <div class="stat-info">
              <div class="stat-value">1,024</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon class="stat-icon" color="#67C23A"><Goods /></el-icon>
            <div class="stat-info">
              <div class="stat-value">128</div>
              <div class="stat-label">角色总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon class="stat-icon" color="#E6A23C"><Menu /></el-icon>
            <div class="stat-info">
              <div class="stat-value">512</div>
              <div class="stat-label">菜单总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon class="stat-icon" color="#F56C6C"><Document /></el-icon>
            <div class="stat-info">
              <div class="stat-value">2,048</div>
              <div class="stat-label">日志总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
</script>

<style scoped lang="scss">
.dashboard {
  .stat-item {
    display: flex;
    align-items: center;
    .stat-icon {
      font-size: 48px;
      margin-right: 20px;
    }
    .stat-info {
      .stat-value {
        font-size: 28px;
        font-weight: bold;
        color: #333;
      }
      .stat-label {
        font-size: 14px;
        color: #999;
        margin-top: 5px;
      }
    }
  }
}
</style>
