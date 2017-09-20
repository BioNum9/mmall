package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Constant;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.util.CommonUtil;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by lovea on 2017/7/23.
 */
@Controller
@RequestMapping("/manage/order")
public class OrderManegeController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;


    /**
     * 获取订单列表
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){

        User user = (User) session.getAttribute(Constant.CURRENT_USER);

        if(!CommonUtil.checkIsLogin(user)){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"请登录后进行操作");
        }

        if(iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.manageList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMsg("无权限操作");
        }

    }

    /**
     * 获取订单详情
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> detail(HttpSession session, Long orderNo){

        User user = (User) session.getAttribute(Constant.CURRENT_USER);

        if(!CommonUtil.checkIsLogin(user)){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"请登录后进行操作");
        }

        if(iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.manageDetail(orderNo);
        }else{
            return ServerResponse.createByErrorMsg("无权限操作");
        }

    }

    /**
     * 搜索订单
     * @param session
     * @param orderNo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(HttpSession session, Long orderNo,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){

        User user = (User) session.getAttribute(Constant.CURRENT_USER);

        if(!CommonUtil.checkIsLogin(user)){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"请登录后进行操作");
        }

        if(iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.manageSearch(orderNo,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMsg("无权限操作");
        }

    }

    /**
     * 订单发货
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse<String> sendGoods(HttpSession session, Long orderNo){

        User user = (User) session.getAttribute(Constant.CURRENT_USER);

        if(!CommonUtil.checkIsLogin(user)){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"请登录后进行操作");
        }

        if(iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.manageSendGoods(orderNo);
        }else{
            return ServerResponse.createByErrorMsg("无权限操作");
        }

    }


}
