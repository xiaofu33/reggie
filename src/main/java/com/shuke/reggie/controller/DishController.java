package com.shuke.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shuke.reggie.common.R;
import com.shuke.reggie.dto.DishDto;
import com.shuke.reggie.entity.Category;
import com.shuke.reggie.entity.Dish;
import com.shuke.reggie.entity.DishFlavor;
import com.shuke.reggie.service.CategoryService;
import com.shuke.reggie.service.DishFlavorService;
import com.shuke.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 添加菜品功能
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("菜品添加成功");
    }

    /**
     * 分页展示菜品
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        // 构造分页构造器对象（由于 Dish 缺少 categoryName 字段，所以使用 Dto 解决）
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto>  dishDtoPage = new Page<>(page,pageSize);

        // 条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        // 添加过滤条件（name 存在时）
        queryWrapper.like(name != null,Dish::getName,name);

        // 添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        // 执行分页查询
        dishService.page(pageInfo,queryWrapper);

        // 对象拷贝（records 字段不能直接拷贝）
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        // 通过 records 集合创建新的适用于 DishDto 的集合
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list =  records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            // 根据分类 id 获取分类 name 并存入 DishDto 中
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据 id 查询菜品和口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success((dishDto));
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info("修改菜品");
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }


    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    /*
        @GetMapping("/list")
        public R<List<Dish>> list(Dish dish){
            // 构造查询器
            LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
            // 添加条件：根据 id 和 status
            queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
            queryWrapper.eq(Dish::getStatus,1);
            queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

            List<Dish> list = dishService.list(queryWrapper);

            return R.success(list);

        }
    */

    /**
     * 菜品展示追加口味数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        // 构造查询器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 添加条件：根据 id 和 status
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList =  list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            // 对象拷贝
            BeanUtils.copyProperties(item,dishDto);

            // 根据分类 id 获取分类 name 并存入 DishDto
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            // 根据菜品 id 获取口味 flavor 并存入 DishDto
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> flavors = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(flavors);

            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);

    }



}
