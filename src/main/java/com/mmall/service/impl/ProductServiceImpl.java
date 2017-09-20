package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Constant;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lovea on 2017/7/16.
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;
    /**
     * 添加或更新商品信息
     * @param product
     * @return
     */
    public ServerResponse saveOrUpdateProduct(Product product){
        if(product != null){

            if(StringUtils.isNotBlank(product.getSubImages())){
                String[] subImageArray = product.getSubImages().split(",");
                if(subImageArray.length > 0){
                    product.setMainImage(subImageArray[0]);
                }
            }

            if(product.getId() != null){
                int rowCount = productMapper.updateByPrimaryKey(product);
                if(rowCount > 0){
                    return ServerResponse.createBySuccessMsg("更新产品成功");
                }else{
                    return ServerResponse.createByErrorMsg("更新产品失败");
                }
            }else{
                int rowCount = productMapper.insert(product);
                if(rowCount > 0){
                    return ServerResponse.createBySuccessMsg("新增产品成功");
                }else{
                    return ServerResponse.createBySuccessMsg("新增产品失败");
                }
            }

        }
        return ServerResponse.createByErrorMsg("新增或更新商品参数不正确");

    }

    /**
     * 更新商品销售状态
     * @param productID
     * @param status
     * @return
     */
    public ServerResponse<String> updateSaleStatus(Integer productID,Integer status){
        if(productID == null || status == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = new Product();

        product.setId(productID);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if(rowCount > 0){
            return ServerResponse.createBySuccessMsg("修改产品销售状态成功");
        }else{
            return ServerResponse.createByErrorMsg("修改产品销售状态失败");
        }
    }

    /**
     * 后台获取商品详情
     * @param productID
     * @return
     */
    public ServerResponse<ProductDetailVo> manageGetProductDetail(Integer productID){
        if(productID == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = productMapper.selectByPrimaryKey(productID);
        if(product == null){
            return ServerResponse.createByErrorMsg("产品已下架或已删除");
        }
        //vo 对象 value object
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    /**
     * 获取商品列表信息
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> manageGetProductList(int pageNumber,int pageSize){
        //startpage
        PageHelper.startPage(pageNumber,pageSize);
        //填充sql查询逻辑
        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem:productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        //pageHelper收尾
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);

        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * 搜索产品信息
     * @param productName
     * @param productId
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNumber,int pageSize){
        PageHelper.startPage(pageNumber,pageSize);
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }

        List<Product> productList = productMapper.selectByNameAndProductId(productName,productId);

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem:productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }

        //pageHelper收尾
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);

        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * 前台获取商品信息详情
     * @param productId
     * @return
     */
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMsg("产品已下架或已删除");
        }
        if(product.getStatus() != Constant.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMsg("产品已下架或已删除");
        }
        //vo 对象 value object
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    /**
     * 前台搜索商品方法
     * @param keyword
     * @param categoryId
     * @param pageNumber
     * @param pageSize
     * @param orderBy
     * @return
     */
    public ServerResponse<PageInfo> getProductListByKeyword(String keyword,Integer categoryId,int pageNumber,int pageSize,String orderBy){
        if(StringUtils.isBlank(keyword) && categoryId == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        List<Integer> categoryIdList = Lists.newArrayList();

        if(categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(keyword)){
                //没有该分类，也没有关键字，返回空的结果集，不报错
                PageHelper.startPage(pageNumber,pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.selectCategoryAndChildrenByID(category.getId()).getData();
        }

        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNumber,pageSize);
        //排序处理
        if(StringUtils.isNotBlank(orderBy)){
            if(Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,
                                                                            categoryIdList.size() == 0?null:categoryIdList);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product:productList
             ) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }


    /**
     * 将product对象转化为productdetailVo对象
     * @param product
     * @return
     */
    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryID(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        //调用配置工具类，设置imageHost
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://image.maroo5mlj.com/"));


        //parentCaterogyID
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryID(Constant.DEFAULT_CATEROGYID);
        }else{
            productDetailVo.setParentCategoryID(category.getParentId());
        }
        //createTime
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        //updateTime
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }

    /**
     * 转化为productListVo
     * @param product
     * @return
     */
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://image.maroo5mlj.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());

        return productListVo;
    }

}
