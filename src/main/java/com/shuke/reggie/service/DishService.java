package com.shuke.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shuke.reggie.dto.DishDto;
import com.shuke.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);
}
