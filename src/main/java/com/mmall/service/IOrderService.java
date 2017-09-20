package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

import java.util.Map;

/**
 * Created by lovea on 2017/7/21.
 */
public interface IOrderService {

    ServerResponse pay(Integer userId, String path, Long orderNumber);

    ServerResponse aliCallback(Map<String,String> params);

    ServerResponse<Boolean> queryOrderPayStatus(Integer userId,Long orderNo);

    ServerResponse createOrder(Integer userId,Integer shippingId);

    ServerResponse<String> cancel(Integer userId,Long orderNo);

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse detail(Integer userId,Long orderNo);

    ServerResponse<PageInfo> list(Integer userId, int pagerNum, int pageSize);

    ServerResponse<PageInfo> manageList(int pageNum,int pageSize);

    ServerResponse<OrderVo> manageDetail(Long orderNo);

    ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize);

    ServerResponse<String> manageSendGoods(Long orderNo);
}