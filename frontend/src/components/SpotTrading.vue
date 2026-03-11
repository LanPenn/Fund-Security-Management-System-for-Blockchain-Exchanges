<template>
  <div class="market-page">
    <!-- 标头 -->
    <div class="header">
      <div class="header-content">
        <!-- 返回按钮 -->
        <el-button class="back-button" type="text" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>返回
        </el-button>
        <h1 class="header-title">
          <span class="icon">💱</span>
          数字货币交易平台
        </h1>
        <p class="header-subtitle">安全、便捷的数字资产交易</p>
      </div>
    </div>

    <!-- 主容器 -->
    <div class="market-container">
      <!-- 交易模式切换 -->
      <div class="mode-switch">
        <el-button 
          @click="toggleBuy" 
          :type="isBuySelected ? 'success' : 'info'"
          class="mode-btn"
        >
          <i class="el-icon-bottom-right"></i> 买入
        </el-button>
        <el-button 
          @click="toggleSell" 
          :type="isSellSelected ? 'danger' : 'info'"
          class="mode-btn"
        >
          <i class="el-icon-top-left"></i> 卖出
        </el-button>
      </div>

      <!-- 交易表单和余额卡片 -->
      <div class="content-wrapper">
        <!-- 交易表单 -->
        <transition name="el-zoom-in-top">
          <div v-if="isBuySelected" class="transaction-card buy-card">
            <h3 class="form-title"><i class="el-icon-shopping-bag-1"></i> 买入ETH</h3>
            <el-input 
              v-model="transactionAmount" 
              placeholder="输入买入金额 (USD)"
              type="number"
              step="0.0001"
              @input="validateInput('buy')"
            >
              <template slot="append">USD</template>
            </el-input>
            
            <div class="calculation-results">
              <p class="calculation-item">
                <span class="label">您将支付金额:</span>
                <span class="value">{{ formatCurrency(calculatePaymentAmount(transactionAmount)) }} USD</span>
              </p>
              <p class="calculation-item">
                <span class="label">您将获得ETH:</span>
                <span class="value">{{ formatCrypto(transactionAmount) }} ETH</span>
              </p>
            </div>

            <el-button 
              @click="handleBuyTransaction" 
              type="success" 
              class="submit-btn"
              :disabled="!isBuyValid"
            >
              确认买入
            </el-button>
          </div>
        </transition>

        <transition name="el-zoom-in-top">
          <div v-if="isSellSelected" class="transaction-card sell-card">
            <h3 class="form-title"><i class="el-icon-sold-out"></i> 卖出ETH</h3>
            <el-input 
              v-model="transactionAmount2" 
              placeholder="输入卖出金额 (ETH)"
              type="number"
              step="0.0001"
              @input="validateInput('sell')"
            >
              <template slot="append">ETH</template>
            </el-input>

            <div class="calculation-results">
              <p class="calculation-item">
                <span class="label">您将获得金额：</span>
                <span class="value">{{ formatCurrency(calculatePaymentAmountForSell(transactionAmount2)) }} USD</span>
              </p>
              <p class="calculation-item">
                <span class="label">您将卖出ETH：</span>
                <span class="value">{{ formatCrypto(transactionAmount2) }} ETH</span>
              </p>
            </div>

            <el-button 
              @click="handleSellTransaction" 
              type="danger" 
              class="submit-btn"
              :disabled="!isSellValid"
            >
              立即卖出
            </el-button>
          </div>
        </transition>

        <!-- 用户余额 -->
        <div class="balance-card">
          <h3 class="balance-title"><i class="el-icon-wallet"></i> 账户资产</h3>
          <div class="balance-item">
            <div class="currency-info">
              <img src="@/img/USD.png" class="currency-icon"/>
              <span class="currency-name"></span>
            </div>
            <span class="balance-value">{{ formatCurrency(balance.usd) }} USD</span>
          </div>
          <div class="balance-item">
            <div class="currency-info">
              <img src="@/img/ETH.png" class="currency-icon"/>
              <span class="currency-name"></span>
            </div>
            <span class="balance-value">{{ formatCrypto(balance.eth) }} ETH</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { BigNumber } from 'bignumber.js';
