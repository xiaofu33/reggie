package com.shuke.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuke.reggie.dto.DishDto;
import com.shuke.reggie.entity.Dish;
import com.shuke.reggie.entity.DishFlavor;
import com.shuke.reggie.mapper.DishMapper;
import com.shuke.reggie.service.DishFlavorService;
import com.shuke.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * 新增菜品，同时保存菜品口味信息
     * @param dishDto
     */
    @Override
    @Transactional // 保持数据一致性，避免异常情况导致只添加了一个表
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        for(DishFlavor flavor : flavors){
            flavor.setDishId(dishId);
        }
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 查询菜品与口味的基本信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 查询菜品基本信息
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        // 查询菜品对应的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 更新菜品与其口味信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新 dish 表基本信息
        this.updateById(dishDto);

        // 清理当前菜品对应的口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        // 添加提交的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        for(DishFlavor flavor : flavors){
            flavor.setDishId(dishDto.getId());
        }

        dishFlavorService.saveBatch(flavors);
    }
}
