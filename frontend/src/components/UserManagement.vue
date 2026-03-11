<template>
    <div class="user-management-container">
      <!-- 标题和返回按钮 -->
      <div class="header-title">
        <el-button class="back-button" type="text" @click="goBack">
          <el-icon><ArrowLeft /></el-icon> 返回
        </el-button>
        <h1 class="title-text">
          <span class="icon">🔒</span>
          账号管理
        </h1>
        <p class="subtitle">管理您的账户信息</p>
      </div>
  
      <el-card class="account-management-card">
        <!-- 用户信息展示 -->
        <el-descriptions title="账户信息" :column="1" border>
          <el-descriptions-item label="用户名">
            <el-input v-model="user.username" :disabled="!isEditingUsername" />
            <el-button type="text" @click="handleUpdateUsername">
              {{ isEditingUsername ? '保存' : '修改' }}
            </el-button>
          </el-descriptions-item>
          <el-descriptions-item label="邮箱">
            <el-input v-model="user.email" :disabled="!isEditingEmail" />
            <el-button type="text" @click="handleUpdateEmail">
              {{ isEditingEmail ? '保存' : '修改' }}
            </el-button>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="user.status === 1 ? 'success' : 'warning'">
              {{ user.status === 1 ? '正常' : '异常' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="用户权限">
            <el-tag type="primary">
              {{ user.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
  
        <!-- 修改密码 -->
        <el-divider />
        <div class="password-section">
          <h3>修改密码</h3>
          <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef">
            <el-form-item label="旧密码" prop="oldPassword">
              <el-input type="password" v-model="passwordForm.oldPassword" />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input type="password" v-model="passwordForm.newPassword" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updatePassword">保存</el-button>
            </el-form-item>
          </el-form>
        </div>
  
        <!-- 注销账号 -->
        <el-divider />
        <div class="cancel-section">
          <h3>注销账号</h3>
          <el-button type="danger" @click="showCancelDialog">注销账号</el-button>
        </div>
      </el-card>
  
      <!-- 注销账号对话框 -->
      <el-dialog title="注销账号" v-model="cancelDialogVisible" width="30%" center>
        <el-form :model="cancelForm" :rules="cancelRules" ref="cancelFormRef">
          <el-form-item label="请输入密码" prop="password">
            <el-input type="password" v-model="cancelForm.password" />
          </el-form-item>
        </el-form>
        <span class="dialog-footer">
          <el-button @click="cancelDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmCancelAccount">确认注销</el-button>
        </span>
      </el-dialog>
    </div>
  </template>
  
  <script>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { ArrowLeft } from '@element-plus/icons-vue'; // 引入返回图标
  import api from '@/utils/api';
  import { BigNumber } from 'bignumber.js'
  export default {
    components: {
      ArrowLeft, // 注册图标组件
    },
    setup() {
      const router = useRouter();
      const user = ref({
        userId: '',
        username: '',
        email: '',
        status: '',
        role: '',
      });
      const isEditingUsername = ref(false);
      const isEditingEmail = ref(false);
      const passwordForm = ref({
        oldPassword: '',
        newPassword: '',
      });
      const passwordRules = ref({
        oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
        newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
      });
      const cancelDialogVisible = ref(false);
      const cancelForm = ref({
        password: '',
      });
      const cancelRules = ref({
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
      });
  
      const fetchUserInfo = async () => {
        try {
          const response = await api.get('/user/info');
          user.value = response;
        } catch (error) {
          console.error('获取用户信息失败:', error);
          alert('获取用户信息失败，请重新登录！');
        }
      };
  
      const handleUpdateUsername = async () => {
        if (isEditingUsername.value) {
          try {
            await api.get('/user/updateUsername', {
              params: {
                id: user.value.userId,
                value: user.value.username,
              },
            });
            alert('用户名修改成功！');
          } catch (error) {
            console.error('用户名修改失败:', error);
            alert('用户名修改失败，请重试！');
          }
        }
        isEditingUsername.value = !isEditingUsername.value;
      };
  
      const handleUpdateEmail = async () => {
        if (isEditingEmail.value) {
          try {
            await api.get('/user/updateEmail', {
              params: {
                id: user.value.userId,
                value: user.value.email,
              },
            });
            alert('邮箱修改成功！');
          } catch (error) {
            console.error('邮箱修改失败:', error);
            alert('邮箱修改失败，请重试！');
          }
        }
        isEditingEmail.value = !isEditingEmail.value;
      };
  
      const updatePassword = async () => {
        try {
          await api.get('/user/updatePassword', {
            params: {
              oldValue: passwordForm.value.oldPassword,
              newValue: passwordForm.value.newPassword,
            },
          });
          alert('密码修改成功！');
        } catch (error) {
          console.error('密码修改失败:', error);
          alert('密码修改失败，请检查旧密码是否正确！');
        }
      };
  
      const showCancelDialog = () => {
        cancelDialogVisible.value = true;
        console.log('注销对话框已打开', cancelDialogVisible.value);
      };
  
      const confirmCancelAccount = async () => {
        try {
          console.log('输入的验证密码', cancelForm.value.password);
          await api.get('/user/verifyPassword', {
            params: { passWord: cancelForm.value.password },
          });
  
          await api.get('/user/cancel');
          alert('账号已注销！');
          window.location.href = '/login';
        } catch (error) {
          console.error('注销账号失败:', error);
          alert('密码错误，注销账号失败！');
        } finally {
          cancelDialogVisible.value = false;
        }
      };
  
      const goBack = () => {
        router.push('/user-info');
      };
  
      fetchUserInfo();
  
      return {
        user,
        isEditingUsername,
        isEditingEmail,
        passwordForm,
        passwordRules,
        cancelDialogVisible,
        cancelForm,
        cancelRules,
        handleUpdateUsername,
        handleUpdateEmail,
        updatePassword,
        showCancelDialog,
        confirmCancelAccount,
        goBack,
      };
    },
  };
  </script>
  
  <style scoped>
  .user-management-container {
    padding: 20px;
    background: linear-gradient(135deg, #53daf8 0%, #4e88ed 100%);
    min-height: 100vh;
  }
  
  .header-title {
    text-align: center;
    margin-bottom: 20px;
    position: relative;
  }
  
  .back-button {
    position: absolute;
    left: 20px;
    top: 50%;
    transform: translateY(-50%);
    padding: 8px 12px;
    border-radius: 6px;
    background-color: #ffffff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: background-color 0.3s ease;
  }
  
  .back-button:hover {
    background-color: #f5f7fa;
  }
  
  .title-text {
    font-size: 28px;
    font-weight: bold;
    color: #303133;
  }
  
  .subtitle {
    font-size: 16px;
    color: #606266;
  }
  
  .account-management-card {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
    border-radius: 12px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  }
  
  .password-section,
  .cancel-section {
    margin-top: 20px;
  }
  
  .cancel-section {
    text-align: center;
  }
  </style>