package com.imooc.dianping.service;

import com.imooc.dianping.common.BusinessException;
import com.imooc.dianping.config.interfaces.InterceptAnnotation;
import com.imooc.dianping.model.Shop;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: Administrator
 * @date: 2020-08-13 14:25
 */
public interface ShopService {
    @InterceptAnnotation(flag = true)
    Shop create(Shop shop) throws BusinessException;

    Shop get(BigDecimal longitude,BigDecimal latitude, Integer id);

    Shop get(Integer id);

    List<Shop> selectAll();

    List<Shop> recommend(BigDecimal longitude,BigDecimal latitude);

    List<Map<String,Object>> searchGroupByTags(String keyword, Integer categoryId, String tags);



    Integer countAllShop();

    List<Shop> search(BigDecimal longitude, BigDecimal latitude,
                      String keyword, Integer orderby, Integer categoryId, String tags);

    public Map<String,Object> searchES(BigDecimal longitude, BigDecimal latitude,
                                       String keyword, Integer orderby, Integer categoryId, String tags) throws IOException;

}
