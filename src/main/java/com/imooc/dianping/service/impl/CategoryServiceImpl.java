package com.imooc.dianping.service.impl;

import com.imooc.dianping.common.BusinessException;
import com.imooc.dianping.common.EmBusinessError;
import com.imooc.dianping.mapper.CategoryMapper;
import com.imooc.dianping.model.Category;
import com.imooc.dianping.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: Administrator
 * @date: 2020-08-13 14:27
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper CategoryMapper;

    @Override
    @Transactional
    public Category create(Category category) throws BusinessException {
        category.setCreatedAt(new Date());
        category.setUpdatedAt(new Date());
        try {
            CategoryMapper.insert(category);
        } catch (DuplicateKeyException ex) {
            throw new BusinessException(EmBusinessError.CATEGORY_NAME_DUPLICATED);
        }
        return CategoryMapper.selectByPrimaryKey(category.getId());
    }

    @Override
    public Category get(Integer id) {
        return CategoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Category> selectAll() {
        List<Category> categories = CategoryMapper.selectAll();
        Example example = new Example(Category.class);

        example.orderBy("sort").desc();
        example.orderBy("id").asc();
        List<Category> categories1 = CategoryMapper.selectByExample(example);
        return categories1;
    }
}
