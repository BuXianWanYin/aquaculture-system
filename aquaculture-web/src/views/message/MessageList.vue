<template>
  <div class="message-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <div>
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="unread-badge">
              <span>消息通知</span>
            </el-badge>
          </div>
          <div>
            <el-button type="success" @click="handleMarkAllRead" :disabled="unreadCount === 0">
              <el-icon><Check /></el-icon>
              全部标记已读
            </el-button>
            <el-button type="danger" @click="handleBatchDelete" :disabled="selectedMessages.length === 0">
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="消息类型">
          <el-select v-model="searchForm.messageType" placeholder="请选择消息类型" clearable style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="通知" value="通知" />
            <el-option label="提醒" value="提醒" />
            <el-option label="公告" value="公告" />
          </el-select>
        </el-form-item>
        <el-form-item label="消息状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="未读" :value="0" />
            <el-option label="已读" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="请输入标题或内容关键词" clearable style="width: 200px;" />
        </el-form-item>
        <el-form-item label="接收时间">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 400px;"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 消息表格 -->
      <el-table 
        :data="tableData" 
        v-loading="loading" 
        border 
        stripe 
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="messageTitle" label="消息标题" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <div style="display: flex; align-items: center;">
              <el-icon v-if="row.status === 0" style="color: #409eff; margin-right: 5px;">
                <CircleCheckFilled />
              </el-icon>
              <span :style="{ fontWeight: row.status === 0 ? 'bold' : 'normal' }">
                {{ row.messageTitle }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="messageContent" label="消息内容" min-width="250" show-overflow-tooltip />
        <el-table-column prop="messageType" label="消息类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getMessageTypeTagType(row.messageType)">
              {{ row.messageType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="senderName" label="发送人" min-width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="接收时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="readTime" label="阅读时间" min-width="180">
          <template #default="{ row }">
            {{ row.readTime ? formatDateTime(row.readTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
            <el-button 
              v-if="row.status === 0" 
              type="success" 
              link 
              size="small" 
              @click="handleMarkRead(row)"
            >
              标记已读
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 消息详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="消息详情"
      width="800px"
    >
      <el-descriptions :column="2" border v-if="messageDetail">
        <el-descriptions-item label="消息ID">{{ messageDetail.messageId }}</el-descriptions-item>
        <el-descriptions-item label="消息类型">
          <el-tag :type="getMessageTypeTagType(messageDetail.messageType)">
            {{ messageDetail.messageType }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="消息标题" :span="2">
          {{ messageDetail.messageTitle }}
        </el-descriptions-item>
        <el-descriptions-item label="发送人">{{ messageDetail.senderName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="接收人">{{ messageDetail.receiverName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="消息状态">
          <el-tag :type="messageDetail.status === 1 ? 'success' : 'warning'">
            {{ messageDetail.status === 1 ? '已读' : '未读' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="接收时间">
          {{ formatDateTime(messageDetail.createTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="阅读时间" v-if="messageDetail.readTime">
          {{ formatDateTime(messageDetail.readTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="关联业务类型" v-if="messageDetail.businessType">
          {{ messageDetail.businessType }}
        </el-descriptions-item>
        <el-descriptions-item label="关联业务ID" v-if="messageDetail.businessId">
          {{ messageDetail.businessId }}
        </el-descriptions-item>
        <el-descriptions-item label="消息内容" :span="2">
          <div style="word-break: break-all; white-space: pre-wrap;">{{ messageDetail.messageContent }}</div>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button v-if="messageDetail && messageDetail.status === 0" type="success" @click="handleMarkReadInDialog">
          标记已读
        </el-button>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Check, CircleCheckFilled } from '@element-plus/icons-vue'
import {
  getMessageList,
  getMessageById,
  markAsRead,
  markBatchAsRead,
  markAllAsRead,
  deleteMessage,
  deleteBatch,
  getUnreadCount
} from '@/api/message'
import { formatDateTime } from '@/utils/date'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const receiverId = computed(() => userStore.userInfo?.userId)

const loading = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const messageDetail = ref(null)
const dateRange = ref(null)
const selectedMessages = ref([])
const unreadCount = ref(0)

const searchForm = reactive({
  messageType: '',
  status: '',
  keyword: '',
  startTime: null,
  endTime: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 获取消息类型标签类型
const getMessageTypeTagType = (messageType) => {
  const typeMap = {
    '通知': 'info',
    '提醒': 'warning',
    '公告': 'success'
  }
  return typeMap[messageType] || ''
}

// 加载未读消息数量
const loadUnreadCount = async () => {
  if (!receiverId.value) return
  try {
    const res = await getUnreadCount(receiverId.value)
    if (res.code === 200) {
      unreadCount.value = res.data || 0
    }
  } catch (error) {
    console.error('加载未读消息数量失败', error)
  }
}

// 加载数据
const loadData = async () => {
  if (!receiverId.value) {
    ElMessage.warning('请先登录')
    return
  }
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      receiverId: receiverId.value,
      messageType: searchForm.messageType || undefined,
      status: searchForm.status !== '' ? searchForm.status : undefined,
      keyword: searchForm.keyword || undefined,
      startTime: searchForm.startTime || undefined,
      endTime: searchForm.endTime || undefined
    }
    const res = await getMessageList(params)
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  if (dateRange.value && dateRange.value.length === 2) {
    searchForm.startTime = dateRange.value[0]
    searchForm.endTime = dateRange.value[1]
  } else {
    searchForm.startTime = null
    searchForm.endTime = null
  }
  pagination.current = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.messageType = ''
  searchForm.status = ''
  searchForm.keyword = ''
  searchForm.startTime = null
  searchForm.endTime = null
  dateRange.value = null
  handleSearch()
}

// 查看详情
const handleView = async (row) => {
  try {
    const res = await getMessageById(row.messageId)
    if (res.code === 200) {
      messageDetail.value = res.data
      detailVisible.value = true
      // 如果消息未读，自动标记为已读
      if (res.data.status === 0) {
        await handleMarkRead(row)
      }
    }
  } catch (error) {
    ElMessage.error('加载详情失败')
  }
}

// 标记已读
const handleMarkRead = async (row) => {
  if (!receiverId.value) return
  try {
    const res = await markAsRead(row.messageId, receiverId.value)
    if (res.code === 200) {
      ElMessage.success('标记成功')
      row.status = 1
      row.readTime = new Date().toISOString()
      loadUnreadCount()
      loadData()
    }
  } catch (error) {
    ElMessage.error('标记失败')
  }
}

// 在对话框中标记已读
const handleMarkReadInDialog = async () => {
  if (!messageDetail.value || !receiverId.value) return
  try {
    const res = await markAsRead(messageDetail.value.messageId, receiverId.value)
    if (res.code === 200) {
      ElMessage.success('标记成功')
      messageDetail.value.status = 1
      messageDetail.value.readTime = new Date().toISOString()
      loadUnreadCount()
      loadData()
    }
  } catch (error) {
    ElMessage.error('标记失败')
  }
}

// 全部标记已读
const handleMarkAllRead = async () => {
  if (!receiverId.value) return
  try {
    await ElMessageBox.confirm('确定要将所有消息标记为已读吗？', '提示', {
      type: 'warning'
    })
    const res = await markAllAsRead(receiverId.value)
    if (res.code === 200) {
      ElMessage.success('全部标记成功')
      loadUnreadCount()
      loadData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('标记失败')
    }
  }
}

// 删除
const handleDelete = async (row) => {
  if (!receiverId.value) return
  try {
    await ElMessageBox.confirm('确定要删除该消息吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteMessage(row.messageId, receiverId.value)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadUnreadCount()
      loadData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (!receiverId.value || selectedMessages.value.length === 0) return
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedMessages.value.length} 条消息吗？`, '提示', {
      type: 'warning'
    })
    const messageIds = selectedMessages.value.map(msg => msg.messageId)
    const res = await deleteBatch(messageIds, receiverId.value)
    if (res.code === 200) {
      ElMessage.success('批量删除成功')
      selectedMessages.value = []
      loadUnreadCount()
      loadData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedMessages.value = selection
}

// 分页
const handleSizeChange = () => {
  loadData()
}

const handleCurrentChange = () => {
  loadData()
}

onMounted(() => {
  loadUnreadCount()
  loadData()
})
</script>

<style scoped>
.message-list {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.unread-badge {
  margin-right: 10px;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>

