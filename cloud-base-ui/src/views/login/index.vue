<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <span class="title">Cloud Base 登录</span>
        </div>
      </template>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="账号登录" name="account">
          <el-form ref="accountFormRef" :model="accountForm" :rules="accountRules" label-width="0">
            <el-form-item prop="username">
              <el-input 
                v-model="accountForm.username" 
                placeholder="请输入用户名"
                prefix-icon="User"
                size="large"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input 
                v-model="accountForm.password" 
                type="password" 
                placeholder="请输入密码"
                prefix-icon="Lock"
                size="large"
                @keyup.enter="handleAccountLogin"
              />
            </el-form-item>
            <el-form-item>
              <el-button 
                type="primary" 
                size="large" 
                style="width: 100%" 
                :loading="loading"
                @click="handleAccountLogin"
              >
                登 录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="小程序登录" name="mini">
          <div class="social-login">
            <div class="qr-placeholder">
              <el-icon size="60"><QuestionFilled /></el-icon>
              <p>请在微信小程序中打开</p>
              <p class="tips">在小程序中调用 wx.login() 获取 code</p>
            </div>
            <el-form ref="miniFormRef" :model="miniForm" :rules="miniRules" label-width="0">
              <el-form-item prop="code">
                <el-input 
                  v-model="miniForm.code" 
                  placeholder="请输入小程序授权code"
                  size="large"
                />
              </el-form-item>
              <el-form-item>
                <el-button 
                  type="primary" 
                  size="large" 
                  style="width: 100%" 
                  :loading="loading"
                  @click="handleMiniLogin"
                >
                  小程序登录
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
        <el-tab-pane label="公众号登录" name="mp">
          <div class="social-login">
            <el-button type="primary" size="large" style="width: 100%" @click="handleMpAuthorize">
              授权登录
            </el-button>
            <p class="tips">点击后将跳转至微信授权页面</p>
          </div>
        </el-tab-pane>
      </el-tabs>
      <div class="tips">
        <p>默认账号: admin / admin123</p>
      </div>
    </el-card>

    <el-dialog v-model="bindDialogVisible" title="绑定账号" width="400px">
      <el-form ref="bindFormRef" :model="bindForm" :rules="bindRules" label-width="80px">
        <el-alert
          title="该微信账号尚未绑定系统账号"
          type="info"
          :closable="false"
          style="margin-bottom: 20px;"
        />
        <el-form-item label="用户名" prop="username">
          <el-input v-model="bindForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="bindForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bindDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBindAccount">绑定并登录</el-button>
        <el-button type="success" @click="handleCreateAccount">创建新账号</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/api/auth'
import { weixinMiniLogin, weixinMiniBind, weixinMiniCreate } from '@/api/social'
import { ElMessage } from 'element-plus'
import { QuestionFilled } from '@element-plus/icons-vue'

const router = useRouter()
const activeTab = ref('account')
const loading = ref(false)
const bindDialogVisible = ref(false)

const accountFormRef = ref()
const miniFormRef = ref()
const bindFormRef = ref()

const accountForm = reactive({
  username: 'admin',
  password: 'admin123'
})

const miniForm = reactive({
  code: '',
  openid: '',
  unionid: '',
  socialType: 1
})

const bindForm = reactive({
  username: '',
  password: '',
  openid: '',
  unionid: '',
  socialType: 1,
  nickname: ''
})

const accountRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const miniRules = {
  code: [{ required: true, message: '请输入授权code', trigger: 'blur' }]
}

const bindRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleAccountLogin = async () => {
  const valid = await accountFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    const res = await login(accountForm)
    localStorage.setItem('token', res.data.token)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleMiniLogin = async () => {
  const valid = await miniFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    const res = await weixinMiniLogin({ code: miniForm.code })
    if (res.data.needBind) {
      miniForm.openid = res.data.openid
      miniForm.unionid = res.data.unionid
      miniForm.socialType = res.data.socialType
      bindForm.openid = res.data.openid
      bindForm.unionid = res.data.unionid
      bindForm.socialType = res.data.socialType
      bindDialogVisible.value = true
    } else {
      localStorage.setItem('token', res.data.token)
      ElMessage.success('登录成功')
      router.push('/')
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleMpAuthorize = () => {
  const appid = 'your_mp_appid'
  const redirectUri = encodeURIComponent(window.location.origin + '/#/auth/weixin/callback')
  const url = `https://open.weixin.qq.com/connect/oauth2/authorize?appid=${appid}&redirect_uri=${redirectUri}&response_type=code&scope=snsapi_base#wechat_redirect`
  window.location.href = url
}

const handleBindAccount = async () => {
  const valid = await bindFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    const data = {
      openid: bindForm.openid,
      username: bindForm.username,
      password: bindForm.password,
      unionid: bindForm.unionid
    }
    const res = await weixinMiniBind(data)
    localStorage.setItem('token', res.data.token)
    ElMessage.success('绑定并登录成功')
    bindDialogVisible.value = false
    router.push('/')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleCreateAccount = async () => {
  loading.value = true
  try {
    const data = {
      openid: bindForm.openid,
      unionid: bindForm.unionid,
      nickname: bindForm.nickname || '微信用户'
    }
    const res = await weixinMiniCreate(data)
    localStorage.setItem('token', res.data.token)
    ElMessage.success('创建账号并绑定成功')
    bindDialogVisible.value = false
    router.push('/')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 420px;
  .card-header {
    text-align: center;
    .title {
      font-size: 24px;
      font-weight: bold;
      color: #333;
    }
  }
}

.social-login {
  padding: 10px 0;
  .qr-placeholder {
    text-align: center;
    padding: 20px 0;
    color: #999;
    p {
      margin: 10px 0;
    }
  }
}

.tips {
  text-align: center;
  color: #999;
  font-size: 12px;
  margin-top: 10px;
}
</style>