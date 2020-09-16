package com.imooc.dianping.mapper;

import com.imooc.dianping.model.Shop;
import com.imooc.dianping.utils.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface ShopMapper extends MyMapper<Shop> {

    List<Shop> recommend(@Param("longitude") BigDecimal longitude, @Param("latitude") BigDecimal latitude);

    List<Shop> search(@Param("longitude") BigDecimal longitude,
                           @Param("latitude") BigDecimal latitude,
                           @Param("keyword")String keyword,
                           @Param("orderby")Integer orderby,
                           @Param("categoryId")Integer categoryId,
                           @Param("tags")String tags);

    List<Map<String,Object>> searchGroupByTags(@Param("keyword")String keyword,
                                               @Param("categoryId")Integer categoryId,
                                               @Param("tags")String tags);

    Shop searchById(@Param("longitude") BigDecimal longitude,
                    @Param("latitude") BigDecimal latitude,
                    @Param("id") Integer id);

    //这三个id的数据变更，就去重建跟它关联的索引
    List<Map<String,Object>> buildESQuery(@Param("sellerId") Integer sellerId,
                                          @Param("categoryId") Integer categoryId,
                                          @Param("shopId") Integer shopId);

}