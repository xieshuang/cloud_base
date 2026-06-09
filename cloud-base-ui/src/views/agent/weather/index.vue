<template>
  <div class="weather-container">
    <div class="chat-panel">
      <!-- 消息列表区域 -->
      <div class="message-list" ref="messageListRef">
        <div v-if="messages.length === 0" class="empty-hint">
          <el-icon :size="48" color="#c0c4cc"><Sunny /></el-icon>
          <p>我是天气查询助手，可以帮你查询全国各地的实时天气和天气预报</p>
          <p class="sub-hint">试试问：今天北京天气怎么样？</p>
        </div>

        <div
          v-for="(msg, index) in messages"
          :key="index"
          :class="['message-item', msg.role === 'user' ? 'user-message' : 'ai-message']"
        >
          <div class="message-avatar">
            <el-avatar v-if="msg.role === 'user'" :size="36" icon="UserFilled" />
            <el-avatar v-else :size="36" :src="aiAvatar" />
          </div>
          <div class="message-content">
            <div class="message-role">{{ msg.role === 'user' ? '我' : '天气助手' }}</div>
            <div class="message-text" v-html="formatMessage(msg.content)"></div>
            <div class="message-time">{{ msg.time }}</div>
          </div>
        </div>

        <!-- 加载动画 -->
        <div v-if="loading" class="message-item ai-message">
          <div class="message-avatar">
            <el-avatar :size="36" :src="aiAvatar" />
          </div>
          <div class="message-content">
            <div class="message-role">天气助手</div>
            <div class="typing-indicator">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="input-area">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="2"
          placeholder="请输入您想查询的天气信息，如：今天北京天气怎么样？"
          resize="none"
          :disabled="loading"
          @keydown.enter.exact.prevent="handleSend"
        />
        <el-button
          type="primary"
          :icon="Promotion"
          :loading="loading"
          :disabled="!inputMessage.trim()"
          @click="handleSend"
        >
          发送
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Sunny, Promotion } from '@element-plus/icons-vue'
import { weatherChat } from '@/api/agent'
import { marked } from 'marked'

// 配置 marked 选项
marked.setOptions({
  breaks: true,        // 将单个换行符转换为 <br>
  gfm: true            // 启用 GitHub Flavored Markdown
})

// AI 头像 SVG（简单天气图标）
const aiAvatar = 'data:image/svg+xml;base64,' + btoa(`
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 64 64">
  <circle cx="32" cy="32" r="30" fill="#409EFF"/>
  <circle cx="28" cy="28" r="8" fill="#FFD700"/>
  <path d="M10 40 Q20 32 32 40 Q44 48 54 40" stroke="#FFD700" stroke-width="3" fill="none"/>
</svg>`)

// 消息列表
const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const messageListRef = ref(null)

/**
 * 使用 marked 将 markdown 文本解析为 HTML
 */
const formatMessage = (text) => {
  if (!text) return ''
  return marked.parse(text)
}

/**
 * 获取当前时间字符串
 */
const getCurrentTime = () => {
  const now = new Date()
  return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
}

/**
 * 滚动到消息列表底部
 */
const scrollToBottom = () => {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  })
}

/**
 * 发送消息
 */
const handleSend = async () => {
  const content = inputMessage.value.trim()
  if (!content || loading.value) return

  // 添加用户消息
  messages.value.push({
    role: 'user',
    content,
    time: getCurrentTime()
  })
  inputMessage.value = ''
  loading.value = true
  scrollToBottom()

  try {
    const res = await weatherChat({ message: content })
    // 后端返回 { code: 200, data: "回复文本", message: "..." }
    const reply = res.data || '抱歉，未能获取天气信息，请稍后再试。'
    messages.value.push({
      role: 'ai',
      content: reply,
      time: getCurrentTime()
    })
  } catch (error) {
    messages.value.push({
      role: 'ai',
      content: '抱歉，天气查询服务暂时不可用，请稍后再试。',
      time: getCurrentTime()
    })
    ElMessage.error('查询失败：' + (error.message || '网络异常'))
  } finally {
    loading.value = false
    scrollToBottom()
  }
}
</script>

