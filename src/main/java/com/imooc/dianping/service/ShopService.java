package com.imooc.dianping.service;

import com.imooc.dianping.common.BusinessException;
import com.imooc.dianping.model.Shop;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: Administrator
 * @date: 2020-08-13 14:25
 */
public interface ShopService {

    Shop create(Shop shop) throws BusinessException;

    Shop get(Integer id);

    List<Shop> selectAll();

    List<Shop> recommend(BigDecimal longitude,BigDecimal latitude);

    List<Map<String,Integer>> searchGroupByTags(String keyword, Integer categoryId, String tags);

    Integer countAllShop();

    List<Shop> search(BigDecimal longitude, BigDecimal latitude,
                      String keyword, Integer orderby, Integer categoryId, String tags);

}
