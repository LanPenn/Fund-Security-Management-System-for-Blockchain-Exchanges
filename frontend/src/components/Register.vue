<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2 class="system-title">区块链交易所资金安全管理系统</h2>
      <h3 class="form-title">注册</h3>
      <el-form @submit.prevent="handleRegister">
        <el-form-item>
          <el-input
            v-model="username"
            placeholder="用户名"
            prefix-icon="User"
            class="custom-input"
            required
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            show-password
            class="custom-input"
            required
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="email"
            type="email"
            placeholder="QQ邮箱"
            prefix-icon="Message"
            class="custom-input"
            required
          />
        </el-form-item>
        <el-alert
          v-if="emailError"
          type="error"
          title="仅支持 QQ 邮箱，请输入 @qq.com 或 @vip.qq.com 结尾的邮箱！"
          show-icon
          class="alert"
        />
        <el-form-item>
          <el-button type="primary" native-type="submit" class="submit-btn">注册</el-button>
        </el-form-item>
      </el-form>
      <p class="form-footer">
        已有账号？ <router-link to="/login" class="link">登录</router-link>
      </p>
    </el-card>
  </div>
</template>

<script>
import api from '@/utils/api';
import qs from 'qs';
export default {
  data() {
    return {
      username: '',
      password: '',
      email: '',
      emailError: false
    };
  },
  methods: {
    async handleRegister() {
      const qqEmailRegex = /^[A-Za-z0-9._%+-]+@qq\.com$|^[A-Za-z0-9._%+-]+@vip\.qq\.com$/;

      if (!qqEmailRegex.test(this.email)) {
        this.emailError = true;
        return;
      }
      this.emailError = false;

      try {
        const response = await api.post('/register', {
  username: this.username,
  password: this.password,
  email: this.email,
});


        alert('注册成功！');
        this.$router.push('/login');
      } catch (error) {
        console.error("注册失败:", error);
        alert('用户已存在，请重试！');
      }
    }
  }
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #4facfe, #00f2fe);
}

.register-card {
  width: 400px;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
  background-color: #ffffff;
}

.system-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  text-align: center;
  margin-bottom: 20px;
}

.form-title {
  font-size: 18px;
  color: #606266;
  text-align: center;
  margin-bottom: 20px;
}

.custom-input >>> .el-input__inner {
  border-radius: 8px;
  padding: 12px;
}

.submit-btn {
  width: 100%;
  background-color: #409eff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  padding: 12px;
  margin-top: 10px;
}

.alert {
  margin-bottom: 20px;
}

.form-footer {
  margin-top: 15px;
  color: #606266;
  text-align: center;
}

.link {
  color: #409eff;
  text-decoration: none;
  font-weight: bold;
}
</style>