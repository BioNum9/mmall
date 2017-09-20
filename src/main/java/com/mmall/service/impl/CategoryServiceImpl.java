package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by lovea on 2017/7/16.
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 增加商品分类
     * @param categoryName
     * @param parentID
     * @return
     */
    public ServerResponse addCategory(String categoryName,Integer parentID){
        if(parentID == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMsg("参数错误，添加失败");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentID);
        category.setStatus(true);

        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccessMsg("添加成功");
        }else{
            return ServerResponse.createByErrorMsg("添加失败");
        }
    }

    /**
     * 更新商品分类
     * @param categoryID
     * @param categoryName
     * @return
     */
    public ServerResponse updateCategoryName(Integer categoryID,String categoryName){
        if(categoryID == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMsg("参数错误，添加失败");
        }

        Category category = new Category();

        category.setId(categoryID);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccessMsg("更新成功");
        }else{
            return ServerResponse.createByErrorMsg("更新失败");
        }

    }

    /**
     * 获取商品子类型列表
     * @param categoryID
     * @return
     */
    public ServerResponse<List<Category>> getChildrenParallelCaterogy(Integer categoryID){
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentID(categoryID);
        if(CollectionUtils.isEmpty(categoryList)){
            logger.info("未找到当前分类子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 递归查询本结点id和子节点ID
     * @param categoryID
     * @return
     */
    public ServerResponse<List<Integer>> selectCategoryAndChildrenByID(Integer categoryID){
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet,categoryID);

        List<Integer>  categoryList = Lists.newArrayList();
        if(categoryID != null){
            for(Category categoryItem : categorySet){
                categoryList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    // 递归算法，算出子节点
   private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryID){
        Category category = categoryMapper.selectByPrimaryKey(categoryID);
       if(category != null){
            categorySet.add(category);
       }
       //查找子节点，递归算法一定要有一个退出条件
       List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentID(categoryID);
       for(Category categoryItem: categoryList){
            findChildCategory(categorySet,categoryItem.getId());
       }
       return categorySet;
   }
}
