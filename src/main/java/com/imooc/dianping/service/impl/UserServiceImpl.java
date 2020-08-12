package com.imooc.dianping.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.dianping.common.BusinessException;
import com.imooc.dianping.common.EmBusinessError;
import com.imooc.dianping.mapper.UserMapper;
import com.imooc.dianping.model.User;
import com.imooc.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Encoder;
import tk.mybatis.mapper.entity.Example;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUser(Integer id) {

        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional
    public User register(User user) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        user.setPassword(encodeByMd5(user.getPassword()));
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException e) {
            System.out.println(e.getMessage());
            throw new BusinessException(EmBusinessError.REGISTER_DUP_FAIL);
        }
        return getUser(user.getId());
    }

    @Override
    public User login(String telphone, String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, BusinessException {
        // PageHelper 使用非常简单，只需要设置页码和每页显示笔数即可
        //PageHelper.startPage(0, 2);

        // 设置分页查询条件
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("telphone" ,telphone);
        criteria.andEqualTo("password" ,encodeByMd5(password));
        User user = userMapper.selectOneByExample(example);
        if(user == null) {
            throw new BusinessException(EmBusinessError.LOGIN_FAIL);
        }
        return user;
    }

    // md5 加密
    private String encodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // 确认计算方法Md5
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String md5Str = base64Encoder.encode(md5.digest(str.getBytes("utf-8")));
        return md5Str;
    }
}
