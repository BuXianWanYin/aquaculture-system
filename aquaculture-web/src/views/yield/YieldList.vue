<template>
  <div class="yield-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>产量统计管理</span>
          <el-button v-if="hasPermission('yield:add')" type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增产量统计
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="养殖计划">
          <el-select v-model="searchForm.planId" placeholder="请选择计划" clearable filterable style="width: 180px;">
            <el-option 
              v-for="plan in planList" 
              :key="plan.planId" 
              :label="plan.planName" 
              :value="plan.planId" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属区域">
          <el-select v-model="searchForm.areaId" placeholder="请选择区域" clearable filterable style="width: 180px;">
            <el-option 
              v-for="area in areaList" 
              :key="area.areaId" 
              :label="area.areaName" 
              :value="area.areaId" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="养殖品种">
          <el-select v-model="searchForm.breedId" placeholder="请选择品种" clearable filterable style="width: 180px;">
            <el-option 
              v-for="breed in breedList" 
              :key="breed.breedId" 
              :label="breed.breedName" 
              :value="breed.breedId" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已驳回" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="planId" label="养殖计划" min-width="150">
          <template #default="{ row }">
            {{ getPlanName(row.planId) }}
          </template>
        </el-table-column>
        <el-table-column prop="areaId" label="所属区域" min-width="120">
          <template #default="{ row }">
            {{ getAreaName(row.areaId) }}
          </template>
        </el-table-column>
        <el-table-column prop="breedId" label="养殖品种" min-width="120">
          <template #default="{ row }">
            {{ getBreedName(row.breedId) }}
          </template>
        </el-table-column>
        <el-table-column prop="actualYield" label="实际产量(kg)" min-width="120" />
        <el-table-column prop="specification" label="规格" min-width="120" />
        <el-table-column prop="statisticsDate" label="统计日期" min-width="120">
          <template #default="{ row }">
            {{ formatDate(row.statisticsDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="catchTime" label="捕捞时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.catchTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="managerId" label="负责人" min-width="120">
          <template #default="{ row }">
            {{ getManagerName(row.managerId) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">待审核</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">已通过</el-tag>
            <el-tag v-else-if="row.status === 2" type="danger">已驳回</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="330" fixed="right">
          <template #default="{ row }">
            <el-button v-if="hasPermission('yield:edit')" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="hasPermission('yield:audit') && row.status === 0" type="success" link size="small" @click="handleAudit(row)">审核</el-button>
            <el-button v-if="hasPermission('yield:evidence:add')" type="warning" link size="small" @click="handleUploadEvidence(row)">上传凭证</el-button>
            <el-button v-if="hasPermission('yield:evidence:view')" type="info" link size="small" @click="handleViewEvidence(row)">查看凭证</el-button>
            <el-button v-if="hasPermission('yield:delete')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
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
    
    <!-- 产量统计表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="yieldFormRef"
        :model="yieldForm"
        :rules="yieldRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="养殖计划" prop="planId">
              <el-select v-model="yieldForm.planId" placeholder="请选择计划" style="width: 100%;" filterable @change="handlePlanChange">
                <el-option 
                  v-for="plan in planList" 
                  :key="plan.planId" 
                  :label="plan.planName" 
                  :value="plan.planId" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属区域" prop="areaId">
              <el-select v-model="yieldForm.areaId" placeholder="请选择区域" style="width: 100%;" filterable :disabled="!!yieldForm.planId">
                <el-option 
                  v-for="area in areaList" 
                  :key="area.areaId" 
                  :label="area.areaName" 
                  :value="area.areaId" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="养殖品种" prop="breedId">
              <el-select v-model="yieldForm.breedId" placeholder="请选择品种" style="width: 100%;" filterable :disabled="!!yieldForm.planId">
                <el-option 
                  v-for="breed in breedList" 
                  :key="breed.breedId" 
                  :label="breed.breedName" 
                  :value="breed.breedId" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实际产量(kg)" prop="actualYield">
              <el-input-number v-model="yieldForm.actualYield" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="规格">
          <el-select v-model="yieldForm.specification" placeholder="请选择规格" style="width: 100%;" filterable allow-create>
            <el-option label="200g以下" value="200g以下" />
            <el-option label="200-300g" value="200-300g" />
            <el-option label="300-500g" value="300-500g" />
            <el-option label="500-800g" value="500-800g" />
            <el-option label="800-1000g" value="800-1000g" />
            <el-option label="1000-1500g" value="1000-1500g" />
            <el-option label="1500g以上" value="1500g以上" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="统计日期" prop="statisticsDate">
              <el-date-picker 
                v-model="yieldForm.statisticsDate" 
                type="date" 
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%;" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="捕捞时间">
              <el-date-picker 
                v-model="yieldForm.catchTime" 
                type="datetime" 
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%;" 
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="负责人" prop="managerId">
          <el-select v-model="yieldForm.managerId" placeholder="请选择负责人" style="width: 100%;" filterable>
            <el-option 
              v-for="user in userList" 
              :key="user.userId" 
              :label="user.realName || user.username" 
              :value="user.userId" 
            />
          </el-select>
        </el-form-item>
        
        <!-- 凭证上传区域 -->
        <el-form-item label="产量凭证" v-if="hasPermission('yield:evidence:add')">
          <el-upload
            ref="formUploadRef"
            :action="formUploadAction"
            :headers="formUploadHeaders"
            :data="formUploadData"
            :on-success="handleFormUploadSuccess"
            :on-error="handleFormUploadError"
            :on-remove="handleFormUploadRemove"
            :before-upload="beforeUpload"
            :file-list="formFileList"
            :limit="5"
            :multiple="true"
            list-type="picture-card"
            :on-preview="handleFormPicturePreview"
            :disabled="false"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">
                只能上传jpg/png文件，且不超过5MB，最多上传5个文件
                <span v-if="!yieldForm.yieldId && !isEdit" style="color: #409eff;">
                  （可以先上传凭证，保存产量统计后将自动关联）
                </span>
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="产量凭证" v-else>
          <el-alert type="info" :closable="false">
            您没有上传凭证的权限
          </el-alert>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 审核对话框 -->
    <el-dialog
      v-model="auditDialogVisible"
      title="产量统计审核"
      width="500px"
    >
      <el-form label-width="100px">
        <el-form-item label="实际产量">
          <el-input v-model="currentYield.actualYield" disabled />
        </el-form-item>
        <el-form-item label="审核意见" prop="auditOpinion">
          <el-input v-model="auditForm.auditOpinion" type="textarea" :rows="4" placeholder="请输入审核意见" />
        </el-form-item>
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.status">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAuditSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 凭证查看对话框 -->
    <el-dialog
      v-model="evidenceDialogVisible"
      title="产量凭证"
      width="800px"
    >
      <div v-if="evidenceList.length === 0" style="text-align: center; padding: 40px; color: #909399;">
        暂无凭证
      </div>
      <div v-else class="evidence-grid">
        <div v-for="(item, index) in evidenceList" :key="item.evidenceId" class="evidence-item">
          <div class="evidence-image-wrapper">
            <el-image
              :src="getImageUrl(item.filePath)"
              :preview-src-list="evidenceList.map(e => getImageUrl(e.filePath))"
              :initial-index="index"
              fit="cover"
              class="evidence-image"
              :preview-teleported="true"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </div>
          <div class="evidence-info">
            <div class="evidence-meta">
              <span class="evidence-time">{{ formatDateTime(item.uploadTime) }}</span>
              <span class="evidence-size" v-if="item.fileSize">
                {{ formatFileSize(item.fileSize) }}
              </span>
            </div>
            <el-button 
              type="danger" 
              size="small" 
              @click="handleDeleteEvidence(item)"
              style="margin-top: 8px;"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="evidenceDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
    
    <!-- 凭证上传对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      :title="`为产量统计上传凭证`"
      width="800px"
      @close="handleUploadDialogClose"
    >
      <el-upload
        v-if="hasPermission('yield:evidence:add')"
        ref="uploadRef"
        :action="uploadAction"
        :headers="uploadHeaders"
        :data="uploadData"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :on-progress="handleUploadProgress"
        :before-upload="beforeUpload"
        :file-list="fileList"
        :limit="5"
        :multiple="true"
        list-type="picture-card"
        :on-preview="handlePictureCardPreview"
      >
        <el-icon><Plus /></el-icon>
        <template #tip>
          <div class="el-upload__tip">
            只能上传jpg/png文件，且不超过5MB，最多上传5个文件
          </div>
        </template>
      </el-upload>
      <el-alert v-else type="warning" :closable="false">
        您没有上传凭证的权限
      </el-alert>
      <div v-if="uploadProgress > 0" style="margin-top: 10px;">
        <el-progress :percentage="uploadProgress" />
      </div>
      
      <!-- 已上传的凭证预览 -->
      <div v-if="uploadedImages.length > 0" style="margin-top: 20px;">
        <div style="margin-bottom: 10px; font-weight: bold;">已上传的凭证：</div>
        <div class="uploaded-images-grid">
          <div v-for="(img, index) in uploadedImages" :key="index" class="uploaded-image-item">
            <el-image
              :src="getImageUrl(img.filePath)"
              :preview-src-list="uploadedImages.map(i => getImageUrl(i.filePath))"
              :initial-index="index"
              fit="cover"
              class="uploaded-image"
              :preview-teleported="true"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="uploaded-image-info">
              <span>{{ img.originalFilename || '图片' }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="uploadDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
    
    <!-- 图片预览对话框 -->
    <el-dialog v-model="previewDialogVisible" title="图片预览" width="800px">
      <el-image
        :src="previewImageUrl"
        fit="contain"
        style="width: 100%; height: 500px;"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled, Plus, Picture } from '@element-plus/icons-vue'
import { getYieldList, saveYield, updateYield, deleteYield, auditYield } from '@/api/yield'
import { getEvidenceList, deleteEvidence, uploadEvidence, saveEvidence } from '@/api/yieldEvidence'
import { getAllPlans } from '@/api/plan'
import { getAllAreas } from '@/api/area'
import { getAllBreeds } from '@/api/breed'
import { getUserListForSelect } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { formatDateTime, formatDate } from '@/utils/date'
import { usePermission } from '@/composables/usePermission'

const userStore = useUserStore()
const { hasPermission } = usePermission()

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const auditDialogVisible = ref(false)
const evidenceDialogVisible = ref(false)
const dialogTitle = ref('新增产量统计')
const isEdit = ref(false)
const yieldFormRef = ref(null)
const currentYield = ref({})
const evidenceList = ref([])
const uploadDialogVisible = ref(false)
const uploadRef = ref(null)
const fileList = ref([])
const uploadProgress = ref(0)
const currentYieldId = ref(null)
const uploadedImages = ref([])
const previewDialogVisible = ref(false)
const previewImageUrl = ref('')
const formUploadRef = ref(null)
const formFileList = ref([])
const formUploadedFiles = ref([]) // 表单中已上传的文件信息
const planList = ref([])
const areaList = ref([])
const breedList = ref([])
const userList = ref([])

const searchForm = reactive({
  planId: null,
  areaId: null,
  breedId: null,
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const yieldForm = reactive({
  yieldId: null,
  planId: null,
  areaId: null,
  breedId: null,
  actualYield: null,
  specification: '',
  statisticsDate: null,
  catchTime: null,
  managerId: null,
  status: 0,
  creatorId: null
})

const auditForm = reactive({
  yieldId: null,
  auditorId: null,
  auditOpinion: '',
  status: 1
})

const yieldRules = {
  planId: [
    { required: true, message: '请选择养殖计划', trigger: 'change' }
  ],
  areaId: [
    { required: true, message: '请选择所属区域', trigger: 'change' }
  ],
  breedId: [
    { required: true, message: '请选择养殖品种', trigger: 'change' }
  ],
  actualYield: [
    { required: true, message: '请输入实际产量', trigger: 'blur' }
  ],
  statisticsDate: [
    { required: true, message: '请选择统计日期', trigger: 'change' }
  ],
  managerId: [
    { required: true, message: '请选择负责人', trigger: 'change' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getYieldList({
      current: pagination.current,
      size: pagination.size,
      planId: searchForm.planId || undefined,
      areaId: searchForm.areaId || undefined,
      breedId: searchForm.breedId || undefined,
      status: searchForm.status
    })
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

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.planId = null
  searchForm.areaId = null
  searchForm.breedId = null
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增产量统计'
  dialogVisible.value = true
  resetForm()
}

const handleEdit = async (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑产量统计'
  Object.assign(yieldForm, {
    yieldId: row.yieldId,
    planId: row.planId,
    areaId: row.areaId,
    breedId: row.breedId,
    actualYield: row.actualYield,
    specification: row.specification || '',
    statisticsDate: row.statisticsDate,
    catchTime: row.catchTime,
    managerId: row.managerId,
    status: row.status,
    creatorId: row.creatorId
  })
  dialogVisible.value = true
  // 加载已上传的凭证
  await loadFormEvidence(row.yieldId)
}

// 处理养殖计划选择变化
const handlePlanChange = (planId) => {
  if (!planId) {
    // 如果清空了选择，也清空区域和品种
    yieldForm.areaId = null
    yieldForm.breedId = null
    return
  }
  
  // 从计划列表中查找选中的计划
  const selectedPlan = planList.value.find(plan => plan.planId === planId)
  if (selectedPlan) {
    // 自动填充所属区域和养殖品种
    yieldForm.areaId = selectedPlan.areaId || null
    yieldForm.breedId = selectedPlan.breedId || null
  }
}

const handleAudit = (row) => {
  currentYield.value = { ...row }
  auditForm.yieldId = row.yieldId
  auditForm.auditorId = userStore.userInfo?.userId
  auditForm.auditOpinion = ''
  auditForm.status = 1
  auditDialogVisible.value = true
}

const handleViewEvidence = async (row) => {
  try {
    const res = await getEvidenceList(row.yieldId)
    if (res.code === 200) {
      evidenceList.value = res.data || []
      evidenceDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('加载凭证列表失败')
  }
}

// 打开上传对话框时，加载已上传的凭证
const loadUploadedEvidence = async () => {
  if (!currentYieldId.value) return
  try {
    const res = await getEvidenceList(currentYieldId.value)
    if (res.code === 200) {
      uploadedImages.value = (res.data || []).map(item => ({
        filePath: item.filePath,
        originalFilename: item.filePath.split('/').pop(),
        fileType: item.fileType,
        fileSize: item.fileSize
      }))
    }
  } catch (error) {
    console.error('加载已上传凭证失败', error)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该产量统计吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteYield(row.yieldId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleDeleteEvidence = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该凭证吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteEvidence(row.evidenceId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      // 刷新凭证列表
      if (evidenceDialogVisible.value) {
        handleViewEvidence({ yieldId: row.yieldId })
      }
      // 如果在上传对话框中，刷新已上传图片列表
      if (uploadDialogVisible.value && currentYieldId.value === row.yieldId) {
        await loadUploadedEvidence()
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  if (!yieldFormRef.value) return
  await yieldFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        yieldForm.creatorId = userStore.userInfo?.userId
        let res
        let savedYieldId = null
        
        if (isEdit.value) {
          // 编辑模式
          res = await updateYield(yieldForm)
          if (res.code === 200) {
            savedYieldId = yieldForm.yieldId
          }
        } else {
          // 新增模式
          res = await saveYield(yieldForm)
          if (res.code === 200) {
            // 从返回的数据中获取yieldId
            if (res.data) {
              if (typeof res.data === 'object') {
                // 如果返回的是对象，尝试获取yieldId
                savedYieldId = res.data.yieldId || res.data.id || null
              } else if (typeof res.data === 'number') {
                savedYieldId = res.data
              }
            }
            // 如果从返回数据中获取不到，尝试从yieldForm中获取（MyBatis Plus会自动填充）
            if (!savedYieldId && yieldForm.yieldId) {
              savedYieldId = yieldForm.yieldId
            }
          }
        }
        
        if (res.code === 200) {
          // 保存所有已上传的凭证（新增或编辑时）
          if (formUploadedFiles.value.length > 0 && savedYieldId) {
            try {
              await saveFormEvidence(savedYieldId)
            } catch (error) {
              console.error('保存凭证失败', error)
              // 即使凭证保存失败，也不影响主流程
            }
          } else if (formUploadedFiles.value.length > 0 && !savedYieldId) {
            // 如果上传了凭证但获取不到yieldId，提示用户
            ElMessage.warning('产量统计已保存，但凭证关联失败，请稍后手动关联凭证')
          }
          
          ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
          dialogVisible.value = false
          loadData()
        }
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

const handleAuditSubmit = async () => {
  try {
    const res = await auditYield(auditForm)
    if (res.code === 200) {
      ElMessage.success('审核成功')
      auditDialogVisible.value = false
      loadData()
    }
  } catch (error) {
    ElMessage.error(error.message || '审核失败')
  }
}

const resetForm = () => {
  Object.assign(yieldForm, {
    yieldId: null,
    planId: null,
    areaId: null,
    breedId: null,
    actualYield: null,
    specification: '',
    statisticsDate: null,
    catchTime: null,
    managerId: null,
    status: 0,
    creatorId: null
  })
  yieldFormRef.value?.clearValidate()
  formFileList.value = []
  formUploadedFiles.value = []
  if (formUploadRef.value) {
    formUploadRef.value.clearFiles()
  }
}

const handleDialogClose = () => {
  resetForm()
}

// 表单上传配置
const formUploadAction = computed(() => {
  return '/api/upload/yieldEvidence'
})

const formUploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return {
    Authorization: `Bearer ${token}`
  }
})

const formUploadData = computed(() => {
  // 如果有yieldId就传，没有就不传（新增时）
  const data = {}
  if (yieldForm.yieldId) {
    data.yieldId = yieldForm.yieldId
  }
  return data
})

// 表单上传成功
const handleFormUploadSuccess = (response, file) => {
  if (response.code === 200) {
    const fileInfo = response.data
    // 保存到待上传列表
    formUploadedFiles.value.push({
      filePath: fileInfo.filePath,
      originalFilename: fileInfo.originalFilename,
      fileType: fileInfo.fileType,
      fileSize: fileInfo.fileSize,
      tempId: Date.now() // 临时ID，用于删除时识别
    })
    // 如果已经有yieldId（编辑模式），立即保存凭证
    if (yieldForm.yieldId) {
      saveEvidence({
        yieldId: yieldForm.yieldId,
        filePath: fileInfo.filePath,
        fileType: fileInfo.fileType,
        fileSize: fileInfo.fileSize,
        uploaderId: userStore.userInfo?.userId
      }).then(() => {
        // 标记为已保存，添加evidenceId标记
        const uploadedFile = formUploadedFiles.value.find(f => f.filePath === fileInfo.filePath)
        if (uploadedFile) {
          uploadedFile.evidenceId = 'saved' // 标记为已保存，避免重复保存
        }
        ElMessage.success('凭证上传并保存成功')
      }).catch(() => {
        ElMessage.warning('凭证文件已上传，但保存凭证信息失败，请保存产量统计后重试')
      })
    } else {
      ElMessage.success('文件上传成功，保存产量统计后将自动关联')
    }
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 表单上传失败
const handleFormUploadError = (error) => {
  ElMessage.error('文件上传失败')
}

// 表单上传移除
const handleFormUploadRemove = async (file) => {
  // 获取evidenceId，优先从file对象中获取，其次从response.data中获取
  const evidenceId = file.evidenceId || file.response?.data?.evidenceId
  const filePath = file.response?.data?.filePath
  
  // 如果是已保存的凭证（有真实的evidenceId），需要调用后端API删除
  if (evidenceId && evidenceId !== 'saved') {
    try {
      await ElMessageBox.confirm('确定要删除该凭证吗？', '提示', {
        type: 'warning'
      })
      const res = await deleteEvidence(evidenceId)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        // 从列表中移除
        if (filePath) {
    const index = formUploadedFiles.value.findIndex(
            f => f.filePath === filePath
    )
    if (index > -1) {
      formUploadedFiles.value.splice(index, 1)
          }
          // 从文件列表中移除
          const fileIndex = formFileList.value.findIndex(
            f => f.response?.data?.filePath === filePath || f.evidenceId === evidenceId
          )
          if (fileIndex > -1) {
            formFileList.value.splice(fileIndex, 1)
          }
        }
      }
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('删除失败')
      }
      // 如果删除失败，阻止移除操作
      return false
    }
  } else {
    // 如果是未保存的凭证（新上传的），直接从列表中移除
    if (filePath) {
      const index = formUploadedFiles.value.findIndex(
        f => f.filePath === filePath
      )
      if (index > -1) {
        formUploadedFiles.value.splice(index, 1)
      }
    }
  }
}

// 表单图片预览
const handleFormPicturePreview = (file) => {
  previewImageUrl.value = file.url || getImageUrl(file.response?.data?.filePath)
  previewDialogVisible.value = true
}

// 加载表单中的凭证
const loadFormEvidence = async (yieldId) => {
  if (!yieldId) return
  try {
    const res = await getEvidenceList(yieldId)
    if (res.code === 200) {
      const evidenceList = res.data || []
      // 转换为上传组件的文件列表格式
      formFileList.value = evidenceList.map(item => ({
        name: item.filePath.split('/').pop(),
        url: getImageUrl(item.filePath),
        evidenceId: item.evidenceId, // 保存evidenceId，方便删除时使用
        response: {
          code: 200,
          data: {
            filePath: item.filePath,
            fileType: item.fileType,
            fileSize: item.fileSize,
            evidenceId: item.evidenceId // 在response中也保存evidenceId
          }
        }
      }))
      formUploadedFiles.value = evidenceList.map(item => ({
        filePath: item.filePath,
        originalFilename: item.filePath.split('/').pop(),
        fileType: item.fileType,
        fileSize: item.fileSize,
        evidenceId: item.evidenceId // 已有凭证的ID
      }))
    }
  } catch (error) {
    console.error('加载凭证失败', error)
  }
}

// 保存表单中的凭证
const saveFormEvidence = async (yieldId) => {
  if (!yieldId || formUploadedFiles.value.length === 0) return
  
  // 只保存新上传的凭证（没有evidenceId的，或者evidenceId为'saved'标记的临时保存）
  const newFiles = formUploadedFiles.value.filter(f => {
    // 过滤掉已有真实evidenceId的凭证（编辑时加载的已有凭证）
    return !f.evidenceId || f.evidenceId === 'saved'
  })
  
  if (newFiles.length === 0) return
  
  try {
    let successCount = 0
    for (const file of newFiles) {
      // 如果已经标记为saved，说明编辑模式下已经保存过了，跳过
      if (file.evidenceId === 'saved') {
        continue
      }
      const evidenceData = {
        yieldId: yieldId,
        filePath: file.filePath,
        fileType: file.fileType,
        fileSize: file.fileSize,
        uploaderId: userStore.userInfo?.userId
      }
      await saveEvidence(evidenceData)
      successCount++
    }
    if (successCount > 0) {
      ElMessage.success(`成功保存 ${successCount} 个凭证`)
    }
  } catch (error) {
    ElMessage.error('保存凭证失败')
  }
}

const handleSizeChange = () => {
  loadData()
}

const handleCurrentChange = () => {
  loadData()
}

// 上传凭证
const handleUploadEvidence = async (row) => {
  currentYieldId.value = row.yieldId
  uploadDialogVisible.value = true
  fileList.value = []
  uploadProgress.value = 0
  await loadUploadedEvidence()
}

// 上传配置
const uploadAction = computed(() => {
  return '/api/upload/yieldEvidence'
})

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return {
    Authorization: `Bearer ${token}`
  }
})

const uploadData = computed(() => {
  return {
    yieldId: currentYieldId.value
  }
})

// 上传前验证
const beforeUpload = (file) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/jpg'
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传JPG/PNG格式的图片!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB!')
    return false
  }
  return true
}

// 上传进度
const handleUploadProgress = (event, file) => {
  uploadProgress.value = Math.round(event.percent)
}

// 上传成功
const handleUploadSuccess = async (response, file) => {
  uploadProgress.value = 0
  if (response.code === 200) {
    const fileInfo = response.data
    // 保存凭证信息到数据库
    try {
      const evidenceData = {
        yieldId: currentYieldId.value,
        filePath: fileInfo.filePath,
        fileType: fileInfo.fileType,
        fileSize: fileInfo.fileSize,
        uploaderId: userStore.userInfo?.userId
      }
      const res = await saveEvidence(evidenceData)
      if (res.code === 200) {
        ElMessage.success('凭证上传成功')
        // 添加到已上传图片列表
        uploadedImages.value.push({
          filePath: fileInfo.filePath,
          originalFilename: fileInfo.originalFilename,
          fileType: fileInfo.fileType,
          fileSize: fileInfo.fileSize
        })
        fileList.value = []
      }
    } catch (error) {
      ElMessage.error('保存凭证信息失败')
    }
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 上传失败
const handleUploadError = (error) => {
  uploadProgress.value = 0
  ElMessage.error('文件上传失败')
}

// 关闭上传对话框
const handleUploadDialogClose = () => {
  currentYieldId.value = null
  fileList.value = []
  uploadProgress.value = 0
  uploadedImages.value = []
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

// 获取图片URL
const getImageUrl = (filePath) => {
  if (!filePath) return ''
  
  // 如果路径以 / 开头，直接使用
  if (filePath.startsWith('/')) {
    // 使用相对路径，通过vite代理访问
    // 如果静态资源访问失败，可以尝试使用Controller接口
    return filePath
  }
  
  // 否则添加 /uploads 前缀
  return `/uploads/${filePath}`
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes || bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

// 图片卡片预览
const handlePictureCardPreview = (file) => {
  previewImageUrl.value = file.url || getImageUrl(file.response?.data?.filePath)
  previewDialogVisible.value = true
}

// 加载计划列表
const loadPlanList = async () => {
  try {
    const res = await getAllPlans()
    if (res.code === 200) {
      planList.value = res.data || []
    }
  } catch (error) {
    console.error('加载计划列表失败', error)
  }
}

// 加载区域列表
const loadAreaList = async () => {
  try {
    const res = await getAllAreas()
    if (res.code === 200) {
      areaList.value = res.data || []
    }
  } catch (error) {
    console.error('加载区域列表失败', error)
  }
}

// 加载品种列表
const loadBreedList = async () => {
  try {
    const res = await getAllBreeds()
    if (res.code === 200) {
      breedList.value = res.data || []
    }
  } catch (error) {
    console.error('加载品种列表失败', error)
  }
}

// 加载用户列表
const loadUserList = async () => {
  try {
    const res = await getUserListForSelect()
    if (res.code === 200) {
      userList.value = res.data || []
    }
  } catch (error) {
    console.error('加载用户列表失败', error)
  }
}

// 根据ID获取计划名称
const getPlanName = (planId) => {
  if (!planId) return '-'
  const plan = planList.value.find(p => p.planId === planId)
  return plan ? plan.planName : `计划${planId}`
}

// 根据ID获取区域名称
const getAreaName = (areaId) => {
  if (!areaId) return '-'
  const area = areaList.value.find(a => a.areaId === areaId)
  return area ? area.areaName : `区域${areaId}`
}

// 根据ID获取品种名称
const getBreedName = (breedId) => {
  if (!breedId) return '-'
  const breed = breedList.value.find(b => b.breedId === breedId)
  return breed ? breed.breedName : `品种${breedId}`
}

// 根据ID获取负责人姓名
const getManagerName = (managerId) => {
  if (!managerId) return '-'
  const user = userList.value.find(u => u.userId === managerId)
  return user ? (user.realName || user.username) : `用户${managerId}`
}

onMounted(() => {
  loadData()
  loadPlanList()
  loadAreaList()
  loadBreedList()
  loadUserList()
})
</script>

<style scoped>
.yield-list {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 凭证查看样式 */
.evidence-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  padding: 10px;
}

.evidence-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  transition: all 0.3s;
}

.evidence-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.evidence-image-wrapper {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f5f7fa;
}

.evidence-image {
  width: 100%;
  height: 100%;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 30px;
}

.evidence-info {
  padding: 12px;
}

.evidence-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

.evidence-time {
  color: #606266;
}

.evidence-size {
  color: #909399;
}

/* 已上传图片样式 */
.uploaded-images-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 15px;
  padding: 10px;
}

.uploaded-image-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  transition: all 0.3s;
}

.uploaded-image-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.uploaded-image {
  width: 100%;
  height: 150px;
}

.uploaded-image-info {
  padding: 8px;
  font-size: 12px;
  color: #606266;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>

