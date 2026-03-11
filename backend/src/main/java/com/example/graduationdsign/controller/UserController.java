package com.example.graduationdsign.controller;

import com.example.graduationdsign.entity.ResponseJson;
import com.example.graduationdsign.entity.User;
import com.example.graduationdsign.entity.tradeAlert;
import com.example.graduationdsign.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    com.example.graduationdsign.dao.alterMapper alterMapper;
    @Autowired
    ResponseJson responseJson;
    // 用户注册
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        return userService.register(user);
    }

    // 用户登录
    @PostMapping("/login")
    public ResponseEntity<?>  login(String username, String password) {
        return userService.login( username,  password);
    }


    @GetMapping("/user/info")
    public ResponseEntity<User> getUserInfo() {
        // 假设你在 session 中存储了当前用户 ID
        return userService.getUserInfo();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        return userService.logout();

    }

    @GetMapping("/user/updateEmail")
    public ResponseEntity<?> updateEmail(@RequestParam int id, @RequestParam String value) {
        return userService.updateEmail(id, value );

    }

    @GetMapping("/user/updatePassword")
    public ResponseEntity<?> updatePassword( @RequestParam String oldValue, @RequestParam String newValue){
        return userService.updatePassword(oldValue,newValue );
    }

    @GetMapping("/user/updateUsername")
    public ResponseEntity<?> updateUsername(@RequestParam int id, @RequestParam String value){
        return userService.updateUsername(id, value);
    }

    @GetMapping("/user/cancel")
    public ResponseEntity<?> updateCancel(){
        return userService.updateCancel();
    }

    @GetMapping("/user/verifyPassword")
    public ResponseEntity<?> verifyPassword(@RequestParam String passWord){
        return userService.verifyPassword( passWord);
    }
    @PostMapping("/getUserList")
    public ResponseJson getUserList (){
        return userService.getUserList();
    }

    @PostMapping("/updateStatus")
    public ResponseJson updateStatus (@RequestParam int userId,@RequestParam int status){
        return userService.updateStatus(userId,status);
    }

    @PostMapping("/getUsersByName")
    public ResponseJson getUsersByName (@RequestParam String param){
        return userService.getUsersByName(param);
    }
    @PostMapping("/getAlerts")
    public ResponseJson getAlerts() {
        List<tradeAlert> Alerts = alterMapper.selectAllAlerts();
        return  responseJson.getCode(Alerts, "查询成功");
    }


    @PostMapping("/deleteAlertById")
    public ResponseJson getAlerts(int alertId) {
         boolean success = alterMapper.deleteAlertById(alertId)>0;
        return  responseJson.getCode(success, "删除结果");
    }

}
