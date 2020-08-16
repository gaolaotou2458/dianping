package com.imooc.dianping.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.dianping.common.BusinessException;
import com.imooc.dianping.common.CommonRes;
import com.imooc.dianping.common.EmBusinessError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/token")
public class TokenUserController{

    @Value("${admin.email}")
    private String email;
    @Value("${admin.encrptyPassord}")
    private String encrptyPassord;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public CommonRes login(@RequestHeader Map<String,Object> he, @RequestBody Map<String,Object> para) throws JsonProcessingException, UnsupportedEncodingException, NoSuchAlgorithmException, BusinessException {
        System.out.println(he);
        String username=(String)para.get("username");
        String password=(String)para.get("password");
        if(email.equals(this.email) && encodeByMd5(password).equals(this.encrptyPassord)){
            HashMap<String,Object> hs=new HashMap<>();
            hs.put("token","token"+username+password);
            return CommonRes.create(hs);
        }else{
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户名密码错误");
        }
    }

    @RequestMapping(value = "/test",method = RequestMethod.GET)
    @ResponseBody
    public String test(@RequestHeader Map<String,Object> he) throws JsonProcessingException {
        System.out.println(he);
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        return objectMapper.writeValueAsString(hs);
    }

    private String encodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确认计算方法MD5
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        return base64Encoder.encode(messageDigest.digest(str.getBytes("utf-8")));

    }
}