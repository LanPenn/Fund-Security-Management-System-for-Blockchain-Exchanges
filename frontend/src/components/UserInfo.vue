<template>
  <div class="user-info-container">
    <!-- 标题 -->
    <div class="header-title">
      <h1 class="title-text">
        <span class="icon">🔒</span>
        区块链交易所资金安全管理系统
        <span class="icon">💰</span>
      </h1>
      <p class="subtitle">安全 · 高效 · 透明</p>
    </div>

    <el-card class="user-info-card">
      <div class="header">
        <el-button class="recharge-btn2" type="primary" @click="handleRecharge">资产💎</el-button>
        <el-button class="market-btn" type="primary" @click="navigateToMarket">  法币市场🌏</el-button>
        <el-button class="market-btn" type="primary" @click="navigateToTradingMarket">交易市场💱</el-button>
        <el-button class="market-btn" type="primary" @click="logout">登出👋</el-button>
        <el-popover trigger="hover" placement="bottom" :width="300">
          <template #reference>
            <span class="account-emoji" @click="navigateToAccountManagement">👤</span>
          </template>
          <el-descriptions title="账户信息" :column="1" border>
            <el-descriptions-item label="用户名">{{ user.username }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ user.email }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="user.status === 1 ? 'success' : 'warning'">{{ user.status === 1 ? '正常' : '异常' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="用户权限">
              <el-tag type="primary">{{ user.role === 'admin' ? '管理员' : '普通用户' }}</el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-popover>
      </div>

      <!-- K线图轮播 -->
      <h2 class="section-title">📈 动态K线图</h2>
      <div class="kline-carousel">
        <div class="kline-chart" ref="klineChart" style="width: 100%; height: 400px;"></div>
      </div>

      <!-- 实时市场数据 -->
      <h2 class="section-title">📊 动态市场数据</h2>
      <el-table :data="marketData" style="width: 100%" class="market-table">
        <el-table-column prop="symbol" label="币种" width="120" />
        <el-table-column prop="price" label="价格" width="120" />
        <el-table-column prop="change" label="涨跌幅" width="120">
          <template #default="{ row }">
            <el-tag :type="row.change >= 0 ? 'success' : 'danger'">
              {{ row.change >= 0 ? '📈' : '📉' }}{{ row.change }}%
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="volume" label="24h交易量" />
      </el-table>
    </el-card>

    <!-- 资产详情弹窗 -->
    <el-drawer v-model="showBalanceDrawer" title="资产详情" direction="rtl" class="balance-drawer">
      <el-descriptions title="账户余额" :column="1" border class="balance-descriptions">
        <el-descriptions-item>
          <template #label>
            <img src="@/img/USD.png" alt="Dollar Icon" class="balance-icon" @click="toggleActionsUSD"/>
          </template>
          {{ balance.bau }} $
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <img src="@/img/ETH.png" alt="ETH Icon" class="balance-icon" @click="toggleActions"/>
          </template>
          {{ balance.bae }} ETH
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <img src="@/img/BTC.png" alt="BTC Icon" class="balance-icon"/>
          </template>
          {{ balance.bab }}
        </el-descriptions-item>
      </el-descriptions>

      <!-- 输入金额和按钮 -->
      <div v-if="showActions" class="balance-actions">
        <el-input
          v-model="transactionAmount"
          type="number"
          placeholder="输入金额"
          class="amount-input"
        />
        <el-button type="success" @click="handleRechargeAction" class="recharge-btn">ETH充值</el-button>
        <el-button type="danger" @click="handleWithdrawAction" class="withdraw-btn">ETH提现</el-button>
      </div>
      <div v-if="showActionsUSD" class="balance-actions">
        <el-input
          v-model="transactionAmount"
          type="number"
          placeholder="输入金额"
          class="amount-input"
        />
        <el-button type="success" @click="handleRechargeActionUSD" class="recharge-btn">USD充值</el-button>
        <el-button type="danger" @click="handleWithdrawActionUSD" class="withdraw-btn">USD提现</el-button>
      </div>

    </el-drawer>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import api from '@/utils/api';
import account from '@/utils/account';
import qs from 'qs';
import { web3, contract, connectWallet } from '@/utils/web3';

export default {
  data() {
    return {
      user: {
        userId: '',
        username: '',
        email: '',
        status: '',
        role: '',
      },
      balance: {
        bau: 0,
        bae: 0, // ETH余额
        bab: 0,
      },
      showBalanceDrawer: false,
      transactionAmount: 0,
      showActions: false,
      showActionsUSD: false,
      walletConnected: false,

      // 实时市场数据
      marketData: [
        { symbol: 'BTC/USDT', price: '$110030.3', change: 2.5, volume: '$1,000,000' },
        { symbol: 'ETH/USDT', price: '$2540.79', change: -1.8, volume: '$500,000' },
        { symbol: 'BNB/USDT', price: '$674.20', change: 5.84, volume: '$200,000' },
      ],

      // K线图数据 - 更新后的数据
      klineData: {     
        BTC:[   
   { time: '2025-05-07', close: 97035.1, open: 96817.9, high: 97647.5, low: 95798.4 },
  { time: '2025-05-08', close: 103274.3, open: 97035.1, high: 103885.4, low: 96901.9 },
  { time: '2025-05-09', close: 102975.1, open: 103270.7, high: 104308.7, low: 102365.7 },
  { time: '2025-05-10', close: 104798.0, open: 102974.7, high: 104969.8, low: 102837.2 },
  { time: '2025-05-11', close: 104119.2, open: 104748.3, high: 104930.9, low: 103358.8 },
  { time: '2025-05-12', close: 102795.0, open: 104119.2, high: 105765.2, low: 100766.8 },
  { time: '2025-05-13', close: 104119.2, open: 102794.9, high: 104972.9, low: 101510.2 },
  { time: '2025-05-14', close: 103513.9, open: 104116.3, high: 104316.0, low: 102632.9 },
  { time: '2025-05-15', close: 103765.4, open: 103513.2, high: 104153.8, low: 101436.8 },
  { time: '2025-05-16', close: 103480.5, open: 103764.6, high: 104544.1, low: 103126.5 },
  { time: '2025-05-17', close: 103112.6, open: 103473.9, high: 103712.3, low: 102626.5 },
  { time: '2025-05-18', close: 106504.5, open: 103113.9, high: 106577.0, low: 103113.9 },
  { time: '2025-05-19', close: 105585.8, open: 106507.7, high: 107081.3, low: 102088.2 },
  { time: '2025-05-20', close: 106815.8, open: 105586.1, high: 107270.5, low: 104199.3 },
  { time: '2025-05-21', close: 109640.1, open: 106814.7, high: 110628.8, low: 106109.5 },
  { time: '2025-05-22', close: 111702.7, open: 109632.6, high: 111953.6, low: 109281.8 },
  { time: '2025-05-23', close: 107313.4, open: 111702.6, high: 111778.7, low: 106855.4 },
  { time: '2025-05-24', close: 107763.0, open: 107313.5, high: 109476.4, low: 106904.5 },
  { time: '2025-05-25', close: 109021.1, open: 107763.0, high: 109203.4, low: 106648.3 },
  { time: '2025-05-26', close: 110030.3, open: 109014.9, high: 110356.5, low: 108694.0 }
   
        ],
          ETH: [     
          
         

  { time: '2025-05-07', close: 1811.20, open: 1816.71, high: 1849.05, low: 1788.76 },
  { time: '2025-05-08', close: 2206.13, open: 1811.20, high: 2225.02, low: 1809.09 },
  { time: '2025-05-09', close: 2345.27, open: 2207.18, high: 2485.98, low: 2185.92 },
  { time: '2025-05-10', close: 2583.78, open: 2345.27, high: 2597.68, low: 2322.81 },
  { time: '2025-05-11', close: 2515.22, open: 2583.94, high: 2594.80, low: 2440.65 },
  { time: '2025-05-12', close: 2495.73, open: 2515.22, high: 2621.09, low: 2413.02 },
  { time: '2025-05-13', close: 2680.05, open: 2494.95, high: 2734.04, low: 2418.44 },
  { time: '2025-05-14', close: 2609.68, open: 2680.04, high: 2719.25, low: 2549.01 },
  { time: '2025-05-15', close: 2548.16, open: 2610.78, high: 2644.57, low: 2480.73 },
  { time: '2025-05-16', close: 2537.04, open: 2545.46, high: 2646.74, low: 2531.45 },
  { time: '2025-05-17', close: 2475.04, open: 2537.14, high: 2537.14, low: 2449.57 },
  { time: '2025-05-18', close: 2498.97, open: 2475.04, high: 2585.12, low: 2340.94 },
  { time: '2025-05-19', close: 2528.21, open: 2498.79, high: 2543.42, low: 2352.64 },
  { time: '2025-05-20', close: 2523.82, open: 2528.23, high: 2587.21, low: 2446.16 },
  { time: '2025-05-21', close: 2551.42, open: 2523.42, high: 2614.54, low: 2454.72 },
  { time: '2025-05-22', close: 2663.77, open: 2545.88, high: 2692.31, low: 2545.70 },
  { time: '2025-05-23', close: 2526.46, open: 2664.62, high: 2731.76, low: 2503.87 },
  { time: '2025-05-24', close: 2529.32, open: 2524.39, high: 2575.62, low: 2516.82 },
  { time: '2025-05-25', close: 2551.20, open: 2530.68, high: 2553.95, low: 2467.77 },
  { time: '2025-05-26', close: 2540.79, open: 2551.13, high: 2596.93, low: 2540.19 }

          
          
        ],
        BNB: [
{ time: '2025-05-07', close: 602.60, open: 602.50, high: 609.00, low: 597.80 },
  { time: '2025-05-08', close: 629.30, open: 602.41, high: 629.30, low: 600.70 },
  { time: '2025-05-09', close: 666.51, open: 629.30, high: 669.71, low: 623.60 },
  { time: '2025-05-10', close: 663.90, open: 666.61, high: 678.00, low: 648.90 },
  { time: '2025-05-11', close: 650.91, open: 662.60, high: 668.49, low: 648.11 },
  { time: '2025-05-12', close: 660.00, open: 650.70, high: 692.31, low: 647.61 },
  { time: '2025-05-13', close: 664.20, open: 660.00, high: 671.01, low: 640.81 },
  { time: '2025-05-14', close: 652.30, open: 664.59, high: 666.10, low: 647.20 },
  { time: '2025-05-15', close: 651.60, open: 652.20, high: 660.89, low: 642.30 },
  { time: '2025-05-16', close: 646.50, open: 651.61, high: 659.70, low: 643.71 },
  { time: '2025-05-17', close: 639.30, open: 646.20, high: 646.70, low: 634.71 },
  { time: '2025-05-18', close: 651.60, open: 639.50, high: 655.00, low: 634.40 },
  { time: '2025-05-19', close: 649.40, open: 650.91, high: 653.20, low: 633.70 },
  { time: '2025-05-20', close: 650.10, open: 649.60, high: 653.70, low: 640.40 },
  { time: '2025-05-21', close: 673.20, open: 650.10, high: 677.89, low: 648.70 },
  { time: '2025-05-22', close: 686.30, open: 673.49, high: 688.91, low: 673.20 },
  { time: '2025-05-23', close: 656.60, open: 686.31, high: 691.30, low: 653.80 },
  { time: '2025-05-24', close: 667.90, open: 656.60, high: 676.80, low: 655.30 },
  { time: '2025-05-25', close: 669.10, open: 667.90, high: 671.40, low: 657.40 },
  { time: '2025-05-26', close: 674.20, open: 669.10, high: 677.50, low: 668.00 }

      ],

      },
      currentSymbol: 'BTC', // 当前展示的币种
      chart: null, // ECharts实例
      carouselInterval: null, // 轮播定时器
    };
  },
  async created() {
    await this.fetchUserInfo();
  },
  mounted() {
    this.initKlineChart();
    this.startCarousel();
  },
  beforeDestroy() {
    this.stopCarousel(); // 组件销毁时清除定时器
  },
  methods: {
    // 跳转到账号管理页面
    navigateToAccountManagement() {
      this.$router.push('/UserManagement');
    },
    // 生成K线图数据
    generateKlineData(symbol, count) {
      const data = [];
      let basePrice = symbol === 'BTC' ? 40000 : symbol === 'ETH' ? 3000 : 400; // 基础价格
      for (let i = 0; i < count; i++) {
        const open = basePrice + Math.random() * 1000 - 500;
        const close = open + Math.random() * 1000 - 500;
        const low = Math.min(open, close) - Math.random() * 500;
        const high = Math.max(open, close) + Math.random() * 500;
        data.push([`2023-10-${i + 1}`, open, close, low, high]);
      }
      return data;
    },

    // 初始化K线图
    initKlineChart() {
      const chartDom = this.$refs.klineChart;
      this.chart = echarts.init(chartDom);
      this.updateChart();
    },

    // 更新K线图
    updateChart() {
      const data = this.klineData[this.currentSymbol];

      // 动态加载图标路径
      const iconPath = `${import.meta.env.BASE_URL}img/${this.currentSymbol}.png`; // 用 BASE_URL 替代 require

      const option = {
        title: {
          text: `{icon|} ${this.currentSymbol}/USDT K线图`,
          left: 'center',
          textStyle: {
            rich: {
              icon: {
                backgroundColor: {
                  image: iconPath, // 动态图标路径
                },
                width: 20,
                height: 20,
                align: 'center',
              },
            },
          },
        },
        tooltip: {
          trigger: 'axis',
        },
        xAxis: {
          type: 'category',
          data: data.map(item => item.time),
        },
        yAxis: {
          scale: true,
        },
        series: [
          {
            type: 'candlestick',
            data: data.map(item => [item.open, item.close, item.low, item.high]),
            itemStyle: {
              color: '#00da3c', // 上涨颜色
              color0: '#ec0000', // 下跌颜色
            },
          },
          {
            type: 'line',
            name: '收盘价',
            data: data.map(item => item.close),
            smooth: true,
            lineStyle: {
              color: '#5470C6',
              width: 2,
            },
          },
        ],
      };

      this.chart.setOption(option);
    },

    // 启动轮播
    startCarousel() {
      this.carouselInterval = setInterval(() => {
        this.nextChart();
      }, 3000); // 每隔3秒切换一次
    },

    // 停止轮播
    stopCarousel() {
      if (this.carouselInterval) {
        clearInterval(this.carouselInterval);
      }
    },

    // 切换到下一个币种
    nextChart() {
      const symbols = ['BTC', 'ETH', 'BNB'];
      const currentIndex = symbols.indexOf(this.currentSymbol);
      this.currentSymbol = currentIndex === symbols.length - 1 ? symbols[0] : symbols[currentIndex + 1];
      this.updateChart();
    },
    
    async logout() {
      // 弹出确认框，确认是否退出
      const confirmLogout = window.confirm('确定要退出登录吗？');
      if (!confirmLogout) {
        return; // 如果用户选择取消，直接返回
      }

      try {
        const response = await api.post('/logout');
        if (window.ethereum) {
          // 如果是 MetaMask 或其他兼容钱包
          try {
            await window.ethereum.request({ method: 'eth_accounts' });  // 检查是否有连接的账户
            // 清除钱包的账户连接状态
            window.ethereum.request({ method: 'eth_disconnect' });
            console.log('钱包断开连接成功');
          } catch (err) {
            console.error('断开钱包失败:', err);
          }
        }

        if (response === '退出成功') {
          alert('退出成功');
          this.$router.push('/login');
        } else {
          alert('退出失败');
        }
      } catch (error) {
        console.error('error,退出失败:', error);
      }
    },

    async fetchUserInfo() {
      try {
        const response = await api.get('/user/info');
        this.user = response;
        
        console.log('用户信息:', this.user);
      } catch (error) {
        console.error('获取用户信息失败:', error);
        alert('获取用户信息失败，请重新登录！');
      }
    },
    async handleRecharge() {
      if (this.user.userId) {
        if (this.walletConnected) {
          this.showBalanceDrawer = true;
      } else {
        await connectWallet();
        this.walletConnected = true;
      }
      const response = await account.post('/balance', qs.stringify({
          userId: this.user.userId,
        }), {
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        });
        this.balance.bau = response.bau;
        const accounts = await web3.eth.getAccounts();
        const userAddress = accounts[0];  // 确保地址正确
        // 传递 userAddress 作为参数
        const contractBalance = await contract.methods.checkBalance(userAddress).call();
        this.balance.bae = web3.utils.fromWei(contractBalance, "ether");
        this.showBalanceDrawer = true;
        // userinfo页面
      }
    },

    async fetchBalance() {
      try {
        const response = await account.post('/balance', qs.stringify({
          userId: this.user.userId,
        }), {
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        });
        this.balance.bau = response.bau;
        const accounts = await web3.eth.getAccounts();
        const userAddress = accounts[0];  // 确保地址正确
        
        // 传递 userAddress 作为参数
        const contractBalance = await contract.methods.checkBalance(userAddress).call();
        this.balance.bae = web3.utils.fromWei(contractBalance, "ether");
        this.showBalanceDrawer = true;
      } catch (error) {
        console.error('获取合约余额失败:', error);
        alert('获取余额失败，请稍后重试！');
      }
    },

    // 充值操作
    async handleRechargeAction() {
      if (this.transactionAmount <= 0) {
        alert('请输入有效的金额');
        return;
      }
      this.$message.info('充值处理中...');
      try {
        const accounts = await web3.eth.getAccounts();
        await contract.methods.deposit().send({
          from: accounts[0],
          value: web3.utils.toWei(this.transactionAmount.toString(), "ether"),
        });
        this.transactionAmount = 0;
        this.$message.success('充值成功');
        await this.fetchBalance();
      } catch (error) {
        console.error('充值失败:', error);
        this.$message.error('充值失败，请稍后重试');
      }
    },
    // USD充值操作
    async handleRechargeActionUSD() {
      if (this.transactionAmount <= 0) {
        alert('请输入有效的金额');
        return;
      }
      this.$message.info('充值处理中...');

      try {
        const response = await account.post('/recharge', qs.stringify({
          userId: this.user.userId,
          value: parseInt(this.transactionAmount, 10),
        }), {
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        });

        // 根据后端返回的状态码和消息显示不同的提示
        if (response.status === 200) {
          console.log(response.status);
          this.transactionAmount = 0;
          this.$message.success('充值成功');
          await this.fetchBalance();
        } else if (response.status === 429) { 
          // 交易频率过高
          this.$message.warning(response.data || '交易频率过高，请稍后再试');
        } else {
          // 其他错误
          this.$message.error(response.data || '充值失败，请稍后重试');
        }
      } catch (error) {
        console.error('充值失败:', error);
      }
    },

    // 提现操作
    async handleWithdrawAction() {
      if (this.transactionAmount <= 0) {
        alert('请输入有效的金额');
        return;
      }
      this.$message.info('提现处理中...');
      try {
        const accounts = await web3.eth.getAccounts();
        await contract.methods.withdraw(web3.utils.toWei(this.transactionAmount.toString(), "ether")).send({
          from: accounts[0],
        });
        this.transactionAmount = 0;
        this.$message.success('提现成功');
        await this.fetchBalance();
      } catch (error) {
        console.error('提现失败:', error);
        this.$message.error('提现失败，请稍后重试');
      }
    },

    // USD提现操作
    async handleWithdrawActionUSD() {
      if (this.transactionAmount <= 0) {
        alert('请输入有效的金额');
        return;
      }
      this.$message.info('提现处理中...');
      try {
        const response = await account.post('/Withdraw', qs.stringify({
          userId: this.user.userId,
          value: this.transactionAmount
        }), {
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        });

        // 根据后端返回的状态码和消息显示不同的提示
        if (response.status === 200) {
          this.transactionAmount = 0;
          this.$message.success('提现成功');
          await this.fetchBalance();
        } else if (response.status === 429) {
          // 交易频率过高
          this.$message.warning(response.data || '交易频率过高，请稍后再试');
        } else if (response.status === 400){
          // 其他错误
          this.$message.error(response.data || '提现金额超出账户余额');
        }
      } catch (error) {
        console.error('提现失败:', error);
      }
    },

    navigateToMarket() {
      this.$router.push({ 
         name: 'SpotTrading', 
         query: { bau: this.balance.bau, bae: this.balance.bae, bab: this.balance.bab }
      });
    },
    navigateToTradingMarket() {
      this.$router.push({ 
         name: 'TradingMarket', 
         query: { bau: this.balance.bau, bae: this.balance.bae, bab: this.balance.bab,userId: this.user.userId }
      });
    },
 
    toggleActions() {
      if (this.walletConnected) {
        this.fetchBalance();
        this.showActions = true;
        this.showActionsUSD = false;
      } else {
        connectWallet();
        this.walletConnected = true;
        this.fetchBalance();
        this.showActions = true;
        this.showActionsUSD = false;
      }
    },

    toggleActionsUSD() {
      this.showActionsUSD = true;
      this.showActions = false;
    }
  },
};

</script>

<style scoped>
.order-icon {
  width: 20px;
  height: 20px;
  object-fit: contain;
}
.kline-carousel {
  margin-top: 20px;
}

.kline-chart {
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.kline-carousel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 20px;
}

.carousel-btn {
  width: 120px;
}

.user-info-container {
  display: flex;
  flex-direction: column;
  align-items: center; /* 确保内容居中 */
  height: 150vh;
  background: linear-gradient(135deg, #53daf8 0%, #4e88ed 100%);
  padding: 20px;
}

.header-title {
  text-align: center; /* 标题居中 */
  margin-bottom: 20px;
  width: 100%; /* 确保标题宽度与卡片一致 */
  max-width: 700px; /* 与卡片宽度一致 */
}

.user-info-card {
  width: 100%;
  max-width: 700px; /* 设置最大宽度 */
  padding: 60px 40px;
  border-radius: 24px;
  box-shadow: 0 16px 32px rgba(0, 0, 0, 0.1);
  background-color: #ffffff;
  position: relative;
  overflow: hidden;
  margin: 0 auto; /* 确保卡片在父容器中居中 */
}

.header {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 30px;
}

.recharge-btn,
.recharge-btn2 {
  padding: 12px 24px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 12px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  color: white;
  width: auto;
  margin-left: 10px;
}

.recharge-btn {
  background: linear-gradient(135deg, #0b79e0 0%, #1f73e8 100%);
}

.recharge-btn2 {
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
}

.recharge-btn:hover,
.recharge-btn2:hover {
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

.title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  text-align: center; /* 确保标题居中 */
  margin-bottom: 12px;
}
@media (max-width: 768px) {
  .user-info-card {
    padding: 30px 20px;
  }

  .title {
    font-size: 22px;
    text-align: center; /* 强制居中 */
  }
}
.balance-drawer {
  background: linear-gradient(135deg, #f7c6ff 0%, #ffbcfc 100%);
  border-radius: 16px;
  padding: 20px;
}

.balance-descriptions {
  margin-top: 20px;
}

.balance-icon {
  width: 24px;
  height: 24px;
  transition: transform 0.3s ease;
}

.balance-icon:hover {
  transform: scale(1.2);
}

.el-descriptions__label {
  font-weight: 600;
  color: #5f6368;
  font-size: 16px;
}

.el-descriptions__content {
  color: #303133;
  font-size: 18px;
}

.el-tag {
  font-size: 14px;
}

.balance-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.amount-input {
  width: 60%;
  font-size: 16px;
  padding: 10px;
}

.withdraw-btn {
  background-color: #f56c6c;
  color: white;
  font-size: 16px;
  width: 40%;
}

.market-table {
  margin-bottom: 20px;
}

.security-status {
  margin-top: 20px;
}

.asset-trend {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

.trend-descriptions {
  flex: 1;
  margin-right: 20px;
}

.trend-value {
  font-size: 24px;
  font-weight: bold;
  color: #67C23A;
}

.chart-container {
  text-align: center;
}

.chart-label {
  margin-top: 10px;
  font-size: 14px;
  color: #606266;
}

.el-progress--dashboard {
  width: 120px;
  height: 120px;
}


.title-text {
  font-size: 36px;
  font-weight: bold;
  color: #ffffff;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center; /* 标题内容居中 */
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

.user-info-card {
  width: 100%;
  max-width: 700px;
  padding: 60px;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  background-color: #ffffff;
  position: relative;
  overflow: hidden;
} 

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 20px 0 10px;
}
</style>