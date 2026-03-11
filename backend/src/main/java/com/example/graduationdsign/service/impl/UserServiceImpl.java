package com.example.graduationdsign.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduationdsign.dao.AccountMapper;
import com.example.graduationdsign.entity.Account;
import com.example.graduationdsign.entity.LoginResponse;
import com.example.graduationdsign.entity.ResponseJson;
import com.example.graduationdsign.entity.User;
import com.example.graduationdsign.dao.UserMapper;
import com.example.graduationdsign.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.regex.Pattern;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private static final String QQ_EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@qq\\.com$|^[A-Za-z0-9._%+-]+@vip\\.qq\\.com$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(QQ_EMAIL_REGEX);

    static  int allid;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    ResponseJson responseJson;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 用户注册
    @Override
    public ResponseEntity<String> register(User user) {
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "邮箱格式有误，请重试");
        }
        Integer count = userMapper.selectCount(new QueryWrapper<User>().eq("username", user.getUsername()));
        if (count > 0) {
            System.out.println(count);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户名已存在");
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setRole("2"); // 默认普通用户
        user.setStatus(1); // 默认账号正常
        userMapper.insert(user);
        Integer userId = user.getUserId();
        accountMapper.insert(new Account(0, 0, 0, user.getCreateTime(), user.getUserId()));
        System.out.println("注册成功:" + user);
        return ResponseEntity.ok("注册成功");
    }
    @Override
    // 用户登录
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        System.out.println("用户名：" + username + " 密码：" + password);
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        int status = user.getStatus();
        System.out.println("状态："+status);
        if(status==3){
            return ResponseEntity.ok().body(new LoginResponse("封禁", user.getRole()));
        }
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            Integer userId = user.getUserId();
            allid = userId;
            System.out.println("用户："+user.getUsername()+"登录成功!" );
            return ResponseEntity.ok().body(new LoginResponse("登录成功", user.getRole()));
        }
        return ResponseEntity.badRequest().body(new LoginResponse("密码错误", null));
        }



    // 用于返回登录结果的简单数据结构
    @Override
    public ResponseEntity<User> getUserInfo() {
        User user = userMapper.selectById(allid);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(user);
    }

    @Override
    public ResponseEntity<?> logout() {
        User user = userMapper.selectById(allid);
        System.out.println("用户:"+user.getUsername()+"退出登录~");
        allid=-1;
        return ResponseEntity.ok("退出成功");
    }

    @Override
    public ResponseEntity<?> updatePassword(String oldValue,String newValue ) {
        int id = allid;
        String oldPassWord = userMapper.checkPassword(id);
        String encodedPassword = passwordEncoder.encode(newValue);
        if (passwordEncoder.matches(oldValue, oldPassWord)) {
            int result = userMapper.updatePassword(id, encodedPassword);
            if (result == 1) {
                return ResponseEntity.ok("修改成功");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("修改失败");

        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("旧密码错误");
        }
    }

    @Override
    public ResponseEntity<?> updateUsername(int id, String value) {
        int result= userMapper.updateUsername(id,value);
        if(result==1){
            return ResponseEntity.ok("修改成功");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("修改失败");
    }


    @Override
    public ResponseEntity<?> updateEmail(int id, String value) {
        int result= userMapper.updateEmail(id,value);
        if(result==1){
            return ResponseEntity.ok("修改成功");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("修改失败");
    }


    @Override
    public ResponseEntity<?> updateCancel(){
         int id = allid;
      int result = userMapper.updateCancel(id);
        if(result==1){
         return ResponseEntity.ok("注销成功");
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("注销失败");
        }

    }




    @Override
    public ResponseEntity<?> verifyPassword(String passWord){
        int id = allid;
        String oldPassWord = userMapper.checkPassword(id);
        if (  passwordEncoder.matches(passWord, oldPassWord)) {

            return ResponseEntity.ok("验证成功");
        }
        return ResponseEntity.badRequest().body("验证失败");

    }


     public ResponseJson getUserList (){
         List<User> userList = userMapper.selectList(null);
        return responseJson.getCode(userList,"查询成功");
     }

    @Override
    public ResponseJson updateStatus(int userId,int status) {
      boolean success = userMapper.updateStatus(userId,status)>0;
        return responseJson.getCode(success,"用户状态修改为"+status);
    }

    @Override
    public ResponseJson getUsersByName(String param) {
        List<User> users = userMapper.getUsersByName(param);
        return responseJson.getCode(users,"查询成功");
    }
}



