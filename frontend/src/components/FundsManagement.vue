<template>
  <div class="funds-management">
    <div class="header">
      <img alt="Vue logo" class="logo" src="@/assets/logo.svg" width="125" height="125" />
      <h1>资金管理</h1>
    </div>

    <div class="wallet-section">
      <button class="connect-wallet-btn" @click="connectWallet">连接钱包</button>
    </div>

    <div class="balance-section">
      <div class="balance-info">
        <p>当前余额: <strong>{{ balance }} ETH</strong></p>
      </div>
      <button class="balance-btn" @click="checkBalance">查询余额</button>
    </div>

    <div class="transaction-section">
      <div class="transaction-card">
        <h3>存款</h3>
        <input type="number" v-model="depositAmount" placeholder="存款金额（ETH）" />
        <button class="action-btn deposit-btn" @click="deposit">存款</button>
      </div>

      <div class="transaction-card">
        <h3>提现</h3>
        <input type="number" v-model="withdrawAmount" placeholder="提现金额（ETH）" />
        <button class="action-btn withdraw-btn" @click="withdraw">提现</button>
      </div>
    </div>
  </div>
</template>

<script>
import { web3, connectWallet, contract } from "@/utils/web3";

export default {
  data() {
    return {
      balance: "0.00",
      depositAmount: "",
      withdrawAmount: "",
    };
  },
  methods: {
    async connectWallet() {
      await connectWallet();
      this.checkBalance();
    },

    async checkBalance() {
      try {
        const accounts = await web3.eth.getAccounts();
        const balanceWei = await contract.methods.checkBalance().call({ from: accounts[0] });
        this.balance = web3.utils.fromWei(balanceWei, "ether");
      } catch (error) {
        console.error("查询余额失败:", error);
      }
    },

    async deposit() {
      try {
        const accounts = await web3.eth.getAccounts();
        await contract.methods.deposit().send({
          from: accounts[0],
          value: web3.utils.toWei(this.depositAmount, "ether"),
        });
        alert("存款成功！");
        this.checkBalance();
      } catch (error) {
        console.error("存款失败:", error);
      }
    },

    async withdraw() {
      try {
        const accounts = await web3.eth.getAccounts();
        await contract.methods.withdraw(web3.utils.toWei(this.withdrawAmount, "ether")).send({
          from: accounts[0],
        });
        alert("提现成功！");
        this.checkBalance();
      } catch (error) {
        console.error("提现失败:", error);
      }
    },
  },
};
</script>

<style scoped>
.funds-management {
  max-width: 600px;
  margin: 30px auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  font-family: 'Arial', sans-serif;
}

.header {
  text-align: center;
}

.logo {
  margin-bottom: 20px;
}

h1 {
  font-size: 2rem;
  font-weight: bold;
  color: #333;
}

.wallet-section {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.connect-wallet-btn {
  background-color: #4CAF50;
  color: white;
  border: none;
  padding: 10px 20px;
  font-size: 1rem;
  cursor: pointer;
  border-radius: 8px;
  transition: background-color 0.3s;
}

.connect-wallet-btn:hover {
  background-color: #45a049;
}

.balance-section {
  text-align: center;
  margin-bottom: 20px;
}

.balance-info p {
  font-size: 1.2rem;
  color: #333;
}

.balance-btn {
  background-color: #2196F3;
  color: white;
  padding: 10px 20px;
  border: none;
  font-size: 1rem;
  cursor: pointer;
  border-radius: 8px;
  transition: background-color 0.3s;
}

.balance-btn:hover {
  background-color: #1976D2;
}

.transaction-section {
  display: flex;
  justify-content: space-between;
  gap: 20px;
}

.transaction-card {
  background-color: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  width: 48%;
}

.transaction-card h3 {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

input {
  width: 100%;
  padding: 10px;
  margin: 10px 0;
  font-size: 1rem;
  border: 1px solid #ddd;
  border-radius: 8px;
}

input:focus {
  outline: none;
  border-color: #2196F3;
}

.action-btn {
  width: 100%;
  padding: 12px;
  font-size: 1.1rem;
  border: none;
  cursor: pointer;
  border-radius: 8px;
}

.deposit-btn {
  background-color: #4CAF50;
  color: white;
}

.deposit-btn:hover {
  background-color: #45a049;
}

.withdraw-btn {
  background-color: #f44336;
  color: white;
}

.withdraw-btn:hover {
  background-color: #e53935;
}

@media (max-width: 768px) {
  .transaction-section {
    flex-direction: column;
    gap: 10px;
  }

  .transaction-card {
    width: 100%;
  }
}
</style>
