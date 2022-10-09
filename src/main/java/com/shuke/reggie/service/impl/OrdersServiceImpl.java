package com.shuke.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuke.reggie.common.BaseContext;
import com.shuke.reggie.common.CustomException;
import com.shuke.reggie.entity.AddressBook;
import com.shuke.reggie.entity.Orders;
import com.shuke.reggie.entity.ShoppingCart;
import com.shuke.reggie.mapper.OrdersMapper;
import com.shuke.reggie.service.AddressBookService;
import com.shuke.reggie.service.OrdersService;
import com.shuke.reggie.service.ShoppingCartService;
import com.shuke.reggie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Override
    @Transactional
    public void submit(Orders orders) {
        // 获取当前用户 id
        Long userId = BaseContext.getCurrentId();

        // 查询当前用户购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list();

        // 查询用户数据
        userService.getById(userId);

        // 查询地址数
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook == null){
            throw new CustomException("用户地址信息有误，不能下单");
        }

        // 向订单表插入数据，一条数据

        this.save(orders);
        // 向订单明细表插入数据，多条数据


    }
}
