package com.mmall.controller.portal;

import com.mmall.common.Constant;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by lovea on 2017/7/18.
 */
@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;


    /**
     * 查询购物车内的信息
     * @param session
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpSession session){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(user.getId());
    }
    /**
     * 购物车添加商品方法
     * @param session
     * @param count
     * @param productId
     * @return
     */
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpSession session, Integer count, Integer productId){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(user.getId(),productId,count);
    }

    /**
     * 购物车更新方法
     * @param session
     * @param count
     * @param productId
     * @return
     */
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpSession session, Integer count, Integer productId){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.update(user.getId(),productId,count);
    }

    /**
     * 删除产品信息
     * @param session
     * @param productIds
     * @return
     */
    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(HttpSession session, String productIds){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProduct(user.getId(),productIds);
    }

    /**
     * 全选
     * @param session
     * @return
     */
    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpSession session){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //如果商品id传null则代表是全部操作
        return iCartService.selectOrUnselect(user.getId(),null,Constant.Cart.CHECKED);
    }

    /**
     * 全不选
     * @param session
     * @return
     */
    @RequestMapping("unselect_all.do")
    @ResponseBody
    public ServerResponse<CartVo> unselectAll(HttpSession session){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(user.getId(),null,Constant.Cart.UN_CHECKED);
    }

    /**
     * 单选
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("select_single.do")
    @ResponseBody
    public ServerResponse<CartVo> select_single(HttpSession session,Integer productId){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(user.getId(),productId,Constant.Cart.CHECKED);
    }

    /**
     * 取消单选
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("unselect_single.do")
    @ResponseBody
    public ServerResponse<CartVo> unselectSingle(HttpSession session,Integer productId){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(user.getId(),productId,Constant.Cart.UN_CHECKED);
    }

    /**
     * 获取购物车内商品信息方法
     * @param session
     * @return
     */
    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if(user == null){
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }

}
