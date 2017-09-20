package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

/**
 * Created by lovea on 2017/7/16.
 */
public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse<String> updateSaleStatus(Integer productID,Integer status);

    ServerResponse<ProductDetailVo> manageGetProductDetail(Integer productID);

    ServerResponse<PageInfo> manageGetProductList(int pageNumber, int pageSize);

    ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNumber,int pageSize);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductListByKeyword(String keyword,Integer categoryId,int pageNumber,int pageSize,String orderBy);
}
