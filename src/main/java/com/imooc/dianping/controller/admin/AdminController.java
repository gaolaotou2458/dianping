package com.imooc.dianping.controller.admin;

import com.imooc.dianping.common.AdminPermission;
import com.imooc.dianping.common.BusinessException;
import com.imooc.dianping.common.CommonRes;
import com.imooc.dianping.common.EmBusinessError;
import com.imooc.dianping.mapper.CategoryMapper;
import com.imooc.dianping.mapper.SellerMapper;
import com.imooc.dianping.mapper.ShopMapper;
import com.imooc.dianping.mapper.UserMapper;
import com.imooc.dianping.model.Category;
import com.imooc.dianping.model.Seller;
import com.imooc.dianping.model.Shop;
import com.imooc.dianping.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Controller("/admin/admin")
@RequestMapping("/admin/admin")
public class AdminController {

    @Value("${admin.email}")
    private String email;
    @Value("${admin.encrptyPassord}")
    private String encrptyPassord;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private SellerMapper sellerMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private HttpServletRequest httpServletRequest;

    public static final String CURRENT_ADMIN_SESSION = "currentAdminSession";

    @RequestMapping("/index")
    //测试通过非text/html请求
//    @AdminPermission(produceType = "application/json")
//    @ResponseBody
    //测试通过非text/html请求
    @AdminPermission
    public ModelAndView index(){
        ModelAndView modelAndView = new ModelAndView("/admin/admin/index");
        User user = new User();
        modelAndView.addObject("userCount",userMapper.selectCount(user));
        modelAndView.addObject("shopCount",shopMapper.selectCount(new Shop()));
        modelAndView.addObject("categoryCount",categoryMapper.selectCount(new Category()));
        modelAndView.addObject("sellerCount",sellerMapper.selectCount(new Seller()));
        modelAndView.addObject("CONTROLLER_NAME","admin");
        modelAndView.addObject("ACTION_NAME","index");
        return  modelAndView;
    }

    //测试通过非text/html请求
    @AdminPermission(produceType = "application/json")
    @ResponseBody
    @RequestMapping("/index1")
    public CommonRes index1(){
        return CommonRes.create(null);
    }

    @RequestMapping("/loginpage")
    public ModelAndView loginpage(){
        ModelAndView modelAndView = new ModelAndView("/admin/admin/login");
        return  modelAndView;
    }

    @PostMapping("/login")
    public String login(@RequestParam(name ="email") String email,
                              @RequestParam(name ="password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        if(StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户名密码不能为空");
        }
        if(email.equals(this.email) && encodeByMd5(password).equals(this.encrptyPassord)){
            //登录成功
            httpServletRequest.getSession().setAttribute(CURRENT_ADMIN_SESSION,email);
            return "redirect:/admin/admin/index";
        }else{
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户名密码错误");
        }
    }

    private String encodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确认计算方法MD5
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        return base64Encoder.encode(messageDigest.digest(str.getBytes("utf-8")));

    }
}
