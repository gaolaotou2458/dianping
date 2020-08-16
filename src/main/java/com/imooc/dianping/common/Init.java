package com.imooc.dianping.common;

import com.imooc.dianping.model.Shop;
import com.imooc.dianping.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 测试初始化加载的时候运行一些东西.
 */

@Component
public class Init {
    @Autowired
    private ShopService shopService;

    @PostConstruct
    public void init() {
        Shop shop = shopService.get(1);
        System.out.println("==============");
        System.out.println(shop);


    }
}