import qs from 'qs';
import api from '@/utils/api';
import account from '@/utils/account';
import { web3, connectWallet, contract } from "@/utils/web3";
import { ArrowLeft } from '@element-plus/icons-vue'; // 引入返回图标  
export default {
  components: {
    ArrowLeft, // 注册图标组件
  },
  data() {
    return {
      user: {
        userId: '',
        username: '',
        email: '',
        status: '',
        role: '',
      },
      isBuySelected: false,
      isSellSelected: false,
      transactionAmount: null,
      transactionAmount2: null,
      balance: {
        usd: 0,
        eth: 0,
      },
      ethToUsdRate: 0,
      isBuyValid: false,
      isSellValid: false,
    };
  },

  computed: {
    usdToEthRate() {
      return new BigNumber(1).dividedBy(this.ethToUsdRate);
    },
  },

  async mounted() {
    await this.fetchUserInfo();
    await this.fetchBalance();
    await this.getETHPrice();
  },

  methods: {


    async  getETHPrice() {
  const response = await fetch(
    'https://api.coingecko.com/api/v3/simple/price?ids=ethereum&vs_currencies=usd'
  );
  const data = await response.json();
  this.ethToUsdRate=data.ethereum.usd
  console.log(`ETH 当前价格为 $${data.ethereum.usd}`);
}
,
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

    async fetchBalance() {
      try {
        const response = await account.post('/balance', qs.stringify({
          userId: this.user.userId,
        }), {
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        });

        this.balance.usd = response.bau;

        const accounts = await web3.eth.getAccounts();
        const userAddress = accounts[0];
        const contractBalance = await contract.methods.checkBalance(userAddress).call();
        this.balance.eth = web3.utils.fromWei(contractBalance, "ether");
        console.log('获取余额成功:', response);
      } catch (error) {
        console.error('获取合约余额失败:', error);
        alert('获取余额失败，请稍后重试！');
      }
    },

    formatCurrency(value) {
      return new BigNumber(value).toFormat(4, BigNumber.ROUND_DOWN);
    },

    goBack() {
      this.$router.push({
        name: 'UserInfo'
      });
    },

    formatCrypto(value) {
      return new BigNumber(value).toFormat(8, BigNumber.ROUND_DOWN);
    },

    calculatePaymentAmount(amount) {
      return new BigNumber(amount || 0).times(this.ethToUsdRate);
    },

    calculateReceivedAmount(amount) {
      return new BigNumber(amount || 0).times(this.usdToEthRate);
    },

    calculatePaymentAmountForSell(amount) {
      return new BigNumber(amount || 0).times(this.ethToUsdRate);
    },

    validateInput(type) {
      if (type === 'buy') {
        const amount = new BigNumber(this.transactionAmount || 0);
        const requiredUSD = this.calculatePaymentAmount(amount);
        this.isBuyValid = amount.gt(0) && requiredUSD.lte(this.balance.usd);
      } else {
        const amount = new BigNumber(this.transactionAmount2 || 0);
        this.isSellValid = amount.gt(0) && amount.lte(this.balance.eth);
      }
    },

    async handleBuyTransaction() {
      try {
        const accounts = await web3.eth.getAccounts();
        const userAddress = accounts[0];

        if (!this.transactionAmount || isNaN(this.transactionAmount) || Number(this.transactionAmount) <= 0) {
          this.$message.error("请输入有效的ETH购买数量！");
          return;
        }

        const usdAmount = this.calculatePaymentAmount(this.transactionAmount);

        if (usdAmount.gt(this.balance.usd)) {
          this.$message.error("USD 余额不足！");
          return;
        }

        this.$message.info("交易开始，正在扣除 USD 余额...");
        await account.post('/Withdraw', qs.stringify({
          userId: this.user.userId,
          value: parseInt(usdAmount.toString(), 10),
        }), {
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        });

        this.$message.info("USD 余额扣除成功，正在购买 ETH...");
        await contract.methods.buyETH(web3.utils.toWei(this.transactionAmount, "ether")).send({
          from: userAddress
        });

        this.$message.success("购买成功！");
        this.transactionAmount = 0;
        await this.fetchBalance();
      } catch (error) {
        console.error("购买失败:", error);
        this.$message.error("购买失败，请稍后重试！");
      }
    },

    async handleSellTransaction() {
      try {
        const accounts = await web3.eth.getAccounts();
        const userAddress = accounts[0];

        if (!this.transactionAmount2 || isNaN(this.transactionAmount2) || Number(this.transactionAmount2) <= 0) {
          this.$message.error("请输入有效的ETH出售数量！");
          return;
        }

        const usdAmount = this.calculatePaymentAmountForSell(this.transactionAmount2);

        this.$message.info("交易开始，正在出售 ETH...");
        await contract.methods.sellETH(web3.utils.toWei(this.transactionAmount2, "ether")).send({
          from: userAddress
        });

        this.$message.info("ETH 出售成功，正在充值 USD...");
        await account.post('/recharge', qs.stringify({
          userId: this.user.userId,
          value: parseInt(usdAmount.toString(), 10),
        }), {
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        });

        this.$message.success("出售成功！");
        this.transactionAmount2 = 0;
        await this.fetchBalance();
      } catch (error) {
        console.error("出售失败:", error);
        this.$message.error("出售失败，请稍后重试！");
      }
    },

    toggleBuy() {
      this.isBuySelected = true;
      this.isSellSelected = false;
      this.transactionAmount2 = null;
    },

    toggleSell() {
      this.isBuySelected = false;
      this.isSellSelected = true;
      this.transactionAmount = null;
    }
  }
};
</script>

