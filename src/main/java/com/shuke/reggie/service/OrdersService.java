package com.shuke.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shuke.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {

    public void submit(Orders orders);

}
