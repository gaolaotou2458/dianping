package com.imooc.dianping.service;

import com.imooc.dianping.common.BusinessException;
import com.imooc.dianping.model.User;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface UserService {

    User getUser(Integer id);

    User register(User user) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException;

    User login(String telphone, String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, BusinessException;
}
