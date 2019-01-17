package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Shipping;

public interface IAddressService{

    /**
     * 添加收货地址
     * */
    public ServerResponse add(Integer userId,Shipping shipping);

    /**
     * 删除地址
     **/
    ServerResponse delete(Integer userId,Integer shippingId);

    /**
     * 更新地址
     */
    ServerResponse update(Shipping shipping);

    /**
     * 查看
     * */
    ServerResponse select(Integer shippingId);

    /**
     * 分页查询
     * */
    ServerResponse list(Integer pageNum,Integer pageSize);
}
