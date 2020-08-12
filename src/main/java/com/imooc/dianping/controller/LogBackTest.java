package com.imooc.dianping.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogBackTest {
    //注意Logger和LoggerFactory类都是org.slf4j包里的。
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/log")
    public String testLog(){
        logger.debug("this is debug level");
        logger.info("this is info level");
        logger.error("this is error level");
        logger.warn("this is warn level");
        return "sussecc";
    }
}