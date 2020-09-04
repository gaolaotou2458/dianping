package com.imooc.dianping.intercept;
import com.alibaba.fastjson.JSON;
import com.imooc.dianping.config.interfaces.InterceptAnnotation;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;


@Component
@Intercepts({
        @Signature(
                type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class
        })
})

public class MySqlInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {


        // 方法一
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
        //先拦截到RoutingStatementHandler，里面有个StatementHandler类型的delegate变量，其实现类是BaseStatementHandler，然后就到BaseStatementHandler的成员变量mappedStatement
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        //id为执行的mapper方法的全路径名，如com.uv.dao.UserMapper.insertUser
        String id = mappedStatement.getId();
        //sql语句类型 select、delete、insert、update
        String sqlCommandType = mappedStatement.getSqlCommandType().toString();
        if(StringUtils.equals("select",sqlCommandType.toLowerCase())) {
            return invocation.proceed();
        }
        BoundSql boundSql = statementHandler.getBoundSql();
        //这个ParameterMapping表示当前SQL绑定的是哪些参数,及参数类型,但并不是参数本身
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        //将参数值转成json字符串
        String parameterObjects = JSON.toJSONString(boundSql.getParameterObject());

        //获取到原始sql语句
        String sql = boundSql.getSql();

        String mSql = sql;
        //TODO 修改位置

        //存放的是SQL的参数[它是一个实例对象]
        Object[] args = invocation.getArgs();
        Object parameterObject = args[1];
        Object target = invocation.getTarget();

        //注解逻辑判断  添加注解了才拦截
        Class<?> classType = Class.forName(mappedStatement.getId().substring(0, mappedStatement.getId().lastIndexOf(".")));
        String mName = mappedStatement.getId().substring(mappedStatement.getId().lastIndexOf(".") + 1, mappedStatement.getId().length());
        for (Method method : classType.getDeclaredMethods()) {
            //通过自定义注解来判断是否拦截
            if (method.isAnnotationPresent(InterceptAnnotation.class) && mName.equals(method.getName())) {
                InterceptAnnotation interceptorAnnotation = method.getAnnotation(InterceptAnnotation.class);
                if (interceptorAnnotation.flag()) {
                    mSql = sql + " limit 2";
                }
            }
        }
        System.out.println("原始参数" + parameterObjects);
        System.out.println("原始sql:" + sql);
        //通过反射修改sql语句
        Field field = boundSql.getClass().getDeclaredField("sql");
        field.setAccessible(true);
        field.set(boundSql, mSql);
        return invocation.proceed();
    }
    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }
    @Override
    public void setProperties(Properties properties) {
    }
}