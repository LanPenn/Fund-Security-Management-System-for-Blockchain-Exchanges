package com.example.graduationdsign.entity;

import lombok.Data;

import java.util.Map;
@Data
public class testVO {
    String name;
    String sex;
    int age;
    int[] oreder;
    Map<Integer,String> map;
}
