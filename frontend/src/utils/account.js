import axios from 'axios';

// 设置后端接口的基本 URL（假设后端运行在 http://localhost:8080）
const account = axios.create({
  baseURL: 'http://localhost:8080/account', // 后端 account 基础 URL
  timeout: 10000, // 请求超时限制
});

// 设置请求拦截器（例如可以添加 token 等）
account.interceptors.request.use(
  (config) => {
    // 如果需要，可以在这里设置请求头，添加 token 等
    // config.headers['Authorization'] = `Bearer ${token}`;
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 设置响应拦截器
account.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default account;
