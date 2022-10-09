package com.shuke.reggie.dto;

import com.shuke.reggie.entity.Setmeal;
import com.shuke.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
