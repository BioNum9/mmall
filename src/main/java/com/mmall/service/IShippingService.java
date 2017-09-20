package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

/**
 * Created by lovea on 2017/7/20.
 */
public interface IShippingService {

    ServerResponse add(Integer userId, Shipping shipping);

    ServerResponse delete(Integer userId,Integer shippingId);

    ServerResponse update(Integer userId,Shipping shipping);

    ServerResponse select(Integer userId,Integer shippingId);

    ServerResponse<PageInfo> list(Integer userId, int pageNumber, int pageSize);
}
