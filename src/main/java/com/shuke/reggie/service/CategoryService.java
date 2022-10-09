package com.shuke.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shuke.reggie.entity.Category;
import org.springframework.transaction.annotation.Transactional;


public interface CategoryService extends IService<Category> {

    public void remove(Long id);

}
