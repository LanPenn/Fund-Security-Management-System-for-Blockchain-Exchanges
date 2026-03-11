// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

// Import the Ownable and ReentrancyGuard contracts
import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/security/ReentrancyGuard.sol";

// Define the interface for the Funds Management contract
interface IFundsManagement {
    function deposit() external payable;
    function withdraw(uint256 amount) external;
    function checkBalance(address user) external view returns (uint256);
    function transferAccounts(uint256 amount ,address from ,address to ) external;
    function freeze (address user,uint256 amount) external;  
    function thaw (address user,uint256 amount) external;
}

contract TradingContract is Ownable, ReentrancyGuard {
    // Reference to the funds management contract (your deposit contract)
    IFundsManagement public fundsContract;

    // Struct for Buy and Sell Orders
    struct Order {
        uint256 amount; // ETH amount for buying/selling
        uint256 price;  // USD price for the order
        address user;   // User who created the order
        bool isBuy;     // True if it's a buy order, false if it's a sell order
        bool isActive;  // Order status (active or completed)
    }

    // Order IDs (for tracking orders)
    uint256 public nextOrderId=0;
    mapping(uint256 => Order) public orders;

    // Events for logging order creation and consumption
    event OrderCreated(uint256 orderId, address indexed user, bool isBuy, uint256 amount, uint256 price);
    event OrderConsumed(uint256 orderId, address indexed consumer);

 constructor(address _fundsContractAddress) Ownable(msg.sender) {
    fundsContract = IFundsManagement(_fundsContractAddress);
}
    // Create Buy Order (Buy ETH with USD)
    //创建者直接调用，填入自己地址和收购eth数量，记录在本合约
    function createBuyOrder(uint256 _amount, uint256 _price) external nonReentrant {      
        // Create the order
        orders[nextOrderId] = Order({
            amount: _amount,
            price: _price,
            user: msg.sender,
            isBuy: true,
            isActive: true
        });

        emit OrderCreated(nextOrderId, msg.sender, true, _amount, _price);

        nextOrderId++;
    }

    // Create Sell Order (Sell ETH for USD)
    function createSellOrder(uint256 _amount, uint256 _price) external payable nonReentrant {
        uint256 usdBalance = fundsContract.checkBalance(msg.sender);
           require(usdBalance > _amount , "Insufficient balance");
           require(_amount > 0, "please:amount>0");
           fundsContract.freeze(msg.sender,_amount);
        // Create the sell order
        orders[nextOrderId] = Order({
            amount: _amount,
            price: _price, 
            user: msg.sender,
            isBuy: false,
            isActive: true
        });

        emit OrderCreated(nextOrderId, msg.sender, false, _amount, 0);

        nextOrderId++;
    }

    // Consume Buy Order (when someone buys ETH)
    //消费者调用这个方法，填入订单id，根据订单的收购eth数量和收购地址从存款合约转入对应eth到对应地址
    function consumeBuyOrder(uint256 orderId) external payable nonReentrant {
        Order storage order = orders[orderId];
        require(order.isActive, "Order is not active");
        require(order.isBuy, "Order is not a buy order");
        uint256 usdBalance = fundsContract.checkBalance(order.user);
        require(usdBalance >= order.amount, "Insufficient balance-consumeBuyOrder");
 
        // Ensure the contract has enough ETH to transfer
        // Transfer the ETH to the buyer
       
      fundsContract.transferAccounts(order.amount,msg.sender,order.user);
       order.isActive = false;

        emit OrderConsumed(orderId, msg.sender);
    }

    // Consume Sell Order (when someone sells ETH)
    function consumeSellOrder(uint256 orderId) external nonReentrant {
        Order storage order = orders[orderId];
        require(order.isActive, "Order is not active");
        require(!order.isBuy, "Order is not a sell order");

        // Ensure the contract has enough funds for the sell transaction
           fundsContract.thaw(order.user,order.amount);
           fundsContract.transferAccounts(order.amount,order.user,msg.sender);

        // Mark the order as consumed
        order.isActive = false;

        emit OrderConsumed(orderId, msg.sender);
    }

    // Get active orders for a user
    function getActiveOrders(address _user) external view returns (uint256[] memory) {
        uint256[] memory activeOrderIds = new uint256[](nextOrderId);
        uint256 count = 0;
        for (uint256 i = 0; i < nextOrderId; i++) {
            if (orders[i].user == _user && orders[i].isActive) {
                activeOrderIds[count] = i;
                count++;
            }
        }

        // Resize the array to the number of active orders
        uint256[] memory result = new uint256[](count);
        for (uint256 i = 0; i < count; i++) {
            result[i] = activeOrderIds[i];
        }

        return result;
    }

    // Function to withdraw funds by the contract owner (admin)
    function withdrawFunds(uint256 amount) external onlyOwner nonReentrant {
        require(address(this).balance >= amount, "Insufficient contract balance");
        payable(owner()).transfer(amount);
    }


  function checkBalance(address user ) public view returns (uint256) {
        return fundsContract.checkBalance(user);
    }


    //取消买单

    //取消卖单

// Event for order cancellation
    event OrderCancelled(uint256 orderId, address indexed user, bool isBuy);

    // Cancel Buy Order
    function cancelBuyOrder(uint256 orderId) external nonReentrant {
        Order storage order = orders[orderId];
        require(order.isActive, "Order is not active");
        require(order.isBuy, "Order is not a buy order");
        require(order.user == msg.sender, "Not order owner");

        // Mark order as cancelled
        order.isActive = false;

        emit OrderCancelled(orderId, msg.sender, true);
    }

    // Cancel Sell Order
    function cancelSellOrder(uint256 orderId) external nonReentrant {
        Order storage order = orders[orderId];
        require(order.isActive, "Order is not active");
        require(!order.isBuy, "Order is not a sell order");
        require(order.user == msg.sender, "Not order owner");

        // Unfreeze the funds in the funds contract
        fundsContract.thaw(order.user, order.amount);

        // Mark order as cancelled
        order.isActive = false;

        emit OrderCancelled(orderId, msg.sender, false);
    }

}
