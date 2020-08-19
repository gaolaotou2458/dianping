package com.imooc.dianping.service.impl;

import com.imooc.dianping.common.BusinessException;
import com.imooc.dianping.common.EmBusinessError;
import com.imooc.dianping.mapper.CategoryMapper;
import com.imooc.dianping.mapper.SellerMapper;
import com.imooc.dianping.mapper.ShopMapper;
import com.imooc.dianping.model.Category;
import com.imooc.dianping.model.Seller;
import com.imooc.dianping.model.Shop;
import com.imooc.dianping.service.CategoryService;
import com.imooc.dianping.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: Administrator
 * @date: 2020-08-13 14:27
 */
@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @Transactional
    public Shop create(Shop shop) throws BusinessException {
        shop.setCreatedAt(new Date());
        shop.setUpdatedAt(new Date());

        //校验商家是否存在正确
        Seller sellerModel = sellerMapper.selectByPrimaryKey(shop.getSellerId());
        if(sellerModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商户不存在");
        }

        if(sellerModel.getDisabledFlag().intValue() == 1){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商户已禁用");
        }

        //校验类目
        Category categoryModel = categoryMapper.selectByPrimaryKey(shop.getCategoryId());
        if(categoryModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"类目不存在");
        }
        shopMapper.insertSelective(shop);

        return get(shop.getId());
    }

    @Override
    public Shop get(Integer id) {
        return shopMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Shop> selectAll() {
        List<Shop> shopModelList = shopMapper.selectAll();
        shopModelList.forEach(shopModel -> {
            shopModel.setSeller(sellerMapper.selectByPrimaryKey(shopModel.getSellerId()));
            shopModel.setCategory(categoryMapper.selectByPrimaryKey(shopModel.getCategoryId()));
        });
        return shopModelList;
    }

    @Override
    public List<Shop> recommend(BigDecimal longitude, BigDecimal latitude) {
        List<Shop> shopModelList = shopMapper.recommend(longitude, latitude);
        shopModelList.forEach(shopModel -> {
            shopModel.setSeller(sellerMapper.selectByPrimaryKey(shopModel.getSellerId()));
            shopModel.setCategory(categoryMapper.selectByPrimaryKey(shopModel.getCategoryId()));
        });
        return shopModelList;
    }

    @Override
    public List<Map<String, Object>> searchGroupByTags(String keyword, Integer categoryId, String tags) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        Example example = new Example(Shop.class);
        Example.Criteria criteria = example.createCriteria();
        String searchKey = "";
        if(StringUtils.isNotBlank(keyword)){
            searchKey = "%" + keyword + "%";
            criteria.andLike("name", searchKey);
        }
        if(categoryId != null){
            criteria.andEqualTo("categoryId", categoryId);

        }
        if(StringUtils.isNotBlank(tags)){
            criteria.andEqualTo("tags", tags);
        }
        List<Shop> shops = shopMapper.selectByExample(example);

        //根据tags分组
        Map<String, List<Shop>> dateListMap = shops.stream()
                .collect(Collectors.groupingBy(Shop::getTags));

        //System.out.println(dateListMap);
        // 遍历map,求tag对应记录的条数

        for (Map.Entry<String, List<Shop>> detailEntry:dateListMap.entrySet()){
            HashMap<String, Object> resMap = new HashMap<>(128);
            String tag = detailEntry.getKey();
            int daySize = detailEntry.getValue().size();
            resMap.put("tags",tag);
            resMap.put("num",daySize);
            resultList.add(resMap);
        }
        System.out.println(resultList);
        return resultList;
    }

    @Override
    public Integer countAllShop() {
        return shopMapper.selectCount(new Shop());
    }

    @Override
    public List<Shop> search(BigDecimal longitude, BigDecimal latitude, String keyword, Integer orderby, Integer categoryId, String tags) {

        List<Shop> shopModelList = shopMapper.search(longitude,latitude,keyword,orderby,categoryId,tags);
        shopModelList.forEach(shopModel -> {
            shopModel.setSeller(sellerMapper.selectByPrimaryKey(shopModel.getSellerId()));
            shopModel.setCategory(categoryMapper.selectByPrimaryKey(shopModel.getCategoryId()));
        });
        return shopModelList;
    }
}
