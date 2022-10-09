package com.shuke.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuke.reggie.common.CustomException;
import com.shuke.reggie.dto.SetmealDto;
import com.shuke.reggie.entity.Setmeal;
import com.shuke.reggie.entity.SetmealDish;
import com.shuke.reggie.mapper.SetmealMapper;
import com.shuke.reggie.service.SetmealDishService;
import com.shuke.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private  SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时保持与菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐 setmeal 的基本信息
        this.save(setmealDto);
        // 保存套餐与菜品 setmealDish 的关联信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐及其关联菜品
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态，确认是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);
        if(count > 0){
            throw  new CustomException("套餐正在售卖中,不能删除");
        }
        // 删除套餐表中对应数据
        this.removeByIds(ids);

        // 删除关联表中的菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper1);

    }
}
