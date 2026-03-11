package com.example.graduationdsign.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    @TableId(type = IdType.AUTO)
    private Integer userId;
    @TableField("username")
    private String username;
    private String password;
    private LocalDateTime createTime;
    private int status;
    private String role;
    private String email;

    @Override
    public String toString() {
        return "User{" +
                "user_id=" + userId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", createTime=" + createTime +
                ", status=" + status +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

