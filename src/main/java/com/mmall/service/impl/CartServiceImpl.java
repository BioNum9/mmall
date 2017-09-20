package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import com.mmall.common.Constant;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.service.ICategoryService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by lovea on 2017/7/18.
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    /**
     * 购物车添加商品
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count){

        //参数错误，返回参数错误信息
        if(productId == null || count == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        //根据 productId判断商品是否有效
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            //如果没有该商品，返回错误信息
            return  ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

       Cart cart = cartMapper.selectCartByUserIdAndProductId(userId,productId);
        if(cart == null) {
            //这个产品不在购物车
            cart = new Cart();
            cart.setQuantity(count);
            cart.setChecked(Constant.Cart.CHECKED);
            cart.setProductId(productId);
            cart.setUserId(userId);
            cartMapper.insert(cart);
        }else{
            //这个产品已存在,数量叠加
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }

        return this.list(userId);
    }

    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);

        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        BigDecimal cartTotalPrice = new BigDecimal("0");

        if(CollectionUtils.isNotEmpty(cartList)){
            for(Cart cart:cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cart.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cart.getProductId());

                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if(product != null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubTitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount = 0;
                    if(product.getStock() >= cart.getQuantity()){
                        //库存充足
                        buyLimitCount = cart.getQuantity();
                        cartProductVo.setLimitQuantity(Constant.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        //超出库存
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Constant.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cart.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cart.getChecked());

                }
                if(cart.getChecked() == Constant.Cart.CHECKED){
                    //如果已经勾选，增加到购物车整个总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://image.imooc.com/"));
        return cartVo;
    }

    /**
     * 更新购物车信息
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    public ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count){
        //参数错误，返回参数错误信息
        if(productId == null || count == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if(cart != null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userId);
    }

    /**
     * 删除购物车商品信息
     * @param userId
     * @param productIds
     * @return
     */
    public ServerResponse<CartVo> deleteProduct(Integer userId,String productIds){
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productIdList)){
            //参数有误
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }else{
            //参数无误，删除购物车内对应商品
            cartMapper.deleteByUserIdAndProductIds(userId,productIdList);
            return this.list(userId);
        }
    }

    /**
     * 查询购物车内商品信息
     * @param userId
     * @return
     */
    public ServerResponse<CartVo> list(Integer userId){
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    /**
     * 全选或者全不选
     * @param userId
     * @param checked
     * @return
     */
    public ServerResponse<CartVo> selectOrUnselect(Integer userId,Integer productId,Integer checked){
       cartMapper.checkedOrUncheckedProduct(userId,checked,productId);
       return this.list(userId);
    }

    public ServerResponse<Integer> getCartProductCount(Integer userId){
        if(userId == null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }
    /**
     * 判断是否全选
     * @param userId
     * @return
     */
    private boolean getAllCheckedStatus(Integer userId){
        if(userId == null){
            return false;
        }else{
            return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
        }
    }



}
