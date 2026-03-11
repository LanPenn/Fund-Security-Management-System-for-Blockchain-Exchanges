package com.example.graduationdsign.service;

import com.example.graduationdsign.entity.ResponseJson;
import com.example.graduationdsign.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Service
public interface UserService {


    ResponseEntity<String> register(User user);
    ResponseEntity<?> login(String username, String password);
    ResponseEntity<User> getUserInfo();

    ResponseEntity<?> logout();
    ResponseEntity<?> updatePassword(String oldValue,String newValue );
    ResponseEntity<?> updateUsername(int id,String value );
    ResponseEntity<?> updateEmail(int id,String value );
    ResponseEntity<?> updateCancel();

    ResponseEntity<?> verifyPassword(String passWord);
     ResponseJson getUserList ();

    ResponseJson updateStatus(int userId,int status);

    ResponseJson getUsersByName(String param);
}
