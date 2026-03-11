import './assets/main.css';
import axios from 'axios';
import { createApp } from 'vue';
import { createPinia } from 'pinia';
import ElementPlus from 'element-plus';
import { ElMessage } from 'element-plus';
import 'element-plus/dist/index.css';
import App from './App.vue';
import router from './router';
import BigNumber from 'bignumber.js';

const app = createApp(App);

// 注册 Element Plus
app.use(ElementPlus);
app.use(createPinia());
app.use(router);

// Vue 3 的全局属性注册方式（替代 Vue 2 的 prototype）
app.config.globalProperties.$message = ElMessage;  // 修正这里
app.config.globalProperties.$bn = BigNumber;      // 修正这里

// axios 配置
axios.defaults.withCredentials = true;

app.mount('#app');