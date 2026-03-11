<template>
    <div class="admin-container">
      <!-- 标题 -->
      <div class="header-title">
        <h1 class="title-text">
          <span class="icon">🔒</span>
          区块链交易所后台管理系统
          <span class="icon">👨‍💼</span>
        </h1>
        <p class="subtitle">安全 · 高效 · 透明</p>
      </div>
  
      <el-card class="admin-card">
        <div class="header">
          <el-button class="logout-btn" type="danger" @click="logout">登出👋</el-button>
          <el-popover trigger="hover" placement="bottom" :width="300">
            <template #reference>
              <span class="account-emoji" @click="navigateToAccountManagement">👤</span>
            </template>
            <el-descriptions title="管理员信息" :column="1" border>
              <el-descriptions-item label="用户名">{{ admin.username }}</el-descriptions-item>
              <el-descriptions-item label="邮箱">{{ admin.email }}</el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag type="success">正常</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="用户权限">
                <el-tag type="danger">超级管理员</el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </el-popover>
        </div>
  
        <!-- 标签页 -->
        <el-tabs v-model="activeTab" class="admin-tabs">
          <!-- 用户管理 -->
          <el-tab-pane label="👥 用户管理" name="userManagement">
            <div class="search-bar">
              <el-input
                v-model="userSearchQuery"
                placeholder="搜索用户名或邮箱"
                clearable
                class="search-input"
                @clear="handleSearchClear"
                @keyup.enter="handleUserSearch"
              >
                <template #append>
                  <el-button @click="handleUserSearch" :icon="Search" />
                </template>
              </el-input>
            </div>
  
            <el-table :data="filteredUsers" style="width: 100%" class="user-table">
              <el-table-column type="userId" prop="userId" label="用户ID" width="70" />
              <el-table-column prop="username" label="用户名" width="70" />
              <el-table-column prop="email" label="邮箱" width="180"/>
              <el-table-column prop="create_time" label="注册时间" width="180" />
              <el-table-column label="状态" width="120">
                <template #default="{ row }">
                  <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                    {{ row.status === 1 ? '正常' : '冻结' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="220">
                <template #default="{ row }">
                  <el-button
                    v-if="row.status === 1"
                    size="small"
                    type="warning"
                    @click="handleMarkAbnormal(row.userId)"
                  >
                    标记异常
                  </el-button>
                  <el-button
                    v-if="row.status === 1"
                    size="small"
                    type="danger"
                    @click="handleFreezeUser(row.userId)"
                  >
                    冻结
                  </el-button>
                  <el-button
                    v-if="row.status !== 1"
                    size="small"
                    type="success"
                    @click="handleUnfreezeUser(row.userId)"
                  >
                    解冻
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
  
            <el-pagination
              v-model:current-page="userCurrentPage"
              v-model:page-size="userPageSize"
              :total="totalUsers"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleUserSizeChange"
              @current-change="handleUserPageChange"
              class="pagination"
            />
          </el-tab-pane>
  
          <!-- 异常日志 -->
          <el-tab-pane label="⚠️ 异常日志" name="alertManagement">
            <div class="action-bar">
              <el-button type="danger" @click="handleDeleteSelectedAlerts" :disabled="selectedAlerts.length === 0">
                批量删除
              </el-button>
              <el-button type="primary" @click="exportAlertsToExcel">
                导出Excel
              </el-button>
              <el-date-picker
                v-model="alertDateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                @change="handleAlertDateChange"
                class="date-picker"
              />
            </div>
  
            <el-table
              :data="filteredAlerts"
              style="width: 100%"
              class="alert-table"
              @selection-change="handleAlertSelectionChange"
            >
              <el-table-column type="selection" width="30" />
              <el-table-column prop="alertId" label="ID" width="40" />
              <el-table-column prop="userId" label="用户ID" width="70" />
              <el-table-column prop="alertType" label="类型" width="110">
              
                <template #default="{ row }">
                  <el-tag :type="getAlertTagType(row.alertType)">
                    {{ row.alertType }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="description" label="描述" width="180"/>
              <el-table-column prop="create_time" label="时间" width="180" />
              <el-table-column prop="tradeId" label="交易ID" width="50" />
              <el-table-column label="操作" width="100">
                <template #default="{ row }">
                  <el-button size="small" type="danger" @click="handleDeleteAlert(row.alertId)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
  
            <el-pagination
              v-model:current-page="alertCurrentPage"
              v-model:page-size="alertPageSize"
              :total="totalAlerts"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleAlertSizeChange"
              @current-change="handleAlertPageChange"
              class="pagination"
            />
          </el-tab-pane>
  
          <!-- 风险订单 -->
          <el-tab-pane label="🛒 风险订单" name="riskOrderManagement">
            <el-table :data="riskOrders" style="width: 100%" class="order-table">
  <el-table-column prop="tradeId" label="订单ID" width="70" />
  <el-table-column prop="userId" label="用户ID" width="70" />
  <el-table-column label="类型" width="100">
    <template #default="{ row }">
      <el-tag :type="row.tradeType === 'buy' ? 'success' : 'danger'">
        {{ row.tradeType === 'buy' ? '买入' : '卖出' }}
      </el-tag>
    </template>
  </el-table-column>
  <el-table-column prop="amount" label="数量" width="90" />
  <el-table-column prop="money" label="金额" width="90" />
  <el-table-column prop="create_time" label="时间" width="170" />
  <el-table-column label="状态" width="70">
    <template #default="{ row }">
      <el-tag :type="(row.status)">
        {{ (row.status) }}
      </el-tag>
    </template>
  </el-table-column>
  <el-table-column label="操作" width="220">
    <template #default="{ row }">
      <el-button-group v-if="row.status === 3"> <!-- 假设3是待审核状态 -->
        <el-button size="small" type="success" @click="handleApproveOrder(row.tradeId)">
          通过
        </el-button>
        <el-button size="small" type="danger" @click="handleRejectOrder(row.tradeId)">
          拒绝
        </el-button>
      </el-button-group>
      <span v-else>已处理</span>
    </template>
  </el-table-column>
</el-table>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, onMounted } from 'vue'
  import { Search } from '@element-plus/icons-vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import * as XLSX from 'xlsx'
  import * as echarts from 'echarts';
    import api from '@/utils/api';
    import account from '@/utils/account';
    import qs from 'qs';
    import dayjs from 'dayjs'
    import trade from '@/utils/trade';
    import { useRouter } from 'vue-router'
  // 管理员信息
  const admin = ref({
    username: 'admin',
    email: '5430532505@qq.com',
    role: 'admin'
  })
  
  // 标签页状态
  const activeTab = ref('userManagement')
  
  // 用户管理相关
  const users = ref([])
  const filteredUsers = ref([])
  const userSearchQuery = ref('')
  const userCurrentPage = ref(1)
  const userPageSize = ref(10)
  const totalUsers = ref(0)
  
  // 异常日志相关
  const alerts = ref([])
  const filteredAlerts = ref([])
  const selectedAlerts = ref([])
  const alertDateRange = ref([])
  const alertCurrentPage = ref(1)
  const alertPageSize = ref(10)
  const totalAlerts = ref(0)
  
  // 风险订单相关
  const riskOrders = ref([])
  const orderCurrentPage = ref(1)
  const orderPageSize = ref(10)
  const totalOrders = ref(0)


   //=============
  
// 获取所有异常日志
const fetchAlerts = async () => {
  try {
    const response = await api.post('/getAlerts')
    console.log('获取所有异常日志:', response)
    if ( response.code === '200') {
      alerts.value = response.data.map(alert => ({
        ...alert,
        // 格式化日期
        create_time: formatDateTime(alert.createTime),
        alertId: alert.alertId,
        userId: alert.userId,
        alertType: alert.alertType,
        description: alert.description,
        tradeId: alert.tradeId,
        createTime: alert.createTime,
        
      }))
      filteredAlerts.value = alerts.value
      totalAlerts.value = alerts.value.length
    } else {
      ElMessage.error(response.data?.msg || '获取异常日志失败11')
    }
  } catch (error) {
    ElMessage.error('网络错误，获取异常日志失败22')
    console.error('获取异常日志失败:', error)
  }
}

// 删除单个日志
const handleDeleteAlert = async (alertId) => {
  try {
    const response = await api.post('/deleteAlertById', qs.stringify({ alertId }), {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
    
    if ( response.code === '200') {
      ElMessage.success('删除日志成功')
      // 刷新日志列表
      await fetchAlerts()
    } else {
      ElMessage.error(response.data?.msg || '删除日志失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，删除日志失败')
    console.error('删除日志失败:', error)
  }
}

// // 批量删除选中日志
// const handleDeleteSelectedAlerts = async () => {
//   if (selectedAlerts.value.length === 0) {
//     ElMessage.warning('请先选择要删除的日志')
//     return
//   }
  
//   try {
//     await ElMessageBox.confirm('确定要删除选中的日志吗?', '警告', {
//       confirmButtonText: '确定',
//       cancelButtonText: '取消',
//       type: 'warning'
//     })
    
//     // 这里假设后端支持批量删除，如果不行需要循环调用单个删除接口
// //     const deletePromises = selectedAlerts.value.map(alert => 
// //       api.post('/api/deleteAlertById', qs.stringify({ alertId: alert.alert_id }), {
// //         headers: {
// //           'Content-Type': 'application/x-www-form-urlencoded'
// //         }
// //       })
    
// //     await Promise.all(deletePromises)
// //     ElMessage.success('批量删除日志成功')
// //     // 刷新日志列表
// //     await fetchAlerts()
// //     selectedAlerts.value = []
// //   } catch (error) {
// //     if (error !== 'cancel') {
// //       ElMessage.error('批量删除日志失败')
// //       console.error('批量删除日志失败:', error)
// //     }
// //   }
// // }

// 导出日志到Excel
const exportAlertsToExcel = () => {
  if (filteredAlerts.value.length === 0) {
    ElMessage.warning('没有数据可导出')
    return
  }
  
  try {
    // 准备导出数据
    const exportData = filteredAlerts.value.map(alert => ({
      '日志ID': alert.alertId,
      '用户ID': alert.userId,
      '日志类型': alert.alertType,
      '描述': alert.description,
      '创建时间': alert.create_time,
      '交易ID': alert.tradeId || '无'
    }))
    
    // 创建工作表
    const worksheet = XLSX.utils.json_to_sheet(exportData)
    // 创建工作簿
    const workbook = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(workbook, worksheet, '异常日志')
    // 生成Excel文件并下载
    XLSX.writeFile(workbook, `异常日志_${dayjs().format('YYYYMMDDHHmmss')}.xlsx`)
    
    ElMessage.success('导出Excel成功')
  } catch (error) {
    ElMessage.error('导出Excel失败')
    console.error('导出Excel失败:', error)
  }
}



// 日期范围筛选
const handleAlertDateChange = () => {
  if (!alertDateRange.value || alertDateRange.value.length !== 2) {
    filteredAlerts.value = alerts.value
    return
  }
  
  const [start, end] = alertDateRange.value
  filteredAlerts.value = alerts.value.filter(alert => {
    const alertDate = dayjs(alert.create_time)
    return alertDate.isAfter(start) && alertDate.isBefore(end)
  })
}

// 日志选择变化
const handleAlertSelectionChange = (selection) => {
  selectedAlerts.value = selection
}

// 分页处理
const handleAlertSizeChange = (val) => {
  alertPageSize.value = val
}

const handleAlertPageChange = (val) => {
  alertCurrentPage.value = val
}

  //=============
  

///////////======
// 获取用户数据
const fetchUsers = async () => {
  try {
    const response = await api.post('/getUserList')
    if ( response.code === '200') {
      users.value = response.data.map(user => ({
        ...user,
        // 添加日期格式化处理
        create_time: formatDateTime(user.createTime),
        userId: user.userId,
        username: user.username,
        email: user.email,
        role: user.role,
        status: user.status,
        createTime: user.createTime,
   
      }))
      filteredUsers.value = users.value
    } else {
      ElMessage.error(response.data?.msg || '获取用户列表失败11')
    }
  } catch (error) {
    ElMessage.error('网络错误，获取用户列表失败')
    console.error('获取用户列表失败22:', error)
  }
}

// 根据名称搜索用户
const searchUsersByName = async (name) => {
  console.log('searchUsersByName:', name)
  try {
    const response = await api.post('/getUsersByName', qs.stringify({ param: name }), {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
    console.log(response)
    if ( response.code === '200') {
      filteredUsers.value = response.data
      totalUsers.value = response.data.length
      userCurrentPage.value = 1 // 搜索后重置页码
    } else {
      ElMessage.error(response.data?.msg || '搜索用户失败11')
    }
  } catch (error) {
    ElMessage.error('网络错误，搜索用户失败')
    console.error('搜索用户失败22:', error)
  }
}

// 更新用户状态
const updateUserStatus = async (userId, status) => {
  try {
    console.log(11,userId, status)
    const response = await api.post('/updateStatus', qs.stringify({ 
      userId: userId,
      status: status
    }), {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
    
    if ( response.code === '200') {
      ElMessage.success(`用户状态已${status === 1 ? '解冻' : '冻结'}`)
      // 更新本地数据
      const userIndex = users.value.findIndex(user => user.userId === userId)
      if (userIndex !== -1) {
        users.value[userIndex].status = status
        filteredUsers.value = [...users.value]
      }
    } else {
      ElMessage.error(response.data?.msg || '更新用户状态失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，更新用户状态失败')
    console.error('更新用户状态失败:', error)
  }
}

// 搜索用户
const handleUserSearch = () => {
  if (!userSearchQuery.value) {
    fetchUsers()
    return
  }
  searchUsersByName(userSearchQuery.value)
}

// 清除搜索
const handleSearchClear = () => {
  userSearchQuery.value = ''
  fetchUsers()
}

// 冻结用户
const handleFreezeUser = (userId) => {
  console.log('userid:', userId)
  ElMessageBox.confirm('确定要冻结该用户吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    updateUserStatus(userId, 3)
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

// 解冻用户
const handleUnfreezeUser = (userId) => {
  ElMessageBox.confirm('确定要解冻该用户吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    updateUserStatus(userId, 1)
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

// 标记异常

  const handleMarkAbnormal = (userId) => {
    console.log('标记异常：userid:', userId)
    ElMessageBox.confirm('确定要标记该用户为异常吗?', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      updateUserStatus(userId, 2)
    }).catch(() => {
      ElMessage.info('已取消操作')
    })
  }



const getAlertTagType = (alertType) => {
  const typeMap = {
    '登录异常': 'danger',
    '交易异常': 'warning',
    '资金异常': 'error',
    '行为异常': 'info',
    '系统警告': 'primary'
  }
  return typeMap[alertType] || 'default'
}
//////////===========


// 风险订单相关函数

// 获取风险订单（status为3的订单）
const fetchRiskOrders = async () => {
  try {
    const response = await trade.post('/getOrdersByStatus', qs.stringify({
      userId: '-1', // 如果需要特定用户订单可以传值
      status: 3   // 风险订单状态
    }), {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });

    console.log('风险订单响应:', response);
    
    if (response?.code === '200') {
      riskOrders.value = response.data.map(order => ({
        tradeId: order.tradeId,
        userId: order.userId,
        tradeType: order.tradeType,
        amount: order.amount,
        money: order.money,
        status: '拒绝',
        create_time: formatDateTime(order.createTime),
        // 确保包含所有需要显示的字段
        ...order
      }));
      totalOrders.value = riskOrders.value.length;
    } else {
      ElMessage.error(response?.msg || '获取风险订单失败');
    }
  } catch (error) {
    ElMessage.error('网络错误，获取风险订单失败');
    console.error('获取风险订单失败:', error);
  }
};

// 审批通过订单
const handleApproveOrder = async (tradeId) => {
  try {
    await ElMessageBox.confirm('确定要通过该订单吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    });
    
    const response = await trade.post('/UpdateOrder', qs.stringify({ 
      tradeId: tradeId
    }), {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });
    
    if (response?.code === '200') {
      ElMessage.success('订单已通过审批');
      // 刷新订单列表
      await fetchRiskOrders();
    } else {
      ElMessage.error(response?.msg || '审批订单失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('审批订单失败');
      console.error('审批订单失败:', error);
    }
  }
};

// 拒绝订单
const handleRejectOrder = async (tradeId) => {
  try {
    await ElMessageBox.confirm('确定要拒绝该订单吗?', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    const response = await trade.post('/UpdateOrder2', qs.stringify({ 
      tradeId: tradeId,
  
    }), {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });
    console.log('审批结果:',response)
    if (response.code === '200') {
      ElMessage.success('订单已拒绝');
      // 刷新订单列表
      await fetchRiskOrders();
    } else {
      ElMessage.error(response?.msg || '拒绝订单失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('拒绝订单失败');
      console.error('拒绝订单失败:', error);
    }
  }
};

// 分页处理
const handleOrderSizeChange = (val) => {
  orderPageSize.value = val;
};

const handleOrderPageChange = (val) => {
  orderCurrentPage.value = val;
};
// 获取订单状态文本
const getOrderStatusText = (status) => {
  const textMap = {
    4: '已拒绝',
    3: '待审核'
  };
  return textMap[status] || '未知状态';
};
//=========================================

const formatDateTime = (dateString) => {
  return dateString ? dayjs(dateString).format('YYYY-MM-DD HH:mm:ss') : ''
}
const router = useRouter();
const logout=() =>{
      // 弹出确认框，确认是否退出
      const confirmLogout = window.confirm('确定要退出登录吗？');
      if (!confirmLogout) {
        return; // 如果用户选择取消，直接返回
      }
          alert('退出成功');
          router.push('/login')
      
    
    }

    onMounted(async () => {
  try {
    await fetchUsers();      // 等待用户数据加载完成
    await fetchAlerts();     // 等待告警数据加载完成
    await fetchRiskOrders(); // 等待风险订单数据加载完成
    console.log("所有数据加载完成！");
  } catch (error) {
    console.error("初始化数据加载失败:", error);
    ElMessage.error("数据加载失败，请刷新重试");
  }
});


  
  </script>
  
  <style scoped>
  .admin-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    min-height: 100vh;
    background: linear-gradient(135deg, #53daf8 0%, #4e88ed 100%);
    padding: 20px;
  }
  
  .header-title {
    text-align: center;
    margin-bottom: 20px;
    width: 100%;
    max-width: 800px;
  }
  
  .title-text {
    font-size: 36px;
    font-weight: bold;
    color: #ffffff;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .title-text .icon {
    font-size: 48px;
    margin: 0 10px;
  }
  
  .subtitle {
    font-size: 18px;
    color: #ffffff;
    margin-top: 10px;
    opacity: 0.8;
  }
  
  .admin-card {
    width: 100%;
    max-width: 800px;
    padding: 40px;
    border-radius: 20px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
    background-color: #ffffff;
    position: relative;
    overflow: hidden;
  }
  
  .header {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    margin-bottom: 30px;
  }
  
  .logout-btn {
    padding: 12px 24px;
    font-size: 16px;
    font-weight: 500;
    border-radius: 12px;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    color: white;
    width: auto;
    margin-left: 10px;
  }
  
  .logout-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
  }
  
  .account-emoji {
    font-size: 36px;
    cursor: pointer;
    margin-left: 10px;
    transition: transform 0.3s ease;
  }
  
  .account-emoji:hover {
    transform: rotate(15deg);
  }
  
  .admin-tabs {
    margin-top: 20px;
  }
  
  .search-bar {
    margin-bottom: 20px;
  }
  
  .search-input {
    width: 300px;
  }
  
  .action-bar {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
    align-items: center;
  }
  
  .date-picker {
    margin-left: auto;
  }
  
  .user-table,
  .alert-table,
  .order-table {
    margin-bottom: 20px;
  }
  
  .pagination {
    margin-top: 20px;
    justify-content: center;
  }
  
  .el-tag {
    font-size: 14px;
  }
  
  @media (max-width: 768px) {
    .admin-card {
      padding: 20px;
    }
    
    .title-text {
      font-size: 24px;
    }
    
    .action-bar {
      flex-direction: column;
      align-items: flex-start;
    }
    
    .date-picker {
      margin-left: 0;
      margin-top: 10px;
      width: 100%;
    }
    
    .search-input {
      width: 100%;
    }
  }
  </style>