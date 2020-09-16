package com.imooc.dianping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(scanBasePackages = {"com.imooc.dianping"})
@MapperScan("com.imooc.dianping.mapper")
@PropertySource("classpath:jdbc.properties")
//启动task定时器
@EnableScheduling
public class DianpingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DianpingApplication.class, args);
    }

}
