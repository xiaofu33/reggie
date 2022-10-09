package com.shuke.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuke.reggie.common.CustomException;
import com.shuke.reggie.entity.Category;
import com.shuke.reggie.entity.Dish;
import com.shuke.reggie.entity.Setmeal;
import com.shuke.reggie.mapper.CategoryMapper;
import com.shuke.reggie.service.CategoryService;
import com.shuke.reggie.service.DishService;
import com.shuke.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;


    /**
     * 根据 id 删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件，根据分类 id 进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        // 查询当前分类是否关联菜品，若已关联抛出异常
        if(count1 > 0){ // 已关联
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        // 查询当前分类是否关联套餐，若已关联抛出异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);

        if(count2 > 0){
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        // 未关联，正常删除
        super.removeById(id);
    }
}
