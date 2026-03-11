import { createRouter, createWebHistory } from 'vue-router';
import Login from '@/components/Login.vue';
import Register from '@/components/Register.vue';
import AdminDashboard from '@/components/AdminDashboard.vue';
import FundsManagement from '@/components/FundsManagement.vue';
import UserInfo from '@/components/UserInfo.vue';
import SpotTrading from '@/components/SpotTrading.vue';
import UserManagement from '@/components/UserManagement.vue';
import TradingMarket from '@/components/TradingMarket.vue';
import Home from '../views/HomeView.vue';
// 定义路由
const routes = [
  {
    path: '/login',
    component: Login, // 登录页
  },
  {
    path: '/register',
    component: Register, // 注册页
  },

  {
    path: '/funds-management',
    name:'FundsManagement',
    component: FundsManagement, // 普通用户界面
  },
  {
    path: '/admin-dashboard',
    component: AdminDashboard, // 管理员界面
  },
  {  
    path: '/user-info',
    name:'UserInfo',
    component: UserInfo,
    },
  {  
    path: '/SpotTrading',
    name:'SpotTrading',
    component:SpotTrading,
    },
 
    {  
      path: '/UserManagement',
      name:'UserManagement',
      component:UserManagement,
      },
      {  
        path: '/TradingMarket',
        name:'TradingMarket',
        component:TradingMarket,
        },


];

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;

