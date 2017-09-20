package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by lovea on 2017/7/17.
 */
@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    /**
     * 前台获取商品信息详情
     * @param productId
     * @return
     */
    @RequestMapping("get_product_detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        return iProductService.getProductDetail(productId);
    }

    /**
     * 前台搜索商品信息列表
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @RequestMapping("get_product_list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getProductList(@RequestParam(value = "keyword",required = false)String keyword,
                                                   @RequestParam(value = "categoryId",required = false) Integer categoryId,
                                                   @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                                   @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                                   @RequestParam(value = "orderBy",defaultValue = "")String orderBy){
        return iProductService.getProductListByKeyword(keyword,categoryId,pageNum,pageSize,orderBy);
    }
}