<style scoped lang="scss">
.weather-container {
  height: calc(100vh - 120px);
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.chat-panel {
  width: 100%;
  max-width: 800px;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

// === 消息列表 ===
.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #fafafa;

  .empty-hint {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    color: #909399;

    p {
      margin-top: 16px;
      font-size: 15px;
    }

    .sub-hint {
      font-size: 13px;
      color: #c0c4cc;
      margin-top: 8px;
    }
  }
}

// === 消息项 ===
.message-item {
  display: flex;
  margin-bottom: 20px;

  .message-avatar {
    flex-shrink: 0;
    margin-right: 10px;
  }

  .message-content {
    max-width: 75%;

    .message-role {
      font-size: 12px;
      color: #909399;
      margin-bottom: 4px;
    }

    .message-text {
      padding: 12px 16px;
      border-radius: 8px;
      font-size: 14px;
      line-height: 1.8;
      word-break: break-word;
    }

    .message-time {
      font-size: 12px;
      color: #c0c4cc;
      margin-top: 4px;
    }
  }
}

// AI 消息样式（左侧）
.ai-message {
  .message-text {
    background: #fff;
    border: 1px solid #e4e7ed;
  }
}

// 用户消息样式（右侧）
.user-message {
  flex-direction: row-reverse;
  text-align: right;

  .message-avatar {
    margin-right: 0;
    margin-left: 10px;
  }

  .message-text {
    background: #409EFF;
    color: #fff;
  }

  .message-time {
    text-align: right;
  }
}

// === 输入动画 ===
.typing-indicator {
  padding: 12px 16px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 6px;

  span {
    width: 8px;
    height: 8px;
    background: #909399;
    border-radius: 50%;
    animation: typing 1.4s ease-in-out infinite;

    &:nth-child(2) {
      animation-delay: 0.2s;
    }

    &:nth-child(3) {
      animation-delay: 0.4s;
    }
  }
}

@keyframes typing {
  0%, 60%, 100% {
    opacity: 0.3;
    transform: scale(0.8);
  }
  30% {
    opacity: 1;
    transform: scale(1);
  }
}

// === Markdown 渲染内容样式 ===
.ai-message .message-text {
  // 标题
  :deep(h1) { font-size: 20px; margin: 0 0 8px 0; }
  :deep(h2) { font-size: 18px; margin: 0 0 6px 0; }
  :deep(h3) { font-size: 16px; margin: 0 0 4px 0; }
  :deep(h4), :deep(h5), :deep(h6) { font-size: 14px; margin: 0 0 4px 0; }

  // 段落
  :deep(p) { margin: 0 0 8px 0; }
  :deep(p:last-child) { margin-bottom: 0; }

  // 粗体
  :deep(strong) { font-weight: 600; color: #303133; }

  // 行内代码
  :deep(code) {
    padding: 2px 6px;
    background: #f5f7fa;
    border: 1px solid #e4e7ed;
    border-radius: 3px;
    font-family: 'Consolas', 'Monaco', monospace;
    font-size: 13px;
    color: #e6425b;
  }

  // 代码块
  :deep(pre) {
    margin: 8px 0;
    padding: 12px;
    background: #f5f7fa;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    overflow-x: auto;

    code {
      padding: 0;
      background: none;
      border: none;
      color: #303133;
    }
  }

  // 无序列表
  :deep(ul), :deep(ol) {
    margin: 4px 0;
    padding-left: 20px;
  }

  :deep(li) {
    margin: 2px 0;
  }

  // 表格
  :deep(table) {
    width: 100%;
    margin: 8px 0;
    border-collapse: collapse;
    font-size: 13px;

    th, td {
      padding: 6px 10px;
      border: 1px solid #e4e7ed;
      text-align: left;
    }

    th {
      background: #f5f7fa;
      font-weight: 600;
    }

    tr:nth-child(even) {
      background: #fafafa;
    }
  }

  // 引用
  :deep(blockquote) {
    margin: 8px 0;
    padding: 4px 12px;
    border-left: 3px solid #409EFF;
    background: #f5f7fa;
    color: #606266;
  }

  // 链接
  :deep(a) {
    color: #409EFF;
    text-decoration: none;
    &:hover { text-decoration: underline; }
  }

  // 水平线
  :deep(hr) {
    margin: 12px 0;
    border: none;
    border-top: 1px solid #e4e7ed;
  }
}

// === 输入区域 ===
.input-area {
  display: flex;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #ebeef5;
  background: #fff;

  .el-textarea {
    flex: 1;
  }

  .el-button {
    align-self: flex-end;
    height: 40px;
  }
}
</style>
