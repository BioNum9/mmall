package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * Created by lovea on 2017/7/16.
 */
public interface ICategoryService {
    ServerResponse addCategory(String categoryName, Integer parentID);

    ServerResponse updateCategoryName(Integer categoryID,String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCaterogy(Integer categoryID);

    ServerResponse<List<Integer>> selectCategoryAndChildrenByID(Integer categoryID);
}
