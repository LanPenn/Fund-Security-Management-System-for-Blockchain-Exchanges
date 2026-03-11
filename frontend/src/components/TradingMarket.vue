<template>
  <div class="trading-container">
    <!-- 头部标题 -->
    <div class="header-title">
      <el-button class="back-button" type="text" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>返回
      </el-button>
      <h1 class="title-text">
        <span class="icon">📊</span>
        交易市场
        <span class="icon">💹</span>
      </h1>
    </div>

    <el-card class="trading-card">
      <!-- 用户操作区 -->
      <div class="user-actions">
        <el-button type="primary" @click="showCreateOrderDialog" class="action-btn">
          <i class="el-icon-document-add"></i> 创建订单
        </el-button>
        
        <!-- 订单类型切换按钮组 -->
        <el-button-group class="order-type-buttons">
          <el-button 
            :type="activeOrderType === 'market' ? 'primary' : ''" 
            @click="switchOrderType('market')"
          >
            <i class="el-icon-s-order"></i> 市场订单
          </el-button>
          <el-button 
            :type="activeOrderType === 'my' ? 'primary' : ''" 
            @click="switchOrderType('my')"
          >
            <i class="el-icon-user-solid"></i> 我的订单
          </el-button>
        </el-button-group>
        
        <div class="user-balance">
          <span>可用余额: {{ formatBalance(userBalance) }} USDT</span>
        </div>
      </div>

      <!-- 实时行情展示 -->
      <div class="market-data-section">
        <h2 class="section-title">
          <i class="el-icon-data-line"></i> 实时行情 - {{ currentSymbol }}
        </h2>
        
        <div class="price-display">
          <div class="current-price">
            <span class="price-label">最新价:</span>
            <span :class="['price-value', priceDirection]">
              {{ latestPrice }} USDT
              <i :class="priceIcon"></i>
            </span>
            <span :class="['price-change', priceChange >= 0 ? 'positive' : 'negative']">
              {{ priceChange >= 0 ? '+' : '' }}{{ priceChange }}%
            </span>
          </div>
          
          <div class="order-book">
            <div class="bids">
              <h3>买盘</h3>
              <el-table :data="bidOrders" style="width: 100%" height="200">
                <el-table-column prop="price" label="价格(USDT)" width="120" sortable>
                  <template #default="{ row }">
                    <span class="bid-price">{{ row.price }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="数量" width="100">
                  <template #default="{ row }">
                    {{ row.amount }} <img src="@/img/ETH.png" style="width:16px; height:16px; vertical-align:middle">
                  </template>
                </el-table-column>
                <el-table-column prop="total" label="总额(USDT)" />
              </el-table>
            </div>
            
            <div class="asks">
              <h3>卖盘</h3>
              <el-table :data="askOrders" style="width: 100%" height="200">
                <el-table-column prop="price" label="价格(USDT)" width="120" sortable>
                  <template #default="{ row }">
                    <span class="ask-price">{{ row.price }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="数量" width="100">
                  <template #default="{ row }">
                    {{ row.amount }} <img src="@/img/ETH.png" style="width:16px; height:16px; vertical-align:middle">
                  </template>
                </el-table-column>
                <el-table-column prop="total" label="总额(USDT)" />
              </el-table>
            </div>
          </div>
        </div>
      </div>

      <!-- 市场订单列表 -->
      <div class="orders-section" v-if="activeOrderType === 'market'">
        <h2 class="section-title">
          <i class="el-icon-s-order"></i> 市场订单
        </h2>
        
        <el-tabs type="border-card">
          <el-tab-pane label="买单">
            <el-table :data="marketBidOrders" style="width: 100%" height="300">
              <el-table-column prop="price" label="价格" width="120" sortable>
                <template #default="{ row }">
                </template>
              </el-table-column>
              <el-table-column label="数量" width="100">
                <template #default="{ row }">
                  {{ row.amount }} <img src="@/img/ETH.png" style="width:16px; height:16px; vertical-align:middle">
                </template>
              </el-table-column>
              <el-table-column label="总额" width="100">
                <template #default="{ row }">
                  {{ row.total }} <img src="@/img/USD.png" style="width:16px; height:16px; vertical-align:middle">
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="创建时间" width="180" />
              <el-table-column prop="creator" label="创建者" width="120" />
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button size="mini" type="success" @click="takeOrder(row, 'sell')">
                    卖出
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          
          <el-tab-pane label="卖单">
            <el-table :data="marketAskOrders" style="width: 100%" height="300">
              <el-table-column prop="price" label="价格" width="120" sortable>
                <template #default="{ row }">

                </template>
              </el-table-column>
              <el-table-column label="数量" width="100">
                <template #default="{ row }">
                  {{ row.amount }} <img src="@/img/ETH.png" style="width:16px; height:16px; vertical-align:middle">
                </template>
              </el-table-column>
              <el-table-column label="总额" width="100">
                <template #default="{ row }">
                  {{ row.total }} <img src="@/img/USD.png" style="width:16px; height:16px; vertical-align:middle">
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="创建时间" width="180" />
              <el-table-column prop="creator" label="创建者" width="120" />
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button size="mini" type="danger" @click="takeOrder(row, 'buy')">
                    买入
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 我的订单列表 -->
      <div class="orders-section" v-else>
        <h2 class="section-title">
          <i class="el-icon-user-solid"></i> 我的订单
        </h2>
        
        <el-tabs type="border-card">
          <el-tab-pane label="我的买单">
            <el-table :data="myBidOrders" style="width: 100%" height="300">
              <el-table-column prop="price" label="价格" width="120" sortable>
                <template #default="{ row }">
  
                </template>
              </el-table-column>
              <el-table-column label="数量" width="100">
                <template #default="{ row }">
                  {{ row.amount }} <img src="@/img/ETH.png" style="width:16px; height:16px; vertical-align:middle">
                </template>
              </el-table-column>
              <el-table-column label="总额" width="100">
                <template #default="{ row }">
                  {{ row.total }} <img src="@/img/USD.png" style="width:16px; height:16px; vertical-align:middle">
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="创建时间" width="180" />
              <el-table-column prop="status" label="状态" width="120">
                <template #default="{ row }">
                  <el-tag 
      :type='warning'
      :class="'status-tag status-1' "
    >
      {{ row.status }}
    </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button size="mini" type="danger" @click="cancelOrder(row)">
                    撤销
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="我的卖单">
            <el-table :data="myAskOrders" style="width: 100%" height="300">
              <el-table-column prop="price" label="价格" width="120" sortable>
                <template #default="{ row }">
       
                </template>
              </el-table-column>
              <el-table-column label="数量" width="100">
                <template #default="{ row }">
                  {{ row.amount }} <img src="@/img/ETH.png" style="width:16px; height:16px; vertical-align:middle">
                </template>
              </el-table-column>
              <el-table-column label="总额" width="100">
                <template #default="{ row }">
                  {{ row.total }} <img src="@/img/USD.png" style="width:16px; height:16px; vertical-align:middle">
                </template>
              </el-table-column>
        
              <el-table-column prop="createdAt" label="创建时间" width="180" />
              <el-table-column prop="status" label="状态" width="120">
                <template #default="{ row }">
                  <el-tag  :type='warning':class="'status-tag status-1'">
                   {{ row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button size="mini" type="danger" @click="cancelOrder(row)">
                    撤销
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="风险订单">
            <el-table :data="RiskOrders" style="width: 100%" height="300">
              <el-table-column prop="price" label="价格" width="120" sortable>
                <template #default="{ row }">

                </template>
              </el-table-column>
              <el-table-column label="数量" width="100">
                <template #default="{ row }">
                  {{ row.amount }} <img src="@/img/ETH.png" style="width:16px; height:16px; vertical-align:middle">
                </template>
              </el-table-column>
              <el-table-column label="总额" width="100">
                <template #default="{ row }">
                  {{ row.total }} <img src="@/img/USD.png" style="width:16px; height:16px; vertical-align:middle">
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="创建时间" width="180" />
              <el-table-column prop="status" label="状态" width="120">
                <template #default="{ row }">
                  <el-tag 
   :type='warning'
      :class="'status-tag status-1' "
    >
      {{ row.status }}
    </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="历史订单">
            <el-table :data="OverOrders" style="width: 100%" height="300">
              <el-table-column prop="price" label="价格" width="120" sortable>
                <template #default="{ row }">

                </template>
              </el-table-column>
              <el-table-column label="数量" width="100">
                <template #default="{ row }">
                  {{ row.amount }} <img src="@/img/ETH.png" style="width:16px; height:16px; vertical-align:middle">
                </template>
              </el-table-column>
              <el-table-column label="总额" width="100">
                <template #default="{ row }">
                  {{ row.total }} <img src="@/img/USD.png" style="width:16px; height:16px; vertical-align:middle">
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="创建时间" width="180" />
              <el-table-column prop="status" label="状态" width="120">
                <template #default="{ row }">
                  <el-tag 
      :type="success"
      :class="'status-tag status-0' "
    >
      {{ row.status }}
    </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

       
          
     

        </el-tabs>
      </div>
    </el-card>
<!-- 创建订单弹窗 -->

<el-drawer 
  v-model="createOrderDialogVisible" 
  :title="`${orderForm.type === 'buy' ? '买入' : '卖出'} ${currentSymbol}`" 
  direction="rtl" 
  class="order-drawer"
  size="50%"
>
  <div class="drawer-content">
    <el-descriptions title="订单信息" :column="1" border class="order-descriptions">
      <el-descriptions-item>
        <template #label>
          <div class="label-content">
            <img src="@/img/buy.png" alt="Order Type" class="order-icon"/>
            <span>交易类型</span>
          </div>
        </template>
        <el-radio-group v-model="orderForm.type">
          <el-radio-button label="buy">买入</el-radio-button>
          <el-radio-button label="sell">卖出</el-radio-button>
        </el-radio-group>
      </el-descriptions-item>
      
      <el-descriptions-item>
        <template #label>
          <div class="label-content">
            <img src="@/img/inclusivity.png" alt="Order Setting" class="order-icon"/>
            <span>订单类型</span>
          </div>
        </template>
        <el-radio-group v-model="orderForm.orderType" @change="handleOrderTypeChange">
          <el-radio-button label="limit">限价</el-radio-button>
          <el-radio-button label="market">市价</el-radio-button>
        </el-radio-group>
      </el-descriptions-item>
      
      <el-descriptions-item>
        <template #label>
          <div class="label-content">
            <img src="@/img/price-tag.png" alt="Price" class="order-icon"/>
            <span>价格</span>
          </div>
        </template>
        <div class="input-with-unit" v-if="orderForm.orderType === 'limit'">
          <el-input-number 
            v-model="orderForm.price" 
            :min="0.0001" 
            :step="0.0001" 
            :precision="4"
            controls-position="right"
            class="number-input"
          />
          <span class="unit">USDT</span>
        </div>
        <div v-else class="market-price-display">
  
          <span class="market-price-hint">{{MarketPrice}}</span>
        </div>
      </el-descriptions-item>
      
      <el-descriptions-item>
        <template #label>
          <div class="label-content">
            <img src="@/img/ethereum.png" alt="Amount" class="order-icon"/>
            <span>数量</span>
          </div>
        </template>
        <div class="input-with-unit">
          <el-input-number 
            v-model="orderForm.amount" 
            :min="0.0001" 
            :step="0.0001" 
            :precision="4"
            controls-position="right"
            class="number-input"
            @change="updateMarketOrderPrice"
          />
          <span class="unit">{{ currentSymbol }}</span>
        </div>
        <div class="balance-hint">
          可用: {{ orderForm.type === 'buy' ? formatBalance(userBalance) : formatBalance(tokenBalance) }}
        </div>
      </el-descriptions-item>
      
      <el-descriptions-item>
        <template #label>
          <div class="label-content">
            <img src="@/img/economy.png" alt="Total" class="order-icon"/>
            <span>总额</span>
          </div>
        </template>
        <span class="total-amount">
          {{ calculateTotal() }} USDT
          <span v-if="orderForm.orderType === 'market'" class="market-total-hint">
            (预估)
          </span>
        </span>
      </el-descriptions-item>
    </el-descriptions>

    <!-- 操作按钮 -->
    <div class="order-actions">
      <el-button 
        type="primary" 
        @click="submitOrder"
        :loading="submittingOrder"
        class="submit-btn"
      >
        {{ orderForm.type === 'buy' ? '买入' : '卖出' }} {{ currentSymbol }}
      </el-button>
      <el-button 
        @click="createOrderDialogVisible = false" 
        class="cancel-btn"
      >
        取消
      </el-button>
    </div>
  </div>
</el-drawer>
    </div>
  </template>
  //=============================================================================
  <script>
  import { format } from 'date-fns';
  import { ArrowLeft, Warning } from '@element-plus/icons-vue'; // 引入返回图标  
  import * as echarts from 'echarts';
 import trade from '@/utils/trade';
 import qs from 'qs';
 import { web3, contract, connectWallet } from '@/utils/web3';
import axios from 'axios';
import account from '@/utils/account';
  export default { 
    name: 'TradingInterface',
    data() {
      const validateAmount = (rule, value, callback) => {
        const maxAmount = this.orderForm.type === 'buy' 
          ? this.userBalance / (this.orderForm.orderType === 'market' ? this.latestPrice : this.orderForm.price)
          : this.tokenBalance;
        
        if (value <= 0) {
          callback(new Error('数量必须大于0'));
        } else if (value > maxAmount) {
          callback(new Error(`数量不能超过可用余额`));
        } else {
          callback();
        }
      };
  
      return {
        currentSymbol: 'ETH',
        latestPrice: 0,
        prevPrice: 0,
        priceChange: 0.69,
        priceDirection: 0,
        userId:this.$route.query.userId,
        MarketPrice:1,
        activeOrderType: 'market', // 'market' 或 'my'
        
        // 盘口数据
        bidOrders: [
          { price: 72450.12, amount: 1.25, total: (72450.12 * 1.25).toFixed(2) },
          { price: 72400.50, amount: 0.85, total: (72400.50 * 0.85).toFixed(2) },
          { price: 72380.75, amount: 2.10, total: (72380.75 * 2.10).toFixed(2) },
          { price: 72350.30, amount: 1.50, total: (72350.30 * 1.50).toFixed(2) },
          { price: 72300.00, amount: 3.20, total: (72300.00 * 3.20).toFixed(2) }
        ],
        askOrders: [
          { price: 1787.37, amount: 0.95, total: (72550.88 * 0.95).toFixed(2) },
          { price: 1782.67, amount: 1.30, total: (72600.25 * 1.30).toFixed(2) },
          { price: 1784.44, amount: 0.75, total: (72650.70 * 0.75).toFixed(2) },
          { price: 1790.29, amount: 2.10, total: (72700.40 * 2.10).toFixed(2) },
          { price: 1777.15, amount: 1.45, total: (72750.90 * 1.45).toFixed(2) }
        ],
        
        // 市场订单
        marketBidOrders: [],
        marketAskOrders: [],
        
        // 我的订单
        showMyOrders: false,
        myBidOrders: [],
        myAskOrders: [],
        RiskOrders: [],
        OverOrders: [],
        
        // 用户余额
        userBalance: 0, // USDT余额
        tokenBalance: 0.9,     // BTC余额
        
        // 创建订单对话框
        createOrderDialogVisible: false,
        submittingOrder: false,
        orderForm: {
          type: 'buy',      // buy/sell
          orderType: 'limit', // limit/market
          price: 0,
          amount: 0
        },


        orderRules: {
          type: [{ required: true, message: '请选择交易类型', trigger: 'change' }],
          orderType: [{ required: true, message: '请选择订单类型', trigger: 'change' }],
          price: [
            { required: true, message: '请输入价格', trigger: 'blur' },
            { type: 'number', min: 0.0001, message: '价格必须大于0', trigger: 'blur' }
          ],
          amount: [
            { required: true, message: '请输入数量', trigger: 'blur' },
            { type: 'number', min: 0.0001, message: '数量必须大于0', trigger: 'blur' },
            { validator: validateAmount, trigger: 'blur' }
          ]
        }
      };
    },
    computed: {
      priceIcon() {
        return this.priceDirection === 'up' ? 'el-icon-top' : 'el-icon-bottom';
      }
    },
    created() {
    
      this.fetchMarketOrders();
      this.fetchMyOrders();
       this.getETHPrice();
       this.fetchBalance();
      // 模拟实时价格更新
      this.priceUpdateInterval = setInterval(() => {
       // this.updateMarketData();
      }, 3000);
    },
    beforeDestroy() {
      clearInterval(this.priceUpdateInterval);
    },
    methods: {
       async fetchBalance() {
      try {
        const response = await account.post('/balance', qs.stringify({
          userId: this.userId,
        }), {
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        });
        this.userBalance = response.bau;
        
        const accounts = await web3.eth.getAccounts();
        const userAddress = accounts[0];  // 确保地址正确
        
        // 传递 userAddress 作为参数
        const contractBalance = await contract.methods.checkBalance(userAddress).call();
       this.tokenBalance = web3.utils.fromWei(contractBalance, "ether");
        this.showBalanceDrawer = true;
      } catch (error) {
        console.error('获取合约余额失败:', error);
        alert('获取余额失败，请稍后重试！');
      }
    },
      resetOrderForm() {
    this.$refs.orderForm?.resetFields(); // 重置表单验证状态
    
    this.orderForm = {
      type: 'buy',
      orderType: 'limit',
      price: this.latestPrice || 0.0001, // 重置为最新价格或默认值
      amount: 0,
      marketPrice:this.marketPrice, // 保持市价同步
    };},

      takeOrder(order, action) {
      this.$confirm(`确定要${action === 'buy' ? '买入' : '卖出'}该订单吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          // 模拟交易处理
          const bidResponse = await trade.post('/UpdateOrder', qs.stringify({
          tradeId: order.tradeId,
        }));
          
          this.$message.success(`订单${action === 'buy' ? '买入' : '卖出'}成功`);
          
          // 更新市场订单列表
          this.fetchMarketOrders();
          
          // 更新我的订单列表
          this.fetchMyOrders();
          
          // 更新余额
          if (action === 'buy') {
            this.tokenBalance += order.amount;
            this.userBalance -= order.total;
          } else {
            this.tokenBalance -= order.amount;
            this.userBalance += order.total;
          }
        } catch (error) {
          this.$message.error('交易失败: ' + error.message);
        }
      }).catch(() => {});
    },

      switchOrderType(type) {
      this.activeOrderType = type;
      if (type === 'my') {
        this.fetchMyOrders();
      } else {
        this.fetchMarketOrders();
      }
    },
      async  getETHPrice() {
  const response = await fetch(
    'https://api.coingecko.com/api/v3/simple/price?ids=ethereum&vs_currencies=usd'
  );
  const data = await response.json();
  this.priceDirection=data.ethereum.usd
  this.latestPrice=data.ethereum.usd
  this.prevPrice=data.ethereum.usd-10
  this.MarketPrice=data.ethereum.usd
  console.log(`ETH 当前价格为 $${data.ethereum.usd}`);
},
/////======================================================
 // 获取市场订单（userId=0获取所有订单）
  // 获取市场订单
  formatDateTime(dateTime) {
  if (!dateTime) return '--';
  return format(new Date(dateTime), 'yyyy-MM-dd HH:mm:ss');
},
  async fetchMarketOrders() {
      try {
        // 获取市场买单 (userId=0表示获取所有订单)
        const bidResponse = await trade.post('/GetOrdersByType', qs.stringify({
          userId: 0,
          tradeType: 'buy'
        }));
        
        if (Array.isArray(bidResponse.data)) {
          this.marketBidOrders = bidResponse.data.map(order => ({
            ...order,
            price: order.money+'USD',
            amount: order.amount  ,
            createdAt: this.formatDateTime(order.createTime),
            creator: `${order.userId}`, // 示例：显示用户ID
            total: order.money * order.amount  
    

          }));
        } else {
          this.marketBidOrders = [];
        }
        
        // 获取市场卖单
        const askResponse = await trade.post('/GetOrdersByType', qs.stringify({
          userId: 0,
          tradeType: 'sell'
        }));
        
        if (Array.isArray(askResponse.data)) {
          this.marketAskOrders = askResponse.data.map(order => ({
            ...order,
            price: order.money+'USD',
            amount: order.amount,
            createdAt: this.formatDateTime(order.createTime),
            creator: `${order.userId}`, // 示例：显示用户ID
            total: order.money * order.amount  // 示例：计算总金额，此处仅为示意，实际应根据实际情况调整字段名和逻辑。例如，如果订单数据中已有total字段，则直接使用该字段即可。 若无此字段，则需要根据其他相关字段（如price
          }));
        } else {
          this.marketAskOrders = [];
        }
      } catch (error) {
        console.error('获取市场订单失败:', error);
        this.marketBidOrders = [];
        this.marketAskOrders = [];
      }
    },


     // 获取我的订单
     async fetchMyOrders() {
      try {
        // 获取我的买单
        const bidResponse = await trade.post('/GetOrdersByType', qs.stringify({
        
          userId: this.userId,
          tradeType: 'buy'
        }));
        console.log('我的买单:', bidResponse.data,'userId:',this.userId);
        
        if (true) {
          this.myBidOrders = bidResponse.data.map(order => ({
            ...order,
            
            price: order.money+'USD',
            amount: order.amount,
            createdAt: this.formatDateTime(order.createTime),
            status: this.getStatusTag(order.status),
            total: order.money * order.amount  // 示例：计算总金额，此处仅为示意，实际应根据实际情况调整字段名和逻辑。例如，如果订单数据中已有total字段，则直接使用该字段即可。 若无此字段，则需要根据其他相关字段（如price
          }));
        } else {
          this.myBidOrders = [];
        }
        
        // 获取我的卖单
        const askResponse = await trade.post('/GetOrdersByType', qs.stringify({
          userId: this.userId,
          tradeType: 'sell'
        }));
        console.log('我的卖单:', bidResponse.data,'userId:',this.userId);
       
        if (true) {
          
          this.myAskOrders = askResponse.data.map(order => ({
            ...order,
            price: order.money+'USD',
            amount: order.amount,
            createdAt: this.formatDateTime(order.createTime),
        status: this.getStatusTag(order.status), 
            total: order.money * order.amount  // 示例：计算总金额，此处仅为示意，实际应根据实际情况调整字段名和逻辑。例如，如果订单数据中已有total字段，则直接使用该字段即可。 若无此字段，则需要根据其他相关字段（如price
          }));
        } else {
          this.myAskOrders = [];
        }

  // 获取已完成订单
  const bidResponse2 = await trade.post('/getOrdersByStatus', qs.stringify({
          userId: this.userId,
          status: 0,

        }));
        console.log('我的完成单:', bidResponse.data,'userId:',this.userId);
        if (true) {
          this.OverOrders = bidResponse2.data.map(order => ({
            ...order,
           
            price: order.money+'USD',
            amount: order.amount,
            createdAt: this.formatDateTime(order.createTime),
            status: this.getStatusTag(order.status), 
            total: order.money * order.amount  // 示例：计算总金额，此处仅为示意，实际应根据实际情况调整字段名和逻辑。例如，如果订单数据中已有total字段，则直接使用该字段即可。 若无此字段，则需要根据其他相关字段（如price
          }));
        } else {
          this.OverOrders = [];
        }
        //获取风险订单
        const bidResponse3 = await trade.post('/getExamineOrders', qs.stringify({
          userId: this.userId,
        }));
        
        if (true) {
          this.RiskOrders = bidResponse3.data.map(order => ({
            ...order,
           
            price: order.money+'USD',
            amount: order.amount,
            createdAt: this.formatDateTime(order.createTime),
            status: this.getStatusTag(order.status), 
            total: order.money * order.amount  // 示例：计算总金额，此处仅为示意，实际应根据实际情况调整字段名和逻辑。例如，如果订单数据中已有total字段，则直接使用该字段即可。 若无此字段，则需要根据其他相关字段（如price
          }));
        } else {
          this.OverOrders = [];
        }

      } catch (error) {
        console.error('获取我的订单失败111:', error);
        this.myBidOrders = [];
        this.myAskOrders = [];
      }
      
    },
async submitOrder(){
  if(((this.orderForm.type==='buy')&&(this.orderForm.price*this.orderForm.amount>this.userBalance/2))||((this.orderForm.type==='sell')&&(this.orderForm.amount>this.tokenBalance/2)
)){
   
      this.$confirm('此订单为大额交易,确定要提交订单吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        
      }).then(async () => {
        this. realSubmitOrder(3);
        ;

      }).catch(() => {});}
else{
  this.realSubmitOrder(1);
}



},
    // 提交订单
    async realSubmitOrder(theStatus) {
      this.submittingOrder = true;
      try {
        const orderData = {
          userId: this.userId,
          tradeType: this.orderForm.type,
          money: this.orderForm.price===0.0001? this.MarketPrice: this.orderForm.price,
          amount: this.orderForm.amount,
          status: theStatus 
        };
        
        const response = await trade.post('/CreateOrder', orderData);
        console.log(response);
        if (response.code ==='200') {
          this.resetOrderForm();
          console.log('订单创建成功');
        //  this.userBalance-=orderData.money;

          ///

try {
        const response = await account.post('/Withdraw', qs.stringify({
          userId: this.userId,
          value: orderData.money
        }), {
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        });

        // 根据后端返回的状态码和消息显示不同的提示
        if (response.status === 200) {
          this.transactionAmount = 0;
        
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
          ////
          if(theStatus===3){
            this.$message.warning('订单创建成功，请等待管理员审批')
          }else{
            this.$message.success('订单创建成功');
          }
        
          this.createOrderDialogVisible = false;
          // 刷新订单列表
          this.fetchMarketOrders();
          this.fetchMyOrders();
        }else {
       
          this.$message.warning('创建订单频率过高，请稍后再试');
        }
      } catch (error) {
        console.error('创建订单失败:', error);
      } finally {
        this.submittingOrder = false;
      }
    },

    // 取消订单
    async cancelOrder(order) {
      console.log('订单id',order.tradeId),
      this.$confirm('确定要撤销该订单吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        
      }).then(async () => {
        try {
          const response = await trade.delete('/CancelOrder', {
            params: {
              tradeId: order.tradeId,
              userId: this.userId,

            }
          });
         console.log(response);
          if (response.code === "200") {
            this.$message.success('订单已撤销');
            // 更新订单列表
            if (order.tradeType === 'buy') {
              this.switchToMyOrders()
            } else {
            
              this.switchToMyOrders()
            }
          } else {  
            this.$message.warning('撤销订单频率过高，请稍后再试！');
          }

          
        } catch (error) {
          this.$message.error('撤销订单失败: ' + error.message);
         
        }
      }).catch(() => {});
    },
    getStatusTag(statusCode) {
      const statusMap = {
        1: '待成交',
        0: '已完成',
        3: '待审核',
      };
      return statusMap[statusCode] || { text: '未知状态', type: 'info' };
    },

      ///=============================================================
      goBack() {
      this.$router.push({
        name: 'UserInfo'
      });
    },
      formatBalance(value) {
        return parseFloat(value).toFixed(4);
      },

      //===获取eth余额
      async fetchUserBalance() {
  try {
    const account = await connectWallet();
    if (!account) return;
    
    // 调用TradingContract的checkBalance方法
    const balance = await tradingContract.methods.checkBalance(account).call();
    this.userBalance = web3.utils.fromWei(balance, 'ether');
  } catch (error) {
    console.error("获取余额失败:", error);
  }
},
      ///====
      
      calculateTotal() {
        if (this.orderForm.orderType === 'market') {
          return (this.latestPrice * this.orderForm.amount).toFixed(2);
        }
        return (this.orderForm.price * this.orderForm.amount).toFixed(2);
      },
      
      // updateMarketData() {
      //   // 模拟价格变动
      //   const change = (Math.random() - 0.5) * 100;
      //   this.prevPrice = this.latestPrice;
      //   this.latestPrice = parseFloat((this.latestPrice + change).toFixed(2));
        
      //   // 计算涨跌幅
      //   this.priceChange = parseFloat(((this.latestPrice - this.prevPrice) / this.prevPrice * 100).toFixed(2));
      //   this.priceDirection = this.latestPrice >= this.prevPrice ? 'up' : 'down';
        
      //   // 更新盘口数据
      //   this.updateOrderBook();
      // },
      
      updateOrderBook() {
        // 更新买盘
        this.bidOrders = this.bidOrders.map(order => {
          const change = (Math.random() - 0.5) * 50;
          const newPrice = parseFloat((order.price + change).toFixed(2));
          return {
            price: newPrice > this.latestPrice ? this.latestPrice - 100 : newPrice,
            amount: parseFloat((order.amount * (0.95 + Math.random() * 0.1)).toFixed(4)),
            total: (newPrice * order.amount).toFixed(2)
          };
        }).sort((a, b) => b.price - a.price);
        
        // 更新卖盘
        this.askOrders = this.askOrders.map(order => {
          const change = (Math.random() - 0.5) * 50;
          const newPrice = parseFloat((order.price + change).toFixed(2));
          return {
            price: newPrice < this.latestPrice ? this.latestPrice + 100 : newPrice,
            amount: parseFloat((order.amount * (0.95 + Math.random() * 0.1)).toFixed(4)),
            total: (newPrice * order.amount).toFixed(2)
          };
        }).sort((a, b) => a.price - b.price);
      },
      
   
      
      showCreateOrderDialog() {
  this.createOrderDialogVisible = true;
  this.$nextTick(() => {
    const dialog = document.querySelector('.el-dialog');
    if (dialog) {

    }
  });
}
      ,

      
     
      
      switchToMyOrders() {
        this.showMyOrders = true;
        this.fetchMyOrders();
      },
      
      switchToMarketOrders() {
        this.showMyOrders = false;
      },
      
      
    },
  

  };
  </script>
  
  <style scoped>
  .order-type-buttons {
  margin: 0 15px;
}

.orders-section {
  margin-top: 20px;
}
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
  .number-input {
  flex: 1;
}

.short-input {
  width: 20px; /* 或者使用 max-width: 200px; 固定值 */
}

.input-with-unit {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 50%; /* 确保容器宽度充满 */
}
  .drawer-content {
  padding: 20px;
}

.order-descriptions {
  margin-bottom: 24px;
}

.label-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.order-icon {
  width: 30px;
  height: 30px;
  object-fit: contain;
}

.input-with-unit {
  display: flex;
  align-items: center;
  gap: 8px;
}

.number-input {
  flex: 1;
}

.unit {
  color: #909399;
  font-size: 14px;
}

.balance-hint {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.total-amount {
  font-weight: bold;
  color: var(--el-color-primary);
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.submit-btn {
  min-width: 120px;
}

.cancel-btn {
  min-width: 80px;
}
  .trading-container {
    display: flex;
  flex-direction: column;
  align-items: center; /* 确保内容居中 */
    /* max-width: 1200px; */
    /* margin: 0 auto; */
    padding: 20px;
    
  background: linear-gradient(135deg, #53daf8 0%, #4e88ed 100%);
  }
  
  .header-title {
    text-align: center;
    margin-bottom: 20px;
  }
  
  .title-text {
    font-size: 28px;
    color: #333;
    margin-bottom: 10px;
  }
  
  .subtitle {
    font-size: 16px;
    color: #666;
  }
  
  .trading-card {
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    
  }
  
  .user-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .action-btn {
    margin-right: 10px;
  }
  
  .user-balance {
    font-size: 14px;
    color: #666;
  }
  
  .section-title {
    font-size: 18px;
    color: #333;
    margin: 20px 0 15px;
    display: flex;
    align-items: center;
  }
  
  .section-title i {
    margin-right: 8px;
  }
  
  .back-btn {
    margin-left: 15px;
  }
  
  .price-display {
    margin-bottom: 20px;
  }
  
  .current-price {
    display: flex;
    align-items: center;
    margin-bottom: 15px;
  }
  
  .price-label {
    font-size: 16px;
    margin-right: 10px;
  }
  
  .price-value {
    font-size: 24px;
    font-weight: bold;
    margin-right: 15px;
  }
  
  .price-value.up {
    color: #f56c6c;
  }
  
  .price-value.down {
    color: #67c23a;
  }
  
  .price-change {
    font-size: 16px;
    padding: 2px 8px;
    border-radius: 4px;
  }
  
  .price-change.positive {
    background-color: #f0f9eb;
    color: #67c23a;
  }
  
  .price-change.negative {
    background-color: #fef0f0;
    color: #f56c6c;
  }
  
  .order-book {
    display: flex;
    gap: 20px;
  }
  
  .bids, .asks {
    flex: 1;
  }
  
  .bids h3 {
    color: #67c23a;
  }
  
  .asks h3 {
    color: #f56c6c;
  }
  
  .bid-price {
    color: #67c23a;
  }
  
  .ask-price {
    color: #f56c6c;
  }
  
  
  .market-orders-section, .my-orders-section {
    margin-top: 30px;
  }
  .el-dialog {
  z-index: 9999 !important;
  
}
/* 状态标签基础样式 */
.status-tag {
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
  font-weight: 500;
}

/* 特定状态样式 */
.status-1 { /* 待成交 */
  background-color: #fdf6ec;
  border-color: #faecd8;
  color: #e6a23c;
}

.status-0 { /* 已完成 */
  background-color: #f0f9eb;
  border-color: #e1f3d8;
  color: #67c23a;
}


/* 鼠标悬停效果 */
.status-tag:hover {
  opacity: 0.8;
  transform: scale(1.05);
  transition: all 0.2s;
}

  </style>