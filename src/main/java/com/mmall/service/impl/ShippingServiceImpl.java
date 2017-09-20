package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by lovea on 2017/7/20.
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    /**
     * 新增收货地址
     * @param userId
     * @param shipping
     * @return
     */
    public ServerResponse add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if(rowCount > 0){
            Map result = Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功",result);
        }else{
            return ServerResponse.createByErrorMsg("新建地址失败");
        }
    }

    /**
     * 删除收货地址
     * @param userId
     * @param shippingId
     * @return
     */
    public ServerResponse delete(Integer userId,Integer shippingId){
        int resultCount = shippingMapper.deleteByShippingIdAndUserId(shippingId,userId);
        if(resultCount > 0){
            return ServerResponse.createBySuccessMsg("删除成功");
        }else{
            return ServerResponse.createByErrorMsg("删除失败");
        }

    }

    /**
     * 更新收获地址
     * @param userId
     * @param shipping
     * @return
     */
    public ServerResponse update(Integer userId,Shipping shipping){
        shipping.setUserId(userId);
        int resultCount = shippingMapper.updateByShippingAndUserId(shipping);
        if(resultCount > 0){
            return ServerResponse.createBySuccessMsg("更新成功");
        }else{
            return ServerResponse.createByErrorMsg("更新失败");
        }

    }

    /**
     * 获取收货地址
     * @param userId
     * @param shippingId
     * @return
     */
    public ServerResponse select(Integer userId,Integer shippingId){
        Shipping shipping = shippingMapper.selectByShippingIdAndUserId(shippingId,userId);
        if(shipping == null){
            return ServerResponse.createByErrorMsg("无法查到对应地址");
        }else{
            return ServerResponse.createBySuccess(shipping);
        }
    }

    /**
     * 查找收货地址列表
     * @param userId
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> list(Integer userId,int pageNumber,int pageSize){
        PageHelper.startPage(pageNumber,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
