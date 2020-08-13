package com.imooc.dianping.service;

import com.imooc.dianping.common.BusinessException;
import com.imooc.dianping.model.Category;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: Administrator
 * @date: 2020-08-13 14:25
 */
public interface CategoryService {

    Category create(Category category) throws BusinessException;

    Category get(Integer id);

    List<Category> selectAll();

}
