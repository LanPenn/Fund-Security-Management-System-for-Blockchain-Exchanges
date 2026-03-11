// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/security/ReentrancyGuard.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

contract FundsManagement is ReentrancyGuard, Ownable {
    // Mapping to store balances for each user
    mapping(address => uint256) public balances;
    mapping(address => uint256) public freezeFunds;

    // Address of the trusted TradingContract
    address public tradingContract;

    // Event to log deposits
    event Deposited(address indexed user, uint256 amount);

    // Event to log withdrawals
    event Withdrawn(address indexed user, uint256 amount);

    // Event to log TradingContract address change
    event TradingContractUpdated(address newTradingContract);

    constructor() payable {
        tradingContract = msg.sender; // Default to deployer, can be updated later
    }

    // Set the TradingContract address (onlyOwner)
    function setTradingContract(address _tradingContract) external onlyOwner {
        require(_tradingContract != address(0), "Invalid address");
        tradingContract = _tradingContract;
        emit TradingContractUpdated(_tradingContract);
    }

    // Deposit function (no reentrancy risk as it only updates state)
    function deposit() public payable {
        require(msg.value > 0, "Deposit amount must be greater than 0");
        balances[msg.sender] += msg.value;
        emit Deposited(msg.sender, msg.value);
    }

    // Withdraw function (protected by nonReentrant)
    function withdraw(uint256 amount) public nonReentrant {
        require(balances[msg.sender] >= amount, "Insufficient balance");
        
        // Update state before external call (Checks-Effects-Interactions)
        balances[msg.sender] -= amount;
        
        // Safe transfer (reentrancy protected)
        (bool success, ) = payable(msg.sender).call{value: amount}("");
        require(success, "Transfer failed");
        
        emit Withdrawn(msg.sender, amount);
    }

    // Transfer between accounts (only callable by TradingContract)
    function transferAccounts(uint256 amount, address from, address to) external {
        require(msg.sender == tradingContract, "Unauthorized");
        require(balances[from] >= amount, "Insufficient balance");
        
        balances[from] -= amount;
        balances[to] += amount;
    }

    // Freeze funds (only callable by TradingContract)
    function freeze(address user, uint256 amount) external {
        require(msg.sender == tradingContract, "Unauthorized");
        require(balances[user] >= amount, "Insufficient balance");
        
        balances[user] -= amount;
        freezeFunds[user] += amount;
    }

    // Thaw funds (only callable by TradingContract)
    function thaw(address user, uint256 amount) external {
        require(msg.sender == tradingContract, "Unauthorized");
        require(freezeFunds[user] >= amount, "Insufficient frozen balance");
        
        freezeFunds[user] -= amount;
        balances[user] += amount;
    }

    // Check user balance (view function, no state change)
    function checkBalance(address user) public view returns (uint256) {
        return balances[user];
    }

    // Emergency withdraw ETH by owner (protected by nonReentrant)
    function emergencyWithdrawETH(uint256 amount) external onlyOwner nonReentrant {
        require(address(this).balance >= amount, "Insufficient contract balance");
        (bool success, ) = payable(owner()).call{value: amount}("");
        require(success, "Transfer failed");
    }
}