package com.example.graduationdsign.entity;

import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
public class ResponseJson {
    private String code;
    private String msg;
    private Object data;



    public ResponseJson(String code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public ResponseJson() {
    }
    public ResponseJson getCode(Object data, String msg) {

        this.data = data;
        this.msg = msg; // 设置消息
        this.code = "200"; // 设置成功状态码
        return this;

    }
    public ResponseJson fail(String msg) {
        this.msg = msg;
        this.code = "-1";
        return this;
    }

    public ResponseJson valueAbnormal(String msg) {
        this.msg = msg;
        this.code = "429";
        return this;
    }
}