<style scoped>
.market-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #0491ef, #bcd0d5);
  padding: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 返回按钮样式 */
.back-button {
  position: absolute;
  left: 60px;
  top: 80px;
  padding: 8px 12px;
  border-radius: 6px;
  background-color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: background-color 0.3s ease;
}

.back-button:hover {
  background-color: #f5f7fa;
}

/* 标头样式 */
.header {
  width: 100%;
  padding: 2px;
  border-radius: 8px;
  margin-bottom: 2px;
  text-align: center;
}

.header-title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 1px;
}

.header-subtitle {
  font-size: 16px;
  color: #606266;
}

/* 主容器 */
.market-container {
  width: 90%;
  max-width: 1000px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

/* 交易模式切换 */
.mode-switch {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 20px;
}

.mode-btn {
  width: 150px;
  font-size: 16px;
}

/* 内容布局 */
.content-wrapper {
  display: flex;
  gap: 20px;
}

/* 交易卡片 */
.transaction-card {
  flex: 1;
  padding: 20px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.95);
}

.buy-card {
  border: 1px solid rgba(103, 194, 58, 0.2);
}

.sell-card {
  border: 1px solid rgba(245, 108, 108, 0.2);
}

/* 余额卡片 */
.balance-card {
  width: 200px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
}


.market-container {
  max-width: 600px;
  margin: 2rem auto;
  padding: 2rem;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
}

.mode-switch {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin-bottom: 2rem;
}

.mode-btn {
  height: 48px;
  font-size: 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.transaction-card {
  padding: 2rem;
  border-radius: 12px;
  margin-bottom: 2rem;
}

.buy-card {
  background: rgba(103, 194, 58, 0.08);
  border: 1px solid rgba(103, 194, 58, 0.2);
}

.sell-card {
  background: rgba(245, 108, 108, 0.08);
  border: 1px solid rgba(245, 108, 108, 0.2);
}

.form-title {
  color: #606266;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  gap: 8px;
}

.calculation-results {
  margin: 1.5rem 0;
  padding: 2rem;
  background: rgba(255,255,255,0.9);
  border-radius: 8px;
}

.calculation-item {
  display: flex;
  justify-content: space-between;
  margin: 0.5rem 0;
  font-size: 14px;
}

.balance-card {
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 12px;
}

.balance-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 0;
  border-bottom: 1px solid #eee;
}

.currency-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.currency-icon {
  width: 32px;
  height: 32px;
  object-fit: contain;
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  border-radius: 8px;
  margin-top: 1rem;
}

.balance-value {
  font-family: 'Roboto Mono', monospace;
  font-size: 16px;
  color: #303133;
}


</style>




