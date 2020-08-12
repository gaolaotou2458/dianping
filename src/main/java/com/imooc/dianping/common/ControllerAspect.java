package com.imooc.dianping.common;

import com.imooc.dianping.controller.admin.AdminController;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Aspect
@Configuration
public class ControllerAspect {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private HttpServletResponse httpServletResponse;

    //切com.imooc.dianping.controller.admin 下所有方法且该方法被打上RequestMapping 标签
    @Around("execution(* com.imooc.dianping.controller.admin.*.*(..)) && (@annotation(org.springframework.web.bind.annotation.RequestMapping) ||  @annotation(org.springframework.web.bind.annotation.PostMapping))")
    public Object adminControllerBeforeValidation(ProceedingJoinPoint joinPoint) throws Throwable {
        // 拿到执行的方法
        Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
        //拿 @AdminPermission注解
        AdminPermission adminPermission = method.getAnnotation(AdminPermission.class);
        if(adminPermission == null){
            //公共方法
            Object resultObject = joinPoint.proceed();
            return resultObject;
        }
        //判断当前管理员是否登录
        String email = (String) httpServletRequest.getSession().getAttribute(AdminController.CURRENT_ADMIN_SESSION);
        if(email == null){
            if(adminPermission.produceType().equals("text/html")){
                httpServletResponse.sendRedirect("/admin/admin/loginpage");
                return null;
            }else{
                // 如果是json请求等
                CommonError commonError= new CommonError(EmBusinessError.ADMIN_SHOULD_LOGIN);
                return CommonRes.create(commonError,"fail");
            }

        }else{
            // 放行
            Object resultObject = joinPoint.proceed();
            return resultObject;
        }
    }
}
