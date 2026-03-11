<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="system-title">区块链交易所资金安全管理系统</h2>
      <h3 class="form-title">登录</h3>
      <el-form @submit.prevent="handleLogin">
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
          <el-button type="primary" native-type="submit" class="submit-btn">登录</el-button>
        </el-form-item>
      </el-form>
      <p class="form-footer">
        没有账号？ <router-link to="/register" class="link">注册</router-link>
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
      password: ''
    };
  },
  methods: {
    async handleLogin() {
      try {
        const response = await api.post('/login', qs.stringify({
          username: this.username,
          password: this.password
        }), {
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        });
        console.log('登录结果：',response);

        if (response.message === '登录成功') {
          alert('登录成功');
          if (response.role === '1') {
            this.$router.push('/user-info');
          } else if (response.role === '2') {
            this.$router.push('/admin-dashboard');
          } else {
            alert('登录失败：返回数据无效');
          }
        } else if (response.message === '封禁') {
          alert('账号已被封禁，请联系管理员解冻！');
        }
        else if (response.message === '密码错误') {
          alert('登录失败，请检查用户名和密码！');
        }
      } catch (error) {
        console.error("登录失败:", error);
        alert('登录失败，请检查用户名和密码！');
      }
    }
  }
};
</script>



<style scoped>


.login-container {
  display: flex;
  justify-content: center;  /*  水平居中 */
  align-items: center;      /*  垂直居中 */
  height: 100vh;
  background: linear-gradient(135deg, #4facfe, #00f2fe);
}
.login-card {
  width: 400px;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
  background-color: #ffffff;
  text-align: center; 
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